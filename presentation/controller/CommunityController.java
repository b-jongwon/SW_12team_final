
package presentation.controller;

import domain.content.Announcement;
import domain.content.ChecklistItem;
import domain.content.ContentItem;
import domain.service.CommunityService;
import domain.community.*;

import java.util.List;

public class CommunityController {

    private final CommunityService service = new CommunityService();

    public CommunityPost post(Long authorId, String title, String content) {
        return service.createPost(authorId, title, content);
    }

    public List<CommunityPost> listPosts() {
        return service.getPosts();
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
}
