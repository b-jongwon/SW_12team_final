
package domain.service;

import data.repository.CommunityRepository;
import domain.community.*;
import domain.user.User;
import domain.content.Announcement;
import domain.content.ChecklistItem;
import domain.content.ContentItem;

import java.util.List;
import java.util.Optional;

public class CommunityService {

    private final CommunityRepository repo = new CommunityRepository();

    public CommunityPost createPost(Long authorId, String authorName, String title, String content) {
        CommunityPost p = new CommunityPost();
        p.create(authorId, authorName, title, content);
        return repo.savePost(p);
    }

    public void deletePost(Long postId, User requester) {
        // 1. 게시글 조회
        Optional<CommunityPost> postOpt = repo.getPosts().stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst();

        if (postOpt.isPresent()) {
            CommunityPost post = postOpt.get();

            // 2. 권한 검사: 관리자(ADMIN)이거나 작성자 본인인 경우만 허용
            boolean isAdmin = "ADMIN".equals(requester.getRole());
            boolean isAuthor = post.getAuthorId().equals(requester.getId());

            if (isAdmin || isAuthor) {
                // 3. 삭제 수행 (메모리 리스트에서 제거 후 전체 저장 방식)
                List<CommunityPost> allPosts = repo.getPosts();
                allPosts.removeIf(p -> p.getId().equals(postId));
                repo.saveAllPosts(allPosts); // Repository에 추가된 메서드 호출
            } else {
                throw new IllegalStateException("삭제 권한이 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
    }

    public List<CommunityPost> getPosts() {
        return repo.getPosts();
    }

    public CommunityComment addComment(Long postId, Long authorId, String content) {
        CommunityComment c = new CommunityComment();
        c.create(postId, authorId, content);
        return repo.saveComment(c);
    }

    public List<CommunityComment> getComments(Long postId) {
        return repo.getComments(postId);
    }


    public ContentItem createContent(String category, String title, String description) {
        ContentItem item = new ContentItem();
        item.create(category, title, description);
        return repo.saveContent(item);
    }

    public List<ContentItem> getContents() {
        return repo.getContents();
    }

    public ChecklistItem addChecklistItem(String text) {
        ChecklistItem item = new ChecklistItem();
        item.create(text);
        return repo.saveChecklist(item);
    }

    public List<ChecklistItem> getChecklist() {
        return repo.getChecklist();
    }

    public Announcement createAnnouncement(String title, String content) {
        Announcement a = new Announcement();
        a.create(title, content);
        return repo.saveAnnouncement(a);
    }

    public List<Announcement> getAnnouncements() {
        return repo.getAnnouncements();
    }
}
