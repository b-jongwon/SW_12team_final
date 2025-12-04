package domain.patient;

import java.time.LocalDateTime;

public class PatientAssignment {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long caregiverId;
    private LocalDateTime assignedAt;


    private String status;

    // 생성자 수정
    public PatientAssignment(Long id, Long patientId, Long doctorId, Long caregiverId, LocalDateTime assignedAt, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.caregiverId = caregiverId;
        this.assignedAt = assignedAt;
        this.status = status; // 초기화
    }

    public PatientAssignment() {}


    public void assign(Long patientId, Long doctorId, Long caregiverId) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.caregiverId = caregiverId;
        this.assignedAt = LocalDateTime.now();
        this.status = "ACCEPTED";
    }


    public void requestConnection(Long patientId, Long doctorId, Long caregiverId) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.caregiverId = caregiverId;
        this.assignedAt = LocalDateTime.now();
        this.status = "PENDING";
    }


    public void accept() {
        this.status = "ACCEPTED";
    }

    public void reject() {
        this.status = "REJECTED";
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public Long getDoctorId() { return doctorId; }
    public Long getCaregiverId() { return caregiverId; }
    public LocalDateTime getAssignedAt() { return assignedAt; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}