package domain.patient;

import java.time.LocalDateTime;

public class HealthRecord {
    private Long id;
    private Long patientId;

    // [추가] 분석을 정교하게 하기 위한 필드
    private int age;        // 기록 당시 나이
    private String gender;  // 기록 당시 성별 (Male/Female)

    private int systolicBp;
    private int diastolicBp;
    private double bloodSugar;
    private String smoking; // Yes/No
    private String drinking;
    private String activityLevel;
    private String mainRiskFactors; // 쉼표로 구분
    private double bmi;
    private LocalDateTime measuredAt;

    public HealthRecord() {
        this.measuredAt = LocalDateTime.now();
    }


    public void update(int age, String gender, int sys, int dia, double sugar,
                       String smoking, String drinking, String activity,
                       String riskFactors, double height, double weight) {
        this.age = age;
        this.gender = gender;
        this.systolicBp = sys;
        this.diastolicBp = dia;
        this.bloodSugar = sugar;
        this.smoking = smoking;
        this.drinking = drinking;
        this.activityLevel = activity;
        this.mainRiskFactors = riskFactors;

        // BMI 자동 계산
        if (height > 0 && weight > 0) {
            // 1. cm를 m로 변환
            double heightInMeters = height / 100.0;

            // 2. 변환된 m 단위로 BMI 계산
            this.bmi = weight / (heightInMeters * heightInMeters);


        } else {
            this.bmi = 0;
        }
    }

    // Getters & Setters 추가
    public int getAge() { return age; }
    public String getGender() { return gender; }

    // ... 기존 Getters ...
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public int getSystolicBp() { return systolicBp; }
    public int getDiastolicBp() { return diastolicBp; }
    public double getBloodSugar() { return bloodSugar; }
    public String getSmoking() { return smoking; }
    public String getMainRiskFactors() { return mainRiskFactors; }
    public double getBmi() { return bmi; }
    public LocalDateTime getMeasuredAt() { return measuredAt; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getDrinking() {
        return drinking;
    }

    public String getActivityLevel() {
        return activityLevel;
    }
}