package domain.service;

import data.repository.CommunityRepository;
import domain.community.CommunityComment;
import domain.community.CommunityPost;
import domain.content.Announcement;
import domain.content.ChecklistItem;
import domain.content.ContentItem;
import domain.user.User;

import java.util.List;

public class CommunityService {

    private final CommunityRepository repo = new CommunityRepository();

    // 게시글 생성
    public CommunityPost createPost(Long authorId, String authorName, String title, String content) {
        CommunityPost p = new CommunityPost();
        p.create(authorId, title, authorName, content);
        return repo.savePost(p);
    }

    public List<CommunityPost> getPosts() {
        return repo.getPosts();
    }

    public CommunityPost getPost(Long postId) {
        return repo.findPostById(postId);
    }

    public CommunityComment addComment(Long postId, Long authorId, String content) {
        CommunityComment c = new CommunityComment();
        c.create(postId, authorId, content);
        return repo.saveComment(c);
    }

    public List<CommunityComment> getComments(Long postId) {
        return repo.getComments(postId);
    }

    // [수정] 인자 4개로 변경 (targetRisk 추가) -> 오류 해결
    public ContentItem createContent(String category, String title, String description, String targetRisk) {
        ContentItem item = new ContentItem();
        // ContentItem의 변경된 create 메서드(인자 4개)에 맞춰 호출
        item.create(category, title, description, targetRisk);
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

    // 게시글 삭제 비즈니스 로직
    public boolean deletePost(Long postId, User requester) {
        CommunityPost post = repo.findPostById(postId);
        if (post == null) return false;

        String role = requester.getRole();
        Long requesterId = requester.getId();

        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        boolean isAuthor = post.getAuthorId() != null &&
                post.getAuthorId().equals(requesterId);

        if (!isAdmin && !isAuthor) {
            return false;
        }

        repo.deletePost(postId);
        repo.deleteCommentsByPostId(postId);
        return true;
    }
}