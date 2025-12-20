package domain.patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PersonalReport {
    private Long id;
    private Long patientId;

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String summaryText; // 종합 요약

    // 리스트 형태로 데이터 관리
    private List<String> trendChartData = new ArrayList<>();
    private List<String> topRiskFactors = new ArrayList<>();
    private List<String> recommendedGoals = new ArrayList<>();

    private String complicationSummary;
    private LocalDateTime createdAt;

    public PersonalReport() {
        this.createdAt = LocalDateTime.now();
    }

    public PersonalReport(Long patientId, String trend, String risk, String advice) {
        this.patientId = patientId;
        this.createdAt = LocalDateTime.now();
        this.summaryText = "건강 데이터 자동 분석 결과";

        // [수정] 들어온 문자열이 여러 줄일 경우 쪼개서 넣기
        if (trend != null && !trend.isEmpty()) {
            // \n 으로 자르고, 각 줄의 앞뒤 공백 제거(trim) 후 저장
            for (String line : trend.split("\n")) {
                this.trendChartData.add(line.trim());
            }
        }

        // risk, advice도 마찬가지로 처리하거나, 단일 문장이면 그대로 add
        if (risk != null && !risk.isEmpty()) {
            for (String line : risk.split("\n")) {
                this.topRiskFactors.add(line.trim());
            }
        }

        if (advice != null && !advice.isEmpty()) {
            this.recommendedGoals.add(advice);
        }
    }

    // 데이터 추가 편의 메서드
    public void addTrendData(String data) { trendChartData.add(data); }
    public void addRiskFactor(String factor) { topRiskFactors.add(factor); }
    public void addGoal(String goal) { recommendedGoals.add(goal); }

    // Getter & Setter
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getPatientId() { return patientId; }
    public void setPeriod(LocalDateTime start, LocalDateTime end) {
        this.periodStart = start; this.periodEnd = end;
    }
    public void setSummaryText(String text) { this.summaryText = text; }
    public void setComplicationSummary(String summary) { this.complicationSummary = summary; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // [핵심] UI(Dialog)에서 텍스트로 보여주기 위한 포맷팅 메서드 (추가됨)
    public String getFormatText() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();

        sb.append("============================================\n");
        sb.append("       📄 개인 맞춤형 건강 리포트 (C_3)\n");
        sb.append("       발행일: ").append(createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).append("\n");
        if (periodStart != null && periodEnd != null) {
            sb.append("       분석 기간: ").append(periodStart.format(fmt)).append(" ~ ").append(periodEnd.format(fmt)).append("\n");
        }
        sb.append("============================================\n\n");

        sb.append("[1] 📋 종합 요약\n");
        sb.append("   \"").append(summaryText).append("\"\n\n");

        sb.append("[2] 📈 건강 변화 추이\n");
        if (trendChartData.isEmpty()) sb.append("   - 분석된 추세 데이터가 없습니다.\n");
        else for (String t : trendChartData) sb.append("   - ").append(t).append("\n");
        sb.append("\n");

        sb.append("[3] ⚠️ 발견된 위험 요인\n");
        if (topRiskFactors.isEmpty()) sb.append("   - 특별한 위험 요인이 발견되지 않았습니다. (양호)\n");
        else for (String r : topRiskFactors) sb.append("   - ").append(r).append("\n");
        sb.append("\n");

        if (complicationSummary != null && !complicationSummary.isEmpty()) {
            sb.append("[4] 📉 합병증 위험 분석\n");
            sb.append("   - ").append(complicationSummary).append("\n\n");
        }

        sb.append("[5] 🩺 닥터 AI의 권장 목표\n");
        if (recommendedGoals.isEmpty()) sb.append("   - 현재 상태 유지를 권장합니다.\n");
        else for (String g : recommendedGoals) sb.append("   - ").append(g).append("\n");

        sb.append("\n============================================");

        return sb.toString();
    }
}