
package domain.report;

import java.time.LocalDateTime;

/**
 * 그룹 비교 리포트: 동일 연령/성별/질환군 등과 비교한 지표.
 * 합병증 관련 비교도 이 안에서 표현 가능 (groupKey로 구분).
 */
public class GroupComparisonResult {
    private Long id;
    private Long patientId;
    private String groupKey;       // 예: "AGE_60_70", "COMPLICATION_STROKE"
    private double patientMetric;  // 환자 지표 (예: 위험 점수)
    private double groupAverage;   // 그룹 평균
    private double percentile;     // 백분위
    private String chartData;      // 그래프용 데이터 (JSON 문자열 등)
    private LocalDateTime createdAt;

    public GroupComparisonResult() {}

    public void initialize(Long patientId, String groupKey) {
        this.patientId = patientId;
        this.groupKey = groupKey;
        this.createdAt = LocalDateTime.now();
    }

    public void calculate(double patientMetric, double groupAvg) {
        this.patientMetric = patientMetric;
        this.groupAverage = groupAvg;
        this.percentile = groupAvg == 0 ? 0 : (patientMetric / groupAvg) * 100;
    }

    public void setChartData(String data) {
        this.chartData = data;
    }

    // --- getter/setter 최소한 ---
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
}
