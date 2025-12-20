package domain.service;

import data.repository.MedicalRepository;
import data.repository.ReportRepository;
import domain.patient.HealthRecord;
import domain.patient.PersonalReport;
import domain.patient.RiskConfiguration;
import domain.patient.GroupComparisonResult;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ReportService {

    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final ReportRepository reportRepo = new ReportRepository();

    // --------------------------------------------------------
    // [1] 개인화 리포트 (C_3)
    // --------------------------------------------------------
    public PersonalReport createPersonalReport(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);

        if (records.size() < 2) {
            return new PersonalReport(patientId,
                    "- 데이터 부족 (2회 이상 기록 필요)",
                    "- 분석 불가",
                    "- 건강 기록을 먼저 입력해주세요.");
        }

        records.sort(Comparator.comparing(HealthRecord::getMeasuredAt));
        HealthRecord past = records.get(0);
        HealthRecord now = records.get(records.size() - 1);

        // 추세 분석
        StringBuilder trend = new StringBuilder();
        int sysDiff = now.getSystolicBp() - past.getSystolicBp();
        if (sysDiff > 10) trend.append("- 혈압 상승 추세 (주의)\n");
        else if (sysDiff < -10) trend.append("- 혈압 감소 추세 (양호)\n");
        else trend.append("- 혈압 안정적 유지 중\n");

        double sugarDiff = now.getBloodSugar() - past.getBloodSugar();
        if (sugarDiff > 15) trend.append("- 혈당 상승 추세 (식단 관리 필요)");
        else if (sugarDiff < -15) trend.append("- 혈당 감소 추세 (매우 양호)");
        else trend.append("- 혈당 변동폭 작음");

        // 위험 분석
        StringBuilder risk = new StringBuilder();
        if (now.getSystolicBp() >= RiskConfiguration.BP_SYSTOLIC_THRESHOLD)
            risk.append("- '고혈압' 위험 감지됨\n");
        if (now.getBloodSugar() >= RiskConfiguration.SUGAR_THRESHOLD)
            risk.append("- '당뇨' 위험 감지됨\n");
        if (risk.length() == 0) risk.append("- 특별한 위험 요인 없음");

        String advice = (risk.toString().contains("위험"))
                ? "주치의 상담 권장, 저염식 실천 필요"
                : "현재 상태 매우 좋음, 운동 지속 권장";

        PersonalReport report = new PersonalReport(patientId, trend.toString(), risk.toString(), advice);
        return reportRepo.savePersonal(report);
    }

    public List<PersonalReport> getPersonalReports(Long patientId) {
        return reportRepo.getPersonalByPatient(patientId);
    }

    // --------------------------------------------------------
    // [2] 또래 비교 리포트 (B_New)
    // --------------------------------------------------------
    public List<GroupComparisonResult> getGroup(Long pid) {
        return reportRepo.getGroupByPatient(pid);
    }

    // [필수 추가] PatientPanel에서 호출하는 메서드 구현
    public GroupComparisonResult createGroup(Long pid, String groupKey, double myMetric, double groupAvg, String chartData) {
        GroupComparisonResult g = new GroupComparisonResult();
        g.setPatientId(pid);
        g.setGroupKey(groupKey);
        g.setPatientMetric(myMetric);
        g.setGroupAverage(groupAvg);
        // 간단한 백분위 계산 로직 (예시)
        double diff = myMetric - groupAvg;
        double percentile = 50.0 + (diff / groupAvg) * 20.0;
        if (percentile > 99) percentile = 99;
        if (percentile < 1) percentile = 1;
        g.setPercentile(percentile);

        g.setCreatedAt(LocalDateTime.now());

        return reportRepo.saveGroup(g);
    }
}