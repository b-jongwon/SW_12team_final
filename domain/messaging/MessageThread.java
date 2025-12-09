package domain.messaging;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageThread {
    private Long id;
    private Long patientId;
    private Long doctorId;

    // [중요] 간병인을 리스트로 변경
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

    // [중요] 간병인 추가 메서드
    public void addCaregiver(Long caregiverId) {
        if (!caregiverIds.contains(caregiverId)) {
            caregiverIds.add(caregiverId);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public Long getDoctorId() { return doctorId; }

    // [중요] getter가 List<Long>을 반환해야 함
    public List<Long> getCaregiverIds() { return caregiverIds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}