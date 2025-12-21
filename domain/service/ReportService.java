package domain.service;

import data.repository.MedicalRepository;
import data.repository.ReportRepository;
import data.repository.UserRepository;
import domain.patient.GroupComparisonResult;
import domain.patient.HealthRecord;
import domain.patient.PersonalReport;
import domain.patient.RiskConfiguration; // ★ 설정값 연동

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ReportService {

    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final ReportRepository reportRepo = new ReportRepository();
    private final UserRepository userRepo = new UserRepository();

    // --------------------------------------------------------
    // [1] 개인화 리포트 (추세 분석)
    // --------------------------------------------------------
    public PersonalReport createPersonalReport(Long patientId) {
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(patientId);

        if (records.size() < 2) {
            return new PersonalReport(patientId,
                    "- 데이터 부족 (2회 이상 기록 필요)",
                    "- 분석 불가",
                    "- 건강 기록을 먼저 입력해주세요.");
        }

        // 날짜순 정렬
        records.sort(Comparator.comparing(HealthRecord::getMeasuredAt));
        HealthRecord past = records.get(0);
        HealthRecord now = records.get(records.size() - 1);

        // 1. 추세 분석 (혈압/혈당 변화량)
        StringBuilder trend = new StringBuilder();
        int sysDiff = now.getSystolicBp() - past.getSystolicBp();
        if (sysDiff > 10) trend.append("- 혈압 상승 추세 (주의 🔺)\n");
        else if (sysDiff < -10) trend.append("- 혈압 감소 추세 (양호 🔻)\n");
        else trend.append("- 혈압 안정적 유지 중\n");

        double sugarDiff = now.getBloodSugar() - past.getBloodSugar();
        if (sugarDiff > 15) trend.append("- 혈당 상승 추세 (식단 관리 필요)\n");
        else if (sugarDiff < -15) trend.append("- 혈당 감소 추세 (매우 양호)\n");

        // 2. 현재 상태 위험 분석 (설정값 연동)
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
        report.setPeriod(past.getMeasuredAt(), now.getMeasuredAt());
        return reportRepo.savePersonal(report);
    }

    public List<PersonalReport> getPersonalReports(Long patientId) {
        return reportRepo.getPersonalByPatient(patientId);
    }

    // --------------------------------------------------------
    // [2] 또래 비교 리포트 (통계 기반 정밀 로직 적용)
    // --------------------------------------------------------

    public List<GroupComparisonResult> getGroup(Long pid) {
        return reportRepo.getGroupByPatient(pid);
    }


    public GroupComparisonResult createGroupComparison(Long pid) {
        // 1. 환자 기록 가져오기
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(pid);
        if (records.isEmpty()) return null;

        // 최신 기록을 기준으로 분석
        records.sort((r1, r2) -> r2.getMeasuredAt().compareTo(r1.getMeasuredAt())); // 최신순
        HealthRecord latest = records.get(0);

        // 2. 나이와 성별 확인
        int age = latest.getAge();
        if (age == 0) age = 30; // 방어 코드

        String gender = latest.getGender();
        if (gender == null) gender = "Male";

        // 3. 비교 그룹 정의
        String ageGroup = (age / 10) * 10 + "대"; // 예: "40대"
        String genderKo = "Male".equalsIgnoreCase(gender) ? "남성" : "여성";
        String groupKey = genderKo + " " + ageGroup + " 평균"; // 결과 예: "남성 40대 평균"

        // 4. 내 수치 (수축기 혈압 기준)
        double myMetric = latest.getSystolicBp();
        if (myMetric == 0) return null;

        double groupAvg = 120.0;

        if ("Male".equalsIgnoreCase(gender)) {
            if (age < 30) groupAvg = 118.0;       // 20대 남성
            else if (age < 40) groupAvg = 121.0;  // 30대
            else if (age < 50) groupAvg = 126.0;  // 40대
            else if (age < 60) groupAvg = 131.0;  // 50대
            else if (age < 70) groupAvg = 138.0;  // 60대
            else groupAvg = 141.0;                // 70대 이상
        } else {
            if (age < 30) groupAvg = 110.0;       // 20대 여성
            else if (age < 40) groupAvg = 112.0;
            else if (age < 50) groupAvg = 118.0;
            else if (age < 60) groupAvg = 126.0;
            else if (age < 70) groupAvg = 136.0;
            else groupAvg = 145.0;
        }

        // 6. 백분위 계산
        double diff = myMetric - groupAvg;
        double zScore = diff / 15.0;


        double percentile = 50.0 + (zScore * 34.0);// 범위 보정 (1% ~ 99% 사이로 제한)
        if (percentile > 99.0) percentile = 99.0;
        if (percentile < 1.0) percentile = 1.0;

        // 7. 결과 저장
        GroupComparisonResult result = new GroupComparisonResult();
        result.setPatientId(pid);
        result.setGroupKey(groupKey);
        result.setPatientMetric(myMetric);
        result.setGroupAverage(groupAvg);
        result.setPercentile(Math.round(percentile * 10) / 10.0); // 소수점 첫째자리까지

        String chartData = String.format("{\"my\": %.1f, \"avg\": %.1f}", myMetric, groupAvg);
        result.setChartData(chartData);

        result.setCreatedAt(LocalDateTime.now());

        return reportRepo.saveGroup(result);
    }

    // [호환성 유지] UI 컨트롤러 등에서 예전 방식으로 호출하더라도, 내부적으로는 정밀 분석을 수행하도록 연결
    public GroupComparisonResult createGroup(Long pid, String groupKey, double metric, double avg, String chartData) {
        return createGroupComparison(pid);
    }
}