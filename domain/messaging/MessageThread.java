
package domain.messaging;

import java.time.LocalDateTime;

public class MessageThread {
    private Long id;
    private Long patientId;
    private Long caregiverId; 
    private Long doctorId;
    private LocalDateTime createdAt;

    public MessageThread() {}

    public void create(Long patientId, Long caregiverId, Long doctorId) {
        this.patientId = patientId;
        this.caregiverId = caregiverId;
        this.doctorId = doctorId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public Long getCaregiverId() { return caregiverId; }
    public Long getDoctorId() { return doctorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
