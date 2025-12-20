package presentation.controller;

import domain.content.ContentItem;
import domain.patient.GroupComparisonResult;
import domain.service.PatientCareService;
import domain.patient.HealthRecord;
import domain.patient.RiskAssessment;
import domain.patient.ComplicationRisk;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;

import java.util.List;

public class PatientController {

    // [수정 1] final로 선언하되, 여기서 바로 new 하지 않음
    private final PatientCareService service;

    // [수정 2] 기본 생성자 (기존 코드 호환용)
    // UI(PatientPanel)에서 new PatientController() 라고 부르면 이쪽이 실행됩니다.
    // 내부에서 스스로 서비스를 생성합니다.
    public PatientController() {
        this.service = new PatientCareService();
    }

    // [수정 3] 생성자 주입 (테스트/확장용)
    // 나중에 테스트할 때: new PatientController(new MockService()) 처럼 가짜를 넣어줄 수 있음.
    // "외부에서 서비스를 주입받는다"는 의존성 주입(DI)의 핵심입니다.
    public PatientController(PatientCareService service) {
        this.service = service;
    }

    // --- 아래 메서드들은 변경 사항 없음 (그대로 사용) ---

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
    public List<GroupComparisonResult> getSimulations(Long patientId) {
        // service는 PatientCareService 객체
        return service.getSimulationResults(patientId);
    }
}