
package domain.community;

public class ChecklistItem {
    private Long id;
    private String text;
    private boolean completed;

    public ChecklistItem() {}

    public void create(String text) {
        this.text = text;
        this.completed = false;
    }

    public void toggle() {
        this.completed = !this.completed;
    }

    public void setId(Long id) { this.id = id; }
}
