
package domain.report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 개인 리포트: 한 환자의 일정 기간 건강 상태, 위험 요인, 목표 등을 요약.
 */
public class PersonalReport {
    private Long id;
    private Long patientId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String summaryText;

    // 그래프/차트 등의 데이터는 일단 문자열 리스트로 단순화
    private List<String> trendChartData = new ArrayList<>();
    private List<String> topRiskFactors = new ArrayList<>();
    private List<String> recommendedGoals = new ArrayList<>();

    // 합병증 관련 요약도 같이 포함 (별도 Report 클래스로 안 빼고 통합)
    private String complicationSummary;

    private LocalDateTime createdAt;

    public PersonalReport() {}

    public void init(Long patientId) {
        this.patientId = patientId;
        this.createdAt = LocalDateTime.now();
    }

    public void setPeriod(LocalDateTime start, LocalDateTime end) {
        this.periodStart = start;
        this.periodEnd = end;
    }

    public void setSummaryText(String text) {
        this.summaryText = text;
    }

    public void addTrendData(String data) {
        trendChartData.add(data);
    }

    public void addRiskFactor(String factor) {
        topRiskFactors.add(factor);
    }

    public void addGoal(String goal) {
        recommendedGoals.add(goal);
    }

    public void setComplicationSummary(String complicationSummary) {
        this.complicationSummary = complicationSummary;
    }

    public String summarize() {
        return "요약: " + summaryText + (complicationSummary != null ? " / 합병증: " + complicationSummary : "");
    }

    // --- getter/setter 최소한만 제공 (필요하면 IDE로 확장) ---
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
