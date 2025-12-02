
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

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ChecklistItem(Long id, String text, boolean completed) {
        this.id = id;
        this.text = text;
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void toggle() {
        this.completed = !this.completed;
    }

    public void setId(Long id) { this.id = id; }
}
