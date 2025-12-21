package domain.messaging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageThread {
    private Long id;
    private Long patientId;
    private Long doctorId;

    private List<Long> caregiverIds = new ArrayList<>();

    private LocalDateTime createdAt;

    public MessageThread() {}

    public void create(Long patientId) {
        this.patientId = patientId;
        this.createdAt = LocalDateTime.now();
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }


    public void addCaregiver(Long caregiverId) {
        if (!caregiverIds.contains(caregiverId)) {
            caregiverIds.add(caregiverId);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public Long getDoctorId() { return doctorId; }


    public List<Long> getCaregiverIds() { return caregiverIds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}