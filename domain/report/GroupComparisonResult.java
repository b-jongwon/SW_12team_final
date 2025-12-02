
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

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public double getPatientMetric() {
        return patientMetric;
    }

    public void setPatientMetric(double patientMetric) {
        this.patientMetric = patientMetric;
    }

    public double getGroupAverage() {
        return groupAverage;
    }

    public void setGroupAverage(double groupAverage) {
        this.groupAverage = groupAverage;
    }

    public double getPercentile() {
        return percentile;
    }

    public void setPercentile(double percentile) {
        this.percentile = percentile;
    }

    public String getChartData() {
        return chartData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public GroupComparisonResult(Long id, Long patientId, String groupKey, double patientMetric, double groupAverage, double percentile, String chartData, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.groupKey = groupKey;
        this.patientMetric = patientMetric;
        this.groupAverage = groupAverage;
        this.percentile = percentile;
        this.chartData = chartData;
        this.createdAt = createdAt;
    }

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
