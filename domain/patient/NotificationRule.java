
package domain.patient;

public class NotificationRule {
    private Long id;
    private Long patientId;
    private String condition;     // 예: "BP_HIGH", "GLUCOSE_HIGH"
    private String action;        // 알림 내용

    public NotificationRule() {}

    public Long getId() {
        return id;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public NotificationRule(Long id, Long patientId, String condition, String action) {
        this.id = id;
        this.patientId = patientId;
        this.condition = condition;
        this.action = action;
    }

    public void configure(Long pid, String condition, String action) {
        this.patientId = pid;
        this.condition = condition;
        this.action = action;
    }

    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
}
