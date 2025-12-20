package domain.service;

import data.repository.AssignmentRepository;
import data.repository.MedicalRepository;
import data.repository.MessagingRepository;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import domain.patient.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientCareService {

    private final MedicalRepository repo = new MedicalRepository();
    private final AssignmentRepository assignRepo = new AssignmentRepository();
    private final MessagingRepository msgRepo = new MessagingRepository();

    public HealthRecord createHealthRecord(Long patientId,
                                           int sys, int dia, double sugar,
                                           String smoking, String drinking,
                                           String activity, String riskFactors,
                                           double height, double weight) {

        // 1. 건강 기록 저장
        HealthRecord record = new HealthRecord();
        record.setPatientId(patientId);
        record.update(sys, dia, sugar, smoking, drinking,
                activity, riskFactors, height, weight);
        HealthRecord savedRecord = repo.saveNewRecord(record);

        // 2. 위험도 분석 로직
        double score = 0.0;
        StringBuilder reason = new StringBuilder();

        if (sys >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD || dia >= RiskConfiguration.BP_DIASTOLIC_THRESHOLD) {
            score += 40.0; reason.append("고혈압 ");
        }
        if (sugar >= RiskConfiguration.SUGAR_THRESHOLD) {
            score += 30.0; reason.append("당뇨 ");
        }
        if (savedRecord.getBmi() >= RiskConfiguration.BMI_THRESHOLD) {
            score += 10.0; reason.append("비만 ");
        }
        if ("Yes".equalsIgnoreCase(smoking)) {
            score += 15.0; reason.append("흡연 ");
        }

        String level = "정상";
        if (score >= 50) level = "고위험";
        else if (score >= 30) level = "주의";

        // 3. 분석 결과 저장
        RiskAssessment risk = new RiskAssessment();
        risk.setPatientId(patientId);
        risk.assess(score, score, level, reason.toString());
        repo.saveRisk(risk);

        // -----------------------------------------------------------------
        // [핵심] 알림 발송 로직
        // -----------------------------------------------------------------
        if ("고위험".equals(level)) {
            String msg = String.format("🚨 [위험 경고] 혈압:%d/%d, 혈당:%.0f (%s)", sys, dia, sugar, reason);

            // (1) 환자 본인에게 알림
            Alert myAlert = new Alert();
            myAlert.create(patientId, msg);
            msgRepo.saveAlert(myAlert);

            // (2) 연결된 보호자에게 알림
            List<PatientAssignment> list = assignRepo.getAssignments(patientId);
            for (PatientAssignment a : list) {
                // [중요] 상태가 ACCEPTED이고, 보호자 ID가 있을 때만 보냄
                if ("ACCEPTED".equals(a.getStatus()) && a.getCaregiverId() != null) {
                    Alert familyAlert = new Alert();
                    familyAlert.create(a.getCaregiverId(), "🚨 [가족 위험] " + msg);
                    msgRepo.saveAlert(familyAlert);
                }
            }
        }

        return savedRecord;
    }

    // --- 기존 조회 메서드 유지 ---
    public List<RiskAssessment> getRisk(Long patientId) {
        List<HealthRecord> records = repo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return Collections.emptyList();
        List<RiskAssessment> result = new ArrayList<>();
        for (HealthRecord record : records) {
            result.add(calculateRiskDynamic(record));
        }
        return result;
    }

    // ... 나머지 getCompRisk, calculate... 메서드들 그대로 유지 ...
    // (보내주신 코드 그대로 두시면 됩니다)
    public List<ComplicationRisk> getCompRisk(Long patientId) {
        List<HealthRecord> records = repo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return Collections.emptyList();
        List<ComplicationRisk> result = new ArrayList<>();
        for (HealthRecord record : records) {
            result.add(calculateComplicationDynamic(record));
        }
        return result;
    }

    private RiskAssessment calculateRiskDynamic(HealthRecord r) {
        double score = 0.0;
        StringBuilder reason = new StringBuilder();
        if (r.getSystolicBp() >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD ||
                r.getDiastolicBp() >= RiskConfiguration.BP_DIASTOLIC_THRESHOLD) {
            score += 30.0; reason.append("고혈압/ ");
        }
        if (r.getBloodSugar() >= RiskConfiguration.SUGAR_THRESHOLD) {
            score += 20.0; reason.append("당뇨/ ");
        }
        if (r.getBmi() >= RiskConfiguration.BMI_THRESHOLD) {
            score += 10.0; reason.append("비만/ ");
        }
        if ("Yes".equalsIgnoreCase(r.getSmoking())) {
            score += 15.0; reason.append("흡연/ ");
        }
        if (score == 0) reason.append("정상 범위");
        String level = "정상";
        if (score >= 50) level = "고위험";
        else if (score >= 30) level = "주의";
        RiskAssessment risk = new RiskAssessment();
        risk.setPatientId(r.getPatientId());
        risk.assess(score, score, level, reason.toString());
        risk.setAssessedAt(r.getMeasuredAt());
        return risk;
    }

    private ComplicationRisk calculateComplicationDynamic(HealthRecord r) {
        double riskScore = 0;
        if (r.getSystolicBp() >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD) riskScore += 20;
        if (r.getBloodSugar() >= RiskConfiguration.SUGAR_THRESHOLD) riskScore += 10;
        String level = riskScore >= 50 ? "높음" : (riskScore >= 20 ? "중간" : "낮음");
        ComplicationRisk comp = new ComplicationRisk();
        comp.setPatientId(r.getPatientId());
        comp.update("심혈관/뇌졸중 (실시간 분석)", riskScore, "위험도: " + level);
        return comp;
    }

    public List<HealthRecord> getRecords(Long patientId) { return repo.findRecordsByPatient(patientId); }
    public RiskAssessment createRisk(Long pid, double score, double percent, String level, String summary) {
        RiskAssessment r = new RiskAssessment(); r.setPatientId(pid); r.assess(score, percent, level, summary); return repo.saveRisk(r);
    }
    public ComplicationRisk createCompRisk(Long pid, String type, double prob, String rec) {
        ComplicationRisk r = new ComplicationRisk(); r.setPatientId(pid); r.update(type, prob, rec); return repo.saveCompRisk(r);
    }
    public List<DoctorNote> getMyNotes(Long patientId) { return repo.findNotesByPatient(patientId); }
    public List<ScheduledExam> getMyExams(Long patientId) { return repo.findExamsByPatient(patientId); }
}