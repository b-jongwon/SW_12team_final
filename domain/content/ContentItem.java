
package domain.content;

public class ContentItem {
    private Long id;
    private String category;
    private String title;
    private String description;

    public ContentItem() {}

    public void create(String category, String title, String description) {
        this.category = category;
        this.title = title;
        this.description = description;
    }

    public ContentItem(Long id, String category, String title, String description) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) { this.id = id; }
}
