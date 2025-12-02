
package domain.patient;

import java.time.LocalDateTime;



public class RiskAssessment {
    private Long id;
    private Long patientId;
    private double riskScore;
    private String riskLevel;
    private String recommendationSummary;
    private double riskPercent;
    private LocalDateTime assessedAt;


    public RiskAssessment() {
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRecommendationSummary() {
        return recommendationSummary;
    }

    public void setRecommendationSummary(String recommendationSummary) {
        this.recommendationSummary = recommendationSummary;
    }

    public double getRiskPercent() {
        return riskPercent;
    }

    public void setRiskPercent(double riskPercent) {
        this.riskPercent = riskPercent;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }

    public RiskAssessment(Long id, Long patientId, double riskScore, String riskLevel, String recommendationSummary, double riskPercent, LocalDateTime assessedAt) {
        this.id = id;
        this.patientId = patientId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.recommendationSummary = recommendationSummary;
        this.riskPercent = riskPercent;
        this.assessedAt = assessedAt;
    }

    public void assess(double riskScore, double percent, String level, String rec) {
        this.riskScore = riskScore;
        this.riskPercent = percent;
        this.riskLevel = level;
        this.recommendationSummary = rec;
        this.assessedAt = LocalDateTime.now();
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long id) { this.patientId = id; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
