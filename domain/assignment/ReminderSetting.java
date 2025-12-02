
package domain.assignment;

public class ReminderSetting {
    private Long id;
    private Long patientId;
    private String reminderType;  // bloodPressure, medication, exercise 등
    private String repeatRule;    // daily, weekly 등
    private String message;

    public ReminderSetting() {}

    public void create(Long pid, String type, String rule, String msg) {
        this.patientId = pid;
        this.reminderType = type;
        this.repeatRule = rule;
        this.message = msg;
    }

    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
}
