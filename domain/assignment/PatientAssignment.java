
package domain.assignment;

import java.time.LocalDateTime;

public class PatientAssignment {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long caregiverId;
    private LocalDateTime assignedAt;

    public PatientAssignment(Long id, Long patientId, Long doctorId, Long caregiverId, LocalDateTime assignedAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.caregiverId = caregiverId;
        this.assignedAt = assignedAt;
    }

    public Long getId() {
        return id;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public void setCaregiverId(Long caregiverId) {
        this.caregiverId = caregiverId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public PatientAssignment() {}

    public void assign(Long patientId, Long doctorId, Long caregiverId) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.caregiverId = caregiverId;
        this.assignedAt = LocalDateTime.now();
    }

    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public Long getDoctorId() { return doctorId; }
    public Long getCaregiverId() { return caregiverId; }
}
