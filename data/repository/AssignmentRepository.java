
package data.repository;

import infra.BaseJsonRepository;
import infra.IdGenerator;

import domain.assignment.PatientAssignment;
import domain.assignment.ReminderSetting;
import domain.assignment.NotificationRule;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.stream.Collectors;

public class AssignmentRepository {

    private final BaseJsonRepository<PatientAssignment> assignRepo =
        new BaseJsonRepository<>("data/patient_assignments.json",
            new TypeToken<List<PatientAssignment>>() {}) {};

    private final BaseJsonRepository<ReminderSetting> reminderRepo =
        new BaseJsonRepository<>("data/reminder_settings.json",
            new TypeToken<List<ReminderSetting>>() {}) {};

    private final BaseJsonRepository<NotificationRule> ruleRepo =
        new BaseJsonRepository<>("data/notification_rules.json",
            new TypeToken<List<NotificationRule>>() {}) {};

    // Assignment
    public PatientAssignment saveAssignment(PatientAssignment a) {
        a.setId(IdGenerator.nextId("assignment"));
        assignRepo.save(a);
        return a;
    }

    public List<PatientAssignment> getAssignments(Long pid) {
        return assignRepo.findAll().stream()
            .filter(a -> a.getPatientId().equals(pid))
            .collect(Collectors.toList());
    }
    public List<PatientAssignment> findAssignmentsByDoctor(Long doctorId) {
        return assignRepo.findAll().stream()
                .filter(a -> a.getDoctorId() != null && a.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }
    // Reminder
    public ReminderSetting saveReminder(ReminderSetting r) {
        r.setId(IdGenerator.nextId("reminder"));
        reminderRepo.save(r);
        return r;
    }

    public List<ReminderSetting> getReminders(Long pid) {
        return reminderRepo.findAll().stream()
            .filter(r -> r.getPatientId().equals(pid))
            .collect(Collectors.toList());
    }

    // Rule
    public NotificationRule saveRule(NotificationRule n) {
        n.setId(IdGenerator.nextId("notif_rule"));
        ruleRepo.save(n);
        return n;
    }

    public List<NotificationRule> getRules(Long pid) {
        return ruleRepo.findAll().stream()
            .filter(r -> r.getPatientId().equals(pid))
            .collect(Collectors.toList());
    }
}
