package presentation.controller;

import domain.service.AssignmentService;
import domain.patient.PatientAssignment; // 패키지 변경 반영
import domain.patient.ReminderSetting;
import domain.patient.NotificationRule;

import java.util.List;

public class AssignmentController {

    private final AssignmentService service = new AssignmentService();

    // [NEW] 문자열 ID로 배정 요청
    public PatientAssignment connectDoctorAndCaregiver(Long patientId, String docLoginId, String careLoginId) {
        return service.assignByLoginId(patientId, docLoginId, careLoginId);
    }

    // --- 기존 메서드 ---
    public PatientAssignment assign(Long pid, Long doctorId, Long caregiverId) {
        return service.assignPatient(pid, doctorId, caregiverId);
    }

    public List<PatientAssignment> getAssignments(Long pid) {
        return service.getAssignments(pid);
    }

    public ReminderSetting createReminder(Long pid, String type, String rule, String msg) {
        return service.createReminder(pid, type, rule, msg);
    }

    public List<ReminderSetting> getReminders(Long pid) {
        return service.getReminders(pid);
    }

    public NotificationRule createRule(Long pid, String cond, String act) {
        return service.createRule(pid, cond, act);
    }

    public List<NotificationRule> getRules(Long pid) {
        return service.getRules(pid);
    }
}