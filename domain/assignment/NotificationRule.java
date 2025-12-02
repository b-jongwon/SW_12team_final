
package domain.assignment;

public class NotificationRule {
    private Long id;
    private Long patientId;
    private String condition;     // 예: "BP_HIGH", "GLUCOSE_HIGH"
    private String action;        // 알림 내용

    public NotificationRule() {}

    public void configure(Long pid, String condition, String action) {
        this.patientId = pid;
        this.condition = condition;
        this.action = action;
    }

    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
}
