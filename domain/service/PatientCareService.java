package domain.service;

import data.repository.MedicalRepository;
import domain.patient.HealthRecord;
import domain.patient.RiskAssessment;
import domain.patient.ComplicationRisk;
import domain.patient.RiskConfiguration;

import java.util.List;

public class PatientCareService {

    private final MedicalRepository repo = new MedicalRepository();

    // [수정] 메서드 시그니처(이름, 인자, 반환타입)는 100% 동일하게 유지
    public HealthRecord createHealthRecord(Long patientId,
                                           int sys, int dia, double sugar,
                                           String smoking, String drinking,
                                           String activity, String riskFactors,
                                           double height, double weight) {

        // 1. [Domain] 건강 기록 엔티티 생성 및 데이터 세팅 (기존 로직)
        HealthRecord record = new HealthRecord();
        record.setPatientId(patientId);
        // update 메서드 안에서 BMI 계산 등 도메인 로직 수행됨
        record.update(sys, dia, sugar, smoking, drinking,
                activity, riskFactors, height, weight);

        // 2. [Data] 건강 기록 저장 (기존 로직)
        HealthRecord savedRecord = repo.saveNewRecord(record);

        // =================================================================
        // 3. [Service Logic] "자동 위험도 분석" 오케스트레이션 (새로 추가된 부분)
        // 컨트롤러가 시키지 않아도, 서비스가 알아서 판단하여 위험도를 분석하고 저장함
        // =================================================================
        analyzeAndSaveRisk(patientId, savedRecord);
        analyzeComplication(patientId, savedRecord);
        return savedRecord;
    }

    // [내부 헬퍼 메서드] 서비스 레이어의 분석 로직
    // (외부에는 노출되지 않고 내부적으로만 돕니다)
    private void analyzeAndSaveRisk(Long patientId, HealthRecord r) {
        double score = 0.0;
        StringBuilder reason = new StringBuilder();

        // 간단한 분석 로직 (실제로는 더 복잡한 알고리즘이 들어갈 자리)
        if (r.getSystolicBp() >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD || r.getDiastolicBp() >= RiskConfiguration.BP_DIASTOLIC_THRESHOLD) {
            score += 30.0;
            reason.append("고혈압/ ");
        }
        if (r.getBloodSugar() >= RiskConfiguration.SUGAR_THRESHOLD) {
            score += 20.0;
            reason.append("당뇨(고혈당)/ ");
        }
        if ("Yes".equalsIgnoreCase(r.getSmoking())) {
            score += 15.0;
            reason.append("흡연/ ");
        }
        if (r.getBmi() >= RiskConfiguration.BMI_THRESHOLD) { // HealthRecord 도메인이 계산해둔 BMI 활용
            score += 10.0;
            reason.append("비만/ ");
        }
        if (score == 0) {
            reason.append("정상 범위");
        }

        // 위험 레벨 결정
        String level = "정상";
        if (score >= 50) level = "고위험";
        else if (score >= 30) level = "주의";

        // 4. [Domain & Data] 분석 결과인 RiskAssessment 엔티티 생성 및 저장
        RiskAssessment risk = new RiskAssessment();
        risk.setPatientId(patientId);
        // assess 메서드를 통해 데이터 주입
        risk.assess(score, score, level, reason.toString());

        repo.saveRisk(risk);
    }
    private void analyzeComplication(Long patientId, HealthRecord r) {
        double riskScore = 0;
        // 기존 위험 요인 가중치
        if (r.getSystolicBp() >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD) riskScore += 20;
        if (r.getBloodSugar() >= RiskConfiguration.SUGAR_THRESHOLD) riskScore += 10;

        String level = riskScore >= 50 ? "높음" : (riskScore >= 20 ? "중간" : "낮음");

        ComplicationRisk comp = new ComplicationRisk();
        comp.setPatientId(patientId);
        comp.update("심혈관/뇌졸중", riskScore, "위험도: " + level);
        repo.saveCompRisk(comp);
    }

    // --- 아래 기존 메서드들은 변경 없음 ---
    public List<HealthRecord> getRecords(Long patientId) {
        return repo.findRecordsByPatient(patientId);
    }

    public RiskAssessment createRisk(Long pid, double score, double percent,
                                     String level, String summary) {
        RiskAssessment r = new RiskAssessment();
        r.setPatientId(pid);
        r.assess(score, percent, level, summary);
        return repo.saveRisk(r);
    }

    public List<RiskAssessment> getRisk(Long pid) {
        return repo.findRiskByPatient(pid);
    }

    public ComplicationRisk createCompRisk(Long pid, String type,
                                           double prob, String rec) {
        ComplicationRisk r = new ComplicationRisk();
        r.setPatientId(pid);
        r.update(type, prob, rec);
        return repo.saveCompRisk(r);
    }
    // [NEW] 내 진료 소견 목록 조회
    public List<domain.medical.DoctorNote> getMyNotes(Long patientId) {
        return repo.findNotesByPatient(patientId);
    }

    // [NEW] 내 검사 예약 목록 조회
    public List<domain.medical.ScheduledExam> getMyExams(Long patientId) {
        return repo.findExamsByPatient(patientId);
    }

    public List<ComplicationRisk> getCompRisk(Long pid) {
        return repo.findCompRiskByPatient(pid);
    }
}