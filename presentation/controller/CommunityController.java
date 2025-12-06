package presentation.controller;

import data.repository.UserRepository;
import domain.community.CommunityComment;
import domain.community.CommunityPost;
import domain.content.Announcement;
import domain.content.ChecklistItem;
import domain.content.ContentItem;
import domain.service.CommunityService;
import domain.user.User;

import java.util.List;
import java.util.Optional;

public class CommunityController {

    private final CommunityService service = new CommunityService();
    private final UserRepository userRepo = new UserRepository();

    public CommunityPost post(Long authorId, String authorName,String title, String content) {
        return service.createPost(authorId, authorName,title, content);
    }

    public List<CommunityPost> listPosts() {
        return service.getPosts();
    }

    public CommunityPost getPost(Long postId) {
        return service.getPost(postId);
    }

    public CommunityComment comment(Long postId, Long authorId, String content) {
        return service.addComment(postId, authorId, content);
    }

    public List<CommunityComment> listComments(Long postId) {
        return service.getComments(postId);
    }

    public ContentItem addContent(String category, String title, String desc) {
        return service.createContent(category, title, desc);
    }

    public List<ContentItem> getContents() {
        return service.getContents();
    }

    public ChecklistItem addChecklist(String text) {
        return service.addChecklistItem(text);
    }

    public List<ChecklistItem> getChecklist() {
        return service.getChecklist();
    }

    public Announcement announce(String title, String content) {
        return service.createAnnouncement(title, content);
    }

    public List<Announcement> listAnnouncements() {
        return service.getAnnouncements();
    }

    // -------------------- 새로 추가된 부분 --------------------

    /** 게시글 삭제 (권한 검사는 Service에서 수행) */
    public boolean deletePost(Long postId, User requester) {
        return service.deletePost(postId, requester);
    }

    /** 작성자 표시용 라벨 (예: "홍길동 (환자)") */
    public String getUserLabel(Long userId) {
        if (userId == null) return "알 수 없음";

        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) return "알 수 없음";

        User u = userOpt.get();
        String roleKo;
        switch (u.getRole()) {
            case "PATIENT":   roleKo = "환자"; break;
            case "DOCTOR":    roleKo = "의사"; break;
            case "CAREGIVER": roleKo = "보호자"; break;
            case "ADMIN":     roleKo = "관리자"; break;
            default:          roleKo = u.getRole(); break;
        }
        return u.getName() + " (" + roleKo + ")";
    }
}
