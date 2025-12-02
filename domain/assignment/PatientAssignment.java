
package domain.assignment;

import java.time.LocalDateTime;

public class PatientAssignment {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long caregiverId;
    private LocalDateTime assignedAt;

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
