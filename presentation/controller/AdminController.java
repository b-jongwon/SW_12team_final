package presentation.controller;

import domain.service.AdminService;
import domain.content.Announcement;
import domain.content.ContentItem;
import java.util.List;

public class AdminController {

    private final AdminService service = new AdminService();

    public Announcement announce(String title, String content) {
        return service.postAnnouncement(title, content);
    }

    public List<Announcement> getAnnouncements() {
        return service.getAnnouncements();
    }

    public ContentItem addContent(String category, String title, String desc) {
        return service.createContent(category, title, desc);
    }

    public List<ContentItem> getContents() {
        return service.getContents();
    }
}