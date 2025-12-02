package domain.medical;

import java.time.LocalDateTime;

public class DoctorNote {
    private Long id;
    private Long doctorId;  // 작성자 (의사)
    private Long patientId; // 대상 (환자)
    private String content; // 내용
    private LocalDateTime createdAt;

    public DoctorNote() {}

    public void write(Long doctorId, Long patientId, String content) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public Long getPatientId() { return patientId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}