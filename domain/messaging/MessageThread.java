
package domain.messaging;

import java.time.LocalDateTime;

public class MessageThread {
    private Long id;
    private Long patientId;
    private Long caregiverId; 
    private Long doctorId;
    private LocalDateTime createdAt;

    public MessageThread() {}

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setCaregiverId(Long caregiverId) {
        this.caregiverId = caregiverId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MessageThread(Long id, Long patientId, Long caregiverId, Long doctorId, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.caregiverId = caregiverId;
        this.doctorId = doctorId;
        this.createdAt = createdAt;
    }

    public void create(Long patientId) {
        this.patientId = patientId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public Long getCaregiverId() { return caregiverId; }
    public Long getDoctorId() { return doctorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
