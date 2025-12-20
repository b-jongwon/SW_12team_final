package domain.service;

import data.repository.AssignmentRepository;
import data.repository.ContentRepository;
import data.repository.MedicalRepository;
import data.repository.MessagingRepository;
import domain.content.ContentItem;
import domain.medical.DoctorNote;
import domain.medical.ScheduledExam;
import domain.patient.*;
import domain.user.Patient;
import data.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientCareService {

    // 레포지토리 초기화
    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final AssignmentRepository assignRepo = new AssignmentRepository();
    private final MessagingRepository msgRepo = new MessagingRepository();
    private final ContentRepository contentRepo = new ContentRepository();
    private final UserRepository userRepository = new UserRepository();
    // --------------------------------------------------------------------------
    // [1] 건강 데이터 생성 및 실시간 1차 위험 분석 (신호등 시스템)
    // --------------------------------------------------------------------------
    public HealthRecord createHealthRecord(Long patientId,
                                           int age, String gender,
                                           int sys, int dia, double sugar,
                                           String smoking, String drinking,
                                           String activity, String riskFactors,
                                           double height, double weight) {

        HealthRecord record = new HealthRecord();
        record.setPatientId(patientId);

        // [수정됨] BMI는 HealthRecord 내부의 update() 메서드에서 자동 계산되도록 함
        // (여기서 이중으로 계산하거나 setBmi를 호출하지 않음)

        // 업데이트 메서드 호출
        record.update(age, gender, sys, dia, sugar, smoking, drinking,
                activity, riskFactors, height, weight);

        HealthRecord savedRecord = medicalRepo.saveNewRecord(record);

        // 2. 기본 위험도 분석 (신호등)
        double score = 0.0;
        StringBuilder reason = new StringBuilder();

        // 혈압 (140/90 이상)
        if (sys >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD || dia >= RiskConfiguration.BP_DIASTOLIC_THRESHOLD) {
            score += 40.0; reason.append("고혈압 ");
        }
        // 혈당 (126 이상)
        if (sugar >= RiskConfiguration.SUGAR_THRESHOLD) {
            score += 30.0; reason.append("당뇨 ");
        }
        // BMI (25 이상 비만)
        if (savedRecord.getBmi() >= RiskConfiguration.BMI_THRESHOLD) {
            score += 10.0; reason.append("비만 ");
        }
        // 흡연 여부
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
    // [3] 합병증(심혈관) 위험도 조회 (개별 경고 기능 강화 버전)
    // --------------------------------------------------------------------------
    public List<ComplicationRisk> getCompRisk(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return Collections.emptyList();

        List<ComplicationRisk> result = new ArrayList<>();
        for (HealthRecord record : records) {
            result.add(calculateComplicationDynamic(record));
        }
        return result;
    }

    // --------------------------------------------------------------------------
    // [4] 맞춤형 콘텐츠 추천
    // --------------------------------------------------------------------------
    public List<ContentItem> getRecommendedContents(Long patientId) {
        List<RiskAssessment> risks = getRisk(patientId);
        String currentLevel = "정상";
        if (!risks.isEmpty()) {
            currentLevel = risks.get(risks.size() - 1).getRiskLevel();
        }
        return contentRepo.findContentsByRisk(currentLevel);
    }

    // ==========================================================================
    // Helper Methods: 비즈니스 로직(알고리즘)이 들어가는 곳
    // ==========================================================================

    // A. 뇌졸중 위험도 계산
    private RiskAssessment calculateRiskDynamic(HealthRecord r) {
        double score = 0.0;
        StringBuilder reason = new StringBuilder();

        // 개인 맞춤형 기준 가져오기
        RiskConfiguration.PersonalCriteria criteria =
                RiskConfiguration.getPersonalizedCriteria(r.getAge(), r.getGender());

        if (r.getSystolicBp() >= criteria.maxSys || r.getDiastolicBp() >= criteria.maxDia) {
            score += 40.0; reason.append("고혈압/ ");
        } else if (r.getSystolicBp() >= (criteria.maxSys - 20)) {
            score += 15.0; reason.append("혈압주의/ ");
        }

        if (r.getBloodSugar() >= criteria.maxSugar) {
            score += 30.0; reason.append("당뇨/ ");
        }

        if (r.getBmi() >= criteria.maxBmi) {
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
        risk.setAssessedAt(r.getMeasuredAt());
        return risk;
    }

    // B. 합병증(심혈관) 위험도 계산 (수정됨: 개별 항목 경고 기능 강화)
    private ComplicationRisk calculateComplicationDynamic(HealthRecord r) {
        double riskScore = 0.0;
        List<String> warnings = new ArrayList<>();

        // 개인 맞춤형 기준 사용
        RiskConfiguration.PersonalCriteria criteria =
                RiskConfiguration.getPersonalizedCriteria(r.getAge(), r.getGender());

        // 1. 혈압 체크 (기준치보다 20 높으면 위험, 그냥 높으면 주의)
        double highBpLimit = criteria.maxSys + 20;

        if (r.getSystolicBp() >= highBpLimit) {
            riskScore += 30;
            warnings.add("혈압 위험(즉시 관리)");
        } else if (r.getSystolicBp() >= criteria.maxSys) {
            riskScore += 15;
            warnings.add("혈압 주의");
        }

        // 2. 혈당 체크
        if (r.getBloodSugar() >= criteria.maxSugar) {
            riskScore += 20;
            warnings.add("혈당 관리");
        }

        // 3. 흡연 체크
        if ("Yes".equalsIgnoreCase(r.getSmoking())) {
            riskScore += 20;
            warnings.add("금연 권장");
        }

        // 4. 비만도(BMI) 체크
        if (r.getBmi() >= 30) {
            riskScore += 10;
            warnings.add("체중 감량(고도비만)");
        } else if (r.getBmi() >= criteria.maxBmi) {
            warnings.add("체중 조절(비만)");
        }

        if (riskScore > 100) riskScore = 100;

        String recommendation;
        String warningText = String.join(", ", warnings);

        if (riskScore >= 70) {
            recommendation = "🚨 [고위험] 즉시 전문의 상담 필요 (" + warningText + ")";
        }
        else if (riskScore >= 40) {
            recommendation = "⚠️ [주의] 생활 습관 개선 시급 (" + warningText + ")";
        }
        else {
            if (!warnings.isEmpty()) {
                recommendation = "✅ [관심] 전체적인 상태는 양호하나, [" + warningText + "] 에 유의하세요.";
            } else {
                recommendation = "🎉 [정상] 현재 매우 건강한 상태입니다. (지속 관리 권장)";
            }
        }

        ComplicationRisk comp = new ComplicationRisk();
        comp.setPatientId(r.getPatientId());
        comp.update("심혈관 건강 및 생활습관", riskScore, recommendation);
        return comp;
    }

    // ==========================================================================
    // [중요] PatientController가 사용하는 단순 조회/생성 메서드들 (오류 해결!)
    // ==========================================================================

    public List<HealthRecord> getRecords(Long pid) {
        return medicalRepo.findRecordsByPatient(pid);
    }

    public List<DoctorNote> getMyNotes(Long pid) {
        return medicalRepo.findNotesByPatient(pid);
    }

    public List<ScheduledExam> getMyExams(Long pid) {
        return medicalRepo.findExamsByPatient(pid);
    }

    public RiskAssessment createRisk(Long pid, double score, double percent, String level, String summary) {
        RiskAssessment r = new RiskAssessment();
        r.setPatientId(pid);
        r.assess(score, percent, level, summary);
        return medicalRepo.saveRisk(r);
    }

    public ComplicationRisk createCompRisk(Long pid, String type, double prob, String rec) {
        ComplicationRisk r = new ComplicationRisk();
        r.setPatientId(pid);
        r.update(type, prob, rec);
        return medicalRepo.saveCompRisk(r);
    }
    public List<GroupComparisonResult> getSimulationResults(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);
        if (records.isEmpty()) return Collections.emptyList();

        HealthRecord last = records.get(records.size() - 1); // 최신 기록
        List<GroupComparisonResult> simulations = new ArrayList<>();

        // 1. [나이대 비교] 내 점수 vs 같은 나이대 평균 점수
        // (점수가 낮을수록 건강함)
        double myRiskScore = calculateRiskDynamic(last).getRiskScore();
        double ageAvgScore = 35.0; // 시뮬레이션 값 (30~40대 평균)
        if (last.getAge() >= 60) ageAvgScore = 55.0; // 고령층 평균은 좀 더 높음

        GroupComparisonResult sim1 = new GroupComparisonResult();
        sim1.setGroupKey(last.getAge() / 10 * 10 + "대 평균 위험도 비교"); // 예: 20대 평균
        sim1.setPatientMetric(myRiskScore);
        sim1.setGroupAverage(ageAvgScore);
        sim1.setCreatedAt(java.time.LocalDateTime.now());
        simulations.add(sim1);

        // 2. [BMI 비교] 내 BMI vs 이상적인 건강 그룹 BMI
        GroupComparisonResult sim2 = new GroupComparisonResult();
        sim2.setGroupKey("상위 10% 건강 그룹(BMI) 비교");
        sim2.setPatientMetric(last.getBmi());
        sim2.setGroupAverage(21.5); // 이상적인 BMI
        sim2.setCreatedAt(java.time.LocalDateTime.now());
        simulations.add(sim2);

        // 3. [혈당 비교] 내 혈당 vs 동년배 평균 혈당
        GroupComparisonResult sim3 = new GroupComparisonResult();
        sim3.setGroupKey("동년배 평균 혈당 비교");
        sim3.setPatientMetric(last.getBloodSugar());
        sim3.setGroupAverage(95.0); // 평균 공복혈당
        sim3.setCreatedAt(java.time.LocalDateTime.now());
        simulations.add(sim3);

        return simulations;
    }
}