package domain.content;

public class ContentItem {
    private Long id;
    private String category;
    private String title;
    private String description;

    // [NEW] 타겟 위험군 추가 ("ALL", "고위험", "주의", "정상")
    private String targetRisk;

    public ContentItem() {}

    // [수정] 생성 메서드에 targetRisk 추가
    public void create(String category, String title, String description, String targetRisk) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.targetRisk = targetRisk;
    }

    public ContentItem(Long id, String category, String title, String description, String targetRisk) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
        this.targetRisk = targetRisk;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // [NEW] Getter & Setter
    public String getTargetRisk() { return targetRisk; }
    public void setTargetRisk(String targetRisk) { this.targetRisk = targetRisk; }

    // [NEW] UI 리스트에 보여줄 요약 문구
    public String getSummary() {
        String badge = "ALL".equals(targetRisk) ? "[공통]" : "[" + targetRisk + " 전용]";
        return String.format("%s [%s] %s", badge, category, title);
    }
}