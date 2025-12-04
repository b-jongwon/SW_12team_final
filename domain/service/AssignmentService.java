package domain.service;

import data.repository.AssignmentRepository;
import data.repository.UserRepository;
import domain.patient.PatientAssignment;
import domain.patient.ReminderSetting;
import domain.patient.NotificationRule;
import domain.user.User;

import java.util.List;
import java.util.Optional;

public class AssignmentService {

    private final AssignmentRepository repo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository();

    // =================================================================
    // [수정] 로그인 ID로 배정 '신청' (중복 방지 로직 추가됨)
    // =================================================================
    public PatientAssignment requestConnection(Long patientId, String docLoginId, String careLoginId) {
        Long doctorId = null;
        Long caregiverId = null;

        // 1. ID 찾기 (기존과 동일)
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

        // -------------------------------------------------------------
        // 2. [NEW] 중복 검사 로직
        // -------------------------------------------------------------
        // 해당 환자의 기존 배정 기록을 모두 가져옵니다.
        List<PatientAssignment> existingList = repo.getAssignments(patientId);

        for (PatientAssignment a : existingList) {
            boolean sameDoctor = (doctorId != null && doctorId.equals(a.getDoctorId()));
            boolean sameCaregiver = (caregiverId != null && caregiverId.equals(a.getCaregiverId()));

            // 의사나 보호자 중 하나라도 이미 연결되어 있거나 신청 중이라면
            if (sameDoctor || sameCaregiver) {
                if ("ACCEPTED".equals(a.getStatus())) {
                    throw new IllegalStateException("이미 연결된 사용자입니다.");
                } else if ("PENDING".equals(a.getStatus())) {
                    throw new IllegalStateException("이미 연결 신청 대기 중입니다.");
                } else if ("REJECTED".equals(a.getStatus())) {
                    // 거절당했던 기록이 있으면, 다시 'PENDING'으로 상태만 바꿔서 재신청 처리
                    a.setStatus("PENDING");
                    return repo.saveAssignment(a); // 업데이트 후 리턴
                }
            }
        }

        // 3. 중복이 없으면 새로 생성 (PENDING)
        PatientAssignment request = new PatientAssignment();
        request.requestConnection(patientId, doctorId, caregiverId);

        return repo.saveAssignment(request);
    }

    // [NEW] 신청 수락/거절 처리
    public void processRequest(Long assignmentId, boolean isAccepted) {
        // 리포지토리에서 해당 배정 건을 찾아서 상태 변경 (이 기능은 Repository에 findById가 필요함)
        // 일단 구조만 잡아두고 다음 단계에서 구현
    }

    // --- 기존 메서드 (테스트 데이터 생성용, 바로 수락) ---
    public PatientAssignment assignPatient(Long pid, Long doctorId, Long caregiverId) {
        PatientAssignment a = new PatientAssignment();
        a.assign(pid, doctorId, caregiverId); // ACCEPTED 상태
        return repo.saveAssignment(a);
    }

    // ... (나머지 getAssignments 등 기존 메서드 유지) ...
    public List<PatientAssignment> getAssignments(Long pid) {
        return repo.getAssignments(pid);
    }
    public ReminderSetting createReminder(Long pid, String type, String rule, String msg) {
        ReminderSetting r = new ReminderSetting();
        r.create(pid, type, rule, msg);
        return repo.saveReminder(r);
    }
    public List<ReminderSetting> getReminders(Long pid) {
        return repo.getReminders(pid);
    }
    public NotificationRule createRule(Long pid, String cond, String act) {
        NotificationRule n = new NotificationRule();
        n.configure(pid, cond, act);
        return repo.saveRule(n);
    }
    public List<NotificationRule> getRules(Long pid) {
        return repo.getRules(pid);
    }
}