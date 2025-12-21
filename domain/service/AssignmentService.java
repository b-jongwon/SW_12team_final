package domain.service;

import data.repository.AssignmentRepository;
import data.repository.UserRepository;
import domain.patient.PatientAssignment;
import domain.patient.ReminderSetting;
import domain.patient.NotificationRule;
import domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssignmentService {

    private final AssignmentRepository repo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();

    //  초기화 시에도 채팅방 연동을 위해 추가
    private final MessagingService messagingService = new MessagingService();

    // 1. 로그인 ID로 배정 '신청'
    public PatientAssignment requestConnection(Long patientId, String docLoginId, String careLoginId) {
        Long doctorId = null;
        Long caregiverId = null;

        // 1. ID 찾기
        if (docLoginId != null && !docLoginId.isEmpty()) {
            Optional<User> doc = userRepo.findByLoginId(docLoginId);
            if (doc.isPresent() && "DOCTOR".equals(doc.get().getRole())) {
                doctorId = doc.get().getId();
            } else {
                throw new IllegalArgumentException("존재하지 않는 의사 ID입니다: " + docLoginId);
            }
        }
        if (careLoginId != null && !careLoginId.isEmpty()) {
            Optional<User> care = userRepo.findByLoginId(careLoginId);
            if (care.isPresent() && "CAREGIVER".equals(care.get().getRole())) {
                caregiverId = care.get().getId();
            } else {
                throw new IllegalArgumentException("존재하지 않는 보호자 ID입니다: " + careLoginId);
            }
        }

        // 중복 체크 로직
        List<PatientAssignment> myAssignments = repo.getAssignments(patientId);
        for (PatientAssignment a : myAssignments) {
            boolean sameDoc = (doctorId != null && doctorId.equals(a.getDoctorId()));
            boolean sameCare = (caregiverId != null && caregiverId.equals(a.getCaregiverId()));

            if (sameDoc || sameCare) {
                if ("PENDING".equals(a.getStatus())) {
                    throw new IllegalStateException("이미 해당 사용자에게 신청 후 대기 중입니다.");
                }
                if ("ACCEPTED".equals(a.getStatus())) {
                    throw new IllegalStateException("이미 해당 사용자와 연결되어 있습니다.");
                }
                if ("REJECTED".equals(a.getStatus())) {
                    a.setStatus("PENDING");
                    repo.updateAssignment(a);
                    return a;
                }
            }
        }

        // 주치의 유일성 체크
        if (doctorId != null) {
            for (PatientAssignment a : myAssignments) {
                if (a.getDoctorId() != null) {
                    if ("ACCEPTED".equals(a.getStatus())) {
                        throw new IllegalStateException("이미 주치의가 배정되어 있습니다. (1명만 가능)");
                    }
                    if ("PENDING".equals(a.getStatus())) {
                        throw new IllegalStateException("현재 주치의 연결 심사 대기 중입니다.");
                    }
                }
            }
        }

        // 신청 생성
        PatientAssignment request = new PatientAssignment();
        request.requestConnection(patientId, doctorId, caregiverId);
        return repo.saveAssignment(request);
    }

    // 2. 환자 연결 현황 조회
    public List<ConnectionSummary> getConnectionStatus(Long patientId) {
        List<ConnectionSummary> result = new ArrayList<>();
        List<PatientAssignment> list = repo.getAssignments(patientId);

        for (PatientAssignment a : list) {
            String role = "";
            String name = "알수없음";

            final Long finalTargetId;

            if (a.getDoctorId() != null) {
                role = "주치의";
                finalTargetId = a.getDoctorId();
            } else if (a.getCaregiverId() != null) {
                role = "보호자";
                finalTargetId = a.getCaregiverId();
            } else {
                finalTargetId = null;
            }

            if (finalTargetId != null) {
                Optional<User> u = userRepo.findAll().stream()
                        .filter(user -> user.getId().equals(finalTargetId))
                        .findFirst();

                if (u.isPresent()) {
                    name = u.get().getName() + " (" + u.get().getLoginId() + ")";
                }
            }
            result.add(new ConnectionSummary(role, name, a.getStatus()));
        }
        return result;
    }

    //  강제 배정
    public PatientAssignment assignPatient(Long pid, Long doctorId, Long caregiverId) {
        PatientAssignment a = new PatientAssignment();
        // ACCEPTED 상태로 바로 생성됨
        a.assign(pid, doctorId, caregiverId);
        PatientAssignment saved = repo.saveAssignment(a);

        // [NEW] ★★★ 여기서도 채팅방에 의사/보호자를 넣어줘야 합니다!
        messagingService.joinRoom(pid, doctorId, caregiverId);

        return saved;
    }

    // 기존 단순 위임 메서드들
    public List<PatientAssignment> getAssignments(Long pid) { return repo.getAssignments(pid); }
    public ReminderSetting createReminder(Long pid, String type, String rule, String msg) {
        ReminderSetting r = new ReminderSetting(); r.create(pid, type, rule, msg); return repo.saveReminder(r);
    }
    public List<ReminderSetting> getReminders(Long pid) { return repo.getReminders(pid); }
    public NotificationRule createRule(Long pid, String cond, String act) {
        NotificationRule n = new NotificationRule(); n.configure(pid, cond, act); return repo.saveRule(n);
    }
    public List<NotificationRule> getRules(Long pid) { return repo.getRules(pid); }

    public static class ConnectionSummary {
        private String role;
        private String name;
        private String status;

        public ConnectionSummary(String role, String name, String status) {
            this.role = role;
            this.name = name;
            this.status = status;
        }
        public String getRole() { return role; }
        public String getName() { return name; }
        public String getStatus() { return status; }
    }
}