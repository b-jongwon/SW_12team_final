package presentation.controller;

import domain.service.DoctorService;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import java.time.LocalDateTime;
import java.util.List;

public class DoctorController {

    private final DoctorService service = new DoctorService();

    // [추가] 담당 환자 목록 조회 요청
    public List<DoctorService.PatientSummary> getMyPatients(Long doctorId) {
        return service.getMyPatients(doctorId);
    }

    public DoctorNote saveNote(Long doctorId, Long patientId, String content) {
        return service.writeNote(doctorId, patientId, content);
    }

    public ScheduledExam scheduleExam(Long doctorId, Long patientId, LocalDateTime date, String description) {
        return service.scheduleExam(doctorId, patientId, date, description);
    }
}