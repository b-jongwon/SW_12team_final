package presentation.controller;

import domain.service.DoctorService;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import java.time.LocalDateTime;
import java.util.List;

public class DoctorController {

    private final DoctorService service = new DoctorService();

    // 내 환자 목록 (수락된 사람만)
    public List<DoctorService.PatientSummary> getMyPatients(Long doctorId) {
        return service.getMyPatients(doctorId);
    }

    // [NEW] 대기 중인 요청 목록
    public List<DoctorService.PatientSummary> getPendingRequests(Long doctorId) {
        return service.getPendingRequests(doctorId);
    }

    // [NEW] 요청 처리 (수락/거절)
    public void reply(Long assignmentId, boolean accept) {
        service.replyToRequest(assignmentId, accept);
    }

    public DoctorNote saveNote(Long doctorId, Long patientId, String content) {
        return service.writeNote(doctorId, patientId, content);
    }

    public ScheduledExam scheduleExam(Long doctorId, Long patientId, LocalDateTime date, String description) {
        return service.scheduleExam(doctorId, patientId, date, description);
    }
}