
package domain.medical;

import java.time.LocalDateTime;

public class HealthRecord {
    private Long id;
    private Long patientId;
    private int systolicBp;
    private int diastolicBp;
    private double bloodSugar;
    private String smoking;
    private String drinking;
    private String activityLevel;
    private double bmi;
    private String mainRiskFactors;
    private LocalDateTime measuredAt;

    public HealthRecord() {}

    public void update(int systolicBp, int diastolicBp, double bloodSugar,
                       String smoking, String drinking, String activityLevel,
                       String mainRiskFactors, double height, double weight) {
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.bloodSugar = bloodSugar;
        this.smoking = smoking;
        this.drinking = drinking;
        this.activityLevel = activityLevel;
        this.mainRiskFactors = mainRiskFactors;
        this.measuredAt = LocalDateTime.now();
        this.bmi = (height > 0 ? weight / (height * height) : 0);
    }

    public String summary() {
        return String.format("BP %d/%d, Sugar %.1f, BMI %.1f, Risk: %s",
                systolicBp, diastolicBp, bloodSugar, bmi, mainRiskFactors);
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long pid) { this.patientId = pid; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
