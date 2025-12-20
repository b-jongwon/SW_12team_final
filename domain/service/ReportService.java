package domain.service;

import data.repository.MedicalRepository;
import data.repository.ReportRepository;
import data.repository.UserRepository; // ★ 필수: 유저 나이 가져오기 위해 추가
import domain.patient.GroupComparisonResult;
import domain.patient.HealthRecord;
import domain.patient.PersonalReport;
import domain.patient.RiskConfiguration;
import domain.user.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ReportService {

    private final MedicalRepository medicalRepo = new MedicalRepository();
    private final ReportRepository reportRepo = new ReportRepository();
    private final UserRepository userRepo = new UserRepository(); // 유저 정보 조회용

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

        // 날짜순 정렬 (과거 -> 최신)
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

        // 2. 현재 상태 위험 분석
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
    // [2] 또래 비교 리포트 (핵심: 나이 기반 동적 생성)
    // --------------------------------------------------------

    // 조회용
    public List<GroupComparisonResult> getGroup(Long pid) {
        return reportRepo.getGroupByPatient(pid);
    }

    public GroupComparisonResult createGroupComparison(Long pid) {
        // 1. 사용자 정보 가져오기 (혹은 최근 HealthRecord에서 가져오기)
        List<HealthRecord> records = medicalRepo.findRecordsByPatient(pid);
        if (records.isEmpty()) return null;

        HealthRecord latest = records.get(records.size() - 1); // 최신 기록

        // 2. 기록된 나이와 성별 가져오기 (없으면 기본값 설정)
        int age = latest.getAge();
        if (age == 0) age = 30; // 기본값

        String gender = latest.getGender();
        if (gender == null || gender.isEmpty()) gender = "Male"; // 기본값

        // 3. 연령대 및 그룹명 결정
        int ageGroup = (age / 10) * 10;
        String genderKo = "Male".equals(gender) ? "남성" : "여성";
        String groupKey = ageGroup + "대 " + genderKo + " 평균";

        // 4. 내 수치
        double myMetric = latest.getSystolicBp();
        if (myMetric == 0) return null; // 혈압 입력 안했으면 분석 불가

        // 5. [핵심] 성별/나이별 가상 평균 계산 로직
        // 기본 혈압: 남성 120, 여성 110 시작
        double baseAvg = "Male".equals(gender) ? 120.0 : 110.0;

        // 나이에 따른 증가 (10살 먹을 때마다 혈압 3씩 증가 가정)
        double ageFactor = (age - 20) * 0.3;
        if (ageFactor < 0) ageFactor = 0;

        double groupAvg = baseAvg + ageFactor;

        // 6. 백분위 계산
        double diff = myMetric - groupAvg;
        double percentile = 50.0 + (diff / 15.0 * 20.0);

        if (percentile > 99.0) percentile = 99.0;
        if (percentile < 1.0) percentile = 1.0;

        // 7. 저장 및 반환
        GroupComparisonResult result = new GroupComparisonResult();
        result.setPatientId(pid);
        result.setGroupKey(groupKey);
        result.setPatientMetric(myMetric);
        result.setGroupAverage(Math.round(groupAvg * 10) / 10.0);
        result.setPercentile(Math.round(percentile * 10) / 10.0);
        result.setCreatedAt(LocalDateTime.now());

        return reportRepo.saveGroup(result);
    }

    // (기존 하드코딩 호출을 위한 호환성 유지 메서드 - 필요 없으면 삭제 가능)
    // UI 쪽에서 이 메서드를 호출하던 부분을 위의 createGroupComparison(pid)로 바꾸는 게 좋음.
    public GroupComparisonResult createGroup(Long pid, String groupKey, double myMetric, double groupAvg, String chartData) {
        // 내부적으로 더 스마트한 메서드로 위임하거나, 그냥 단순 저장 수행
        return createGroupComparison(pid);
    }
}