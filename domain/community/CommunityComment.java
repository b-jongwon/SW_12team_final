
package domain.community;

import java.time.LocalDateTime;

public class CommunityComment {
    private Long id;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;

    public CommunityComment() {}

    public void create(Long postId, Long authorId, String content) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; }
}
