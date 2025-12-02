
package domain.medical;

import java.time.LocalDateTime;

public class ComplicationRisk {
    private Long id;
    private Long patientId;
    private String complicationType;
    private double probability;
    private String recommendation;
    private LocalDateTime assessedAt;

    public void update(String type, double prob, String rec) {
        this.complicationType = type;
        this.probability = prob;
        this.recommendation = rec;
        this.assessedAt = LocalDateTime.now();
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long pid) { this.patientId = pid; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
