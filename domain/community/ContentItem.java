
package domain.community;

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

    public void setId(Long id) { this.id = id; }
}
