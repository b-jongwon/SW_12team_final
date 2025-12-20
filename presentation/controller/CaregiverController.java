package presentation.controller;

import domain.service.CaregiverService;
import domain.patient.HealthRecord;
import domain.medical.DoctorNote;
import domain.user.Patient;

import java.util.List;

public class CaregiverController {

    private final CaregiverService service = new CaregiverService();

    public List<CaregiverService.FamilySummary> getMyFamily(Long caregiverId) {
        return service.getMyFamily(caregiverId);
    }

    // [NEW]
    public List<CaregiverService.FamilySummary> getPendingRequests(Long caregiverId) {
        return service.getPendingRequests(caregiverId);
    }

    // [NEW]
    public void reply(Long assignmentId, boolean accept) {
        service.replyToRequest(assignmentId, accept);
    }
    // 특정 환자의 건강 기록 조회
    public List<HealthRecord> getPatientRecords(Long patientId) {
        return service.getPatientHealthRecords(patientId);
    }

    // 특정 환자의 의사 소견 조회
    public List<DoctorNote> getPatientNotes(Long patientId) {
        return service.getPatientDoctorNotes(patientId);
    }


}