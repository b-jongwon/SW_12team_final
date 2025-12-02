
package domain.community;

import java.time.LocalDateTime;

public class Announcement {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public Announcement() {}

    public void create(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void setId(Long id) { this.id = id; }
}
