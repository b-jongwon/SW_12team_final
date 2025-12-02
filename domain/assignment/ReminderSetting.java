
package domain.assignment;

public class ReminderSetting {
    private Long id;
    private Long patientId;
    private String reminderType;  // bloodPressure, medication, exercise 등
    private String repeatRule;    // daily, weekly 등
    private String message;

    public ReminderSetting() {}

    public ReminderSetting(Long id, Long patientId, String reminderType, String repeatRule, String message) {
        this.id = id;
        this.patientId = patientId;
        this.reminderType = reminderType;
        this.repeatRule = repeatRule;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getRepeatRule() {
        return repeatRule;
    }

    public void setRepeatRule(String repeatRule) {
        this.repeatRule = repeatRule;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void create(Long pid, String type, String rule, String msg) {
        this.patientId = pid;
        this.reminderType = type;
        this.repeatRule = rule;
        this.message = msg;
    }

    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
}
