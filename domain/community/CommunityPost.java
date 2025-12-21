package domain.community;

import java.time.LocalDateTime;

public class CommunityPost {
    private Long id;
    private Long authorId;
    private String authorName;   // 실명 저장
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public CommunityPost() {}

    public CommunityPost(Long id, Long authorId, String authorName,
                         String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }


    public void create(Long authorId, String title, String authorName, String content) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // --- getters / setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) { this.id = id; }

    public Long getAuthorId() {
        return authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
