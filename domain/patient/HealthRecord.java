
package domain.patient;

import java.time.LocalDateTime;

public class HealthRecord {
    private Long id;
    private Long patientId;
    private int systolicBp;
    private int diastolicBp;
    private double bloodSugar;
    private String smoking;
    private String drinking;

    public int getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(int systolicBp) {
        this.systolicBp = systolicBp;
    }

    public int getDiastolicBp() {
        return diastolicBp;
    }

    public void setDiastolicBp(int diastolicBp) {
        this.diastolicBp = diastolicBp;
    }

    public double getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(double bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getDrinking() {
        return drinking;
    }

    public void setDrinking(String drinking) {
        this.drinking = drinking;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getMainRiskFactors() {
        return mainRiskFactors;
    }

    public void setMainRiskFactors(String mainRiskFactors) {
        this.mainRiskFactors = mainRiskFactors;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    public HealthRecord(Long id, Long patientId, int systolicBp, int diastolicBp, double bloodSugar, String smoking, String drinking, String activityLevel, double bmi, String mainRiskFactors, LocalDateTime measuredAt) {
        this.id = id;
        this.patientId = patientId;
        this.systolicBp = systolicBp;
        this.diastolicBp = diastolicBp;
        this.bloodSugar = bloodSugar;
        this.smoking = smoking;
        this.drinking = drinking;
        this.activityLevel = activityLevel;
        this.bmi = bmi;
        this.mainRiskFactors = mainRiskFactors;
        this.measuredAt = measuredAt;
    }

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
