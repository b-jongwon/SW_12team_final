
package domain.medical;

import java.time.LocalDateTime;

public class RiskAssessment {
    private Long id;
    private Long patientId;
    private double riskScore;
    private String riskLevel;
    private String recommendationSummary;
    private double riskPercent;
    private LocalDateTime assessedAt;

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
