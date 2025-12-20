package domain.service;

import data.repository.AssignmentRepository;
import data.repository.ContentRepository;
import data.repository.MedicalRepository;
import data.repository.MessagingRepository;
import domain.content.ContentItem;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import domain.patient.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientCareService {

    // 레포지토리 초기화
    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final AssignmentRepository assignRepo = new AssignmentRepository();
    private final MessagingRepository msgRepo = new MessagingRepository();
    private final ContentRepository contentRepo = new ContentRepository();

    // --------------------------------------------------------------------------
    // [1] 건강 데이터 생성 및 실시간 1차 위험 분석 (신호등 시스템)
    // --------------------------------------------------------------------------
    public HealthRecord createHealthRecord(Long patientId,
                                           int age, String gender, // [추가됨]
                                           int sys, int dia, double sugar,
                                           String smoking, String drinking,
                                           String activity, String riskFactors,
                                           double height, double weight) {

        HealthRecord record = new HealthRecord();
        record.setPatientId(patientId);

        // 업데이트 메서드 호출 (순서 주의)
        record.update(age, gender, sys, dia, sugar, smoking, drinking,
                activity, riskFactors, height, weight);

        HealthRecord savedRecord = medicalRepo.saveNewRecord(record);

        // 2. 기본 위험도 분석 (신호등)
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
        medicalRepo.saveRisk(risk);

        // 4. 고위험군 알림 발송 (환자 및 보호자)
        if ("고위험".equals(level)) {
            String msg = String.format("🚨 [위험 경고] 혈압:%d/%d, 혈당:%.0f (%s)", sys, dia, sugar, reason);

            // 환자 본인 알림
            Alert myAlert = new Alert();
            myAlert.create(patientId, msg);
            msgRepo.saveAlert(myAlert);

            // 보호자 알림 (연결된 보호자가 있을 경우)
            List<PatientAssignment> list = assignRepo.getAssignments(patientId);
            for (PatientAssignment a : list) {
                if ("ACCEPTED".equals(a.getStatus()) && a.getCaregiverId() != null) {
                    Alert familyAlert = new Alert();
                    familyAlert.create(a.getCaregiverId(), "🚨 [가족 위험] " + msg);
                    msgRepo.saveAlert(familyAlert);
                }
            }
        }
        return savedRecord;
    }

    // --------------------------------------------------------------------------
    // [2] 뇌졸중 위험도 조회 (저장된 기록 기반 동적 계산)
    // --------------------------------------------------------------------------
    public List<RiskAssessment> getRisk(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return Collections.emptyList();

        List<RiskAssessment> result = new ArrayList<>();
        for (HealthRecord record : records) {
            result.add(calculateRiskDynamic(record));
        }
        return result;
    }

    // --------------------------------------------------------------------------
    // [3] 합병증(심혈관) 위험도 조회 (핵심: 동적 계산 적용)
    // --------------------------------------------------------------------------
    public List<ComplicationRisk> getCompRisk(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return Collections.emptyList();

        List<ComplicationRisk> result = new ArrayList<>();
        // 모든 기록에 대해 계산하거나, 최신 기록만 계산할 수도 있음. 여기선 전체 이력 반환.
        for (HealthRecord record : records) {
            result.add(calculateComplicationDynamic(record));
        }
        return result;
    }

    // --------------------------------------------------------------------------
    // [4] 맞춤형 콘텐츠 추천
    // --------------------------------------------------------------------------
    public List<ContentItem> getRecommendedContents(Long patientId) {
        List<RiskAssessment> risks = getRisk(patientId); // 재계산된 리스크 사용
        String currentLevel = "정상";
        if (!risks.isEmpty()) {
            // 가장 최신(마지막) 위험도의 레벨을 가져옴
            currentLevel = risks.get(risks.size() - 1).getRiskLevel();
        }
        return contentRepo.findContentsByRisk(currentLevel);
    }

    // ==========================================================================
    // Helper Methods: 비즈니스 로직(알고리즘)이 들어가는 곳
    // ==========================================================================

    // A. 뇌졸중 위험도 계산 (기존 로직 유지)
    private RiskAssessment calculateRiskDynamic(HealthRecord r) {
        double score = 0.0;
        StringBuilder reason = new StringBuilder();

        if (r.getSystolicBp() >= 140 || r.getDiastolicBp() >= 90) {
            score += 40.0; reason.append("고혈압/ ");
        } else if (r.getSystolicBp() >= 120) {
            score += 15.0; reason.append("혈압주의/ ");
        }

        if (r.getBloodSugar() >= 126) {
            score += 30.0; reason.append("당뇨/ ");
        }

        if (r.getBmi() >= 25.0) { // BMI 25 이상 비만
            score += 15.0; reason.append("비만/ ");
        }

        if ("Yes".equalsIgnoreCase(r.getSmoking())) {
            score += 15.0; reason.append("흡연/ ");
        }

        if (score == 0) reason.append("정상 범위");

        String level = "정상";
        if (score >= 60) level = "고위험";
        else if (score >= 30) level = "주의";

        RiskAssessment risk = new RiskAssessment();
        risk.setPatientId(r.getPatientId());
        risk.assess(score, score, level, reason.toString());
        risk.setAssessedAt(r.getMeasuredAt()); // 기록된 시간 기준
        return risk;
    }

    // B. 합병증(심혈관) 위험도 계산 (상세 알고리즘 적용)
    private ComplicationRisk calculateComplicationDynamic(HealthRecord r) {
        double riskScore = 0.0;
        List<String> factors = new ArrayList<>();

        // 1. 수축기 혈압 가중치
        if (r.getSystolicBp() >= 160) {
            riskScore += 50; factors.add("심각한 고혈압");
        } else if (r.getSystolicBp() >= 140) {
            riskScore += 30; factors.add("고혈압");
        } else if (r.getSystolicBp() >= 130) {
            riskScore += 10;
        }

        // 2. 혈당 가중치
        if (r.getBloodSugar() >= 126) {
            riskScore += 20; factors.add("당뇨");
        }

        // 3. 흡연 여부
        if ("Yes".equalsIgnoreCase(r.getSmoking())) {
            riskScore += 20; factors.add("흡연");
        }

        // 4. BMI 가중치
        if (r.getBmi() >= 30) {
            riskScore += 10; factors.add("고도비만");
        }

        // 최대 점수 100점 제한
        if (riskScore > 100) riskScore = 100;

        // 결과 문자열 생성
        String recommendation;
        if (riskScore >= 70) recommendation = "즉시 전문의 상담 필요 (" + String.join(", ", factors) + ")";
        else if (riskScore >= 40) recommendation = "생활 습관 개선 시급 (" + String.join(", ", factors) + ")";
        else recommendation = "현재 상태 양호 (지속 관리 권장)";

        ComplicationRisk comp = new ComplicationRisk();
        comp.setPatientId(r.getPatientId());
        comp.update("심혈관/뇌졸중 예측", riskScore, recommendation);
        return comp;
    }

    // 단순 조회 및 저장 메서드들
    public List<HealthRecord> getRecords(Long pid) { return medicalRepo.findRecordsByPatient(pid); }
    public List<DoctorNote> getMyNotes(Long pid) { return medicalRepo.findNotesByPatient(pid); }
    public List<ScheduledExam> getMyExams(Long pid) { return medicalRepo.findExamsByPatient(pid); }

    // 1. 수동 위험도 생성 (Controller 호환용)
    public RiskAssessment createRisk(Long pid, double score, double percent, String level, String summary) {
        RiskAssessment r = new RiskAssessment();
        r.setPatientId(pid);
        r.assess(score, percent, level, summary);
        return medicalRepo.saveRisk(r);
    }

    // 2. 수동 합병증 위험 생성 (Controller 호환용)
    public ComplicationRisk createCompRisk(Long pid, String type, double prob, String rec) {
        ComplicationRisk r = new ComplicationRisk();
        r.setPatientId(pid);
        r.update(type, prob, rec);
        return medicalRepo.saveCompRisk(r);
    }
}