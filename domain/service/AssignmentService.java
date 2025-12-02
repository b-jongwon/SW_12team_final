package domain.service;

import data.repository.AssignmentRepository;
import data.repository.UserRepository; // [추가] 사용자 조회를 위해 필요
import domain.patient.PatientAssignment; // [패키지 변경 반영]
import domain.patient.ReminderSetting;
import domain.patient.NotificationRule;
import domain.user.User;

import java.util.List;
import java.util.Optional;

public class AssignmentService {

    private final AssignmentRepository repo = new AssignmentRepository();
    private final UserRepository userRepo = new UserRepository(); // [추가]

    // =================================================================
    // [NEW] 로그인 ID(String)를 이용한 편리한 배정 메서드
    // =================================================================
    public PatientAssignment assignByLoginId(Long patientId, String docLoginId, String careLoginId) {
        Long doctorId = null;
        Long caregiverId = null;

        // 1. 의사 ID 찾기
        if (docLoginId != null && !docLoginId.isEmpty()) {
            Optional<User> doc = userRepo.findByLoginId(docLoginId);
            if (doc.isPresent() && "DOCTOR".equals(doc.get().getRole())) {
                doctorId = doc.get().getId();
            } else {
                throw new IllegalArgumentException("존재하지 않는 의사 ID입니다: " + docLoginId);
            }
        }

        // 2. 간병인 ID 찾기
        if (careLoginId != null && !careLoginId.isEmpty()) {
            Optional<User> care = userRepo.findByLoginId(careLoginId);
            if (care.isPresent() && "CAREGIVER".equals(care.get().getRole())) {
                caregiverId = care.get().getId();
            } else {
                throw new IllegalArgumentException("존재하지 않는 보호자 ID입니다: " + careLoginId);
            }
        }

        // 3. 배정 실행 (기존 메서드 재활용)
        // (기존 배정이 있으면 업데이트하거나 새로 만드는 로직이 필요하지만, 여기선 단순 추가)
        return assignPatient(patientId, doctorId, caregiverId);
    }

    // --- 기존 메서드들 ---
    public PatientAssignment assignPatient(Long pid, Long doctorId, Long caregiverId) {
        PatientAssignment a = new PatientAssignment();
        a.assign(pid, doctorId, caregiverId);
        return repo.saveAssignment(a);
    }

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