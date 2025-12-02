package domain.medical;

import java.time.LocalDateTime;

public class ScheduledExam {
    private Long id;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime examDate; // 예약 일시
    private String description;     // 검사 내용 (예: 혈액검사, MRI)
    private String status;          // "SCHEDULED", "COMPLETED", "CANCELED"

    public ScheduledExam() {}

    public void schedule(Long doctorId, Long patientId, LocalDateTime date, String desc) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.examDate = date;
        this.description = desc;
        this.status = "SCHEDULED";
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDoctorId() { return doctorId; }
    public Long getPatientId() { return patientId; }
    public LocalDateTime getExamDate() { return examDate; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}