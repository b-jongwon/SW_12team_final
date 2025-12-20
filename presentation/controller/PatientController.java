package presentation.controller;

import domain.content.ContentItem;
import domain.service.PatientCareService;
import domain.patient.HealthRecord;
import domain.patient.RiskAssessment;
import domain.patient.ComplicationRisk;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;

import java.util.List;

public class PatientController {

    private final PatientCareService service = new PatientCareService();

    public HealthRecord addRecord(Long pid, int age, String gender, int sys, int dia, double sugar,
                                  String smoking, String drinking, String activity,
                                  String riskFactors, double height, double weight) {
        // 서비스 호출 시 age, gender 전달
        return service.createHealthRecord(pid, age, gender, sys, dia, sugar, smoking, drinking, activity, riskFactors, height, weight);
    }

    // [필수 추가] UI에서 호출함
    public List<ContentItem> getContents(Long patientId) {
        return service.getRecommendedContents(patientId);
    }

    public List<HealthRecord> getRecords(Long pid) { return service.getRecords(pid); }
    public List<RiskAssessment> getRisk(Long pid) { return service.getRisk(pid); }
    public List<ComplicationRisk> getCompRisk(Long pid) { return service.getCompRisk(pid); }
    public List<DoctorNote> getMyNotes(Long pid) { return service.getMyNotes(pid); }
    public List<ScheduledExam> getMyExams(Long pid) { return service.getMyExams(pid); }

    // (필요 시 유지)
    public RiskAssessment assessRisk(Long pid, double score, double percent, String level, String summary) {
        return service.createRisk(pid, score, percent, level, summary);
    }
    public ComplicationRisk addCompRisk(Long pid, String type, double prob, String rec) {
        return service.createCompRisk(pid, type, prob, rec);
    }
}