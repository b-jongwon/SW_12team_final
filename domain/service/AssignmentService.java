
package domain.service;

import data.repository.AssignmentRepository;

import domain.patient.PatientAssignment;
import domain.patient.ReminderSetting;
import domain.assignment.NotificationRule;

import java.util.List;

public class AssignmentService {

    private final AssignmentRepository repo = new AssignmentRepository();

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
