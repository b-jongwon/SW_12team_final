
package domain.community;

import java.time.LocalDateTime;

public class CommunityPost {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public CommunityPost() {}

    public void create(Long authorId, String title, String content) {
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void setId(Long id) { this.id = id; }
    public Long getAuthorId() { return authorId; }
    public Long getId() {      // ★★★ Main.java에서 필요
        return id;
    }
}
