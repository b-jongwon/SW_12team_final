package domain.patient;

import java.time.LocalDateTime;

public class ComplicationRisk {
    private Long id;
    private Long patientId;
    private String complicationType;
    private double probability;
    private String recommendation;
    private LocalDateTime assessedAt;

    public ComplicationRisk() {}

    public void update(String type, double prob, String rec) {
        this.complicationType = type;
        this.probability = prob;
        this.recommendation = rec;
        this.assessedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getComplicationType() { return complicationType; }
    public void setComplicationType(String complicationType) { this.complicationType = complicationType; }

    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }
}
