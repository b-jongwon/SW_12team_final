package presentation.controller;

import domain.service.AdminService;
import domain.content.Announcement;
import domain.content.ContentItem;
import java.util.List;

public class AdminController {

    private final AdminService service = new AdminService();

    // [수정] 공지사항 등록 연결
    public void postAnnouncement(String title, String content) {
        service.postAnnouncement(title, content);
    }

    public List<Announcement> getAnnouncements() {
        return service.getAnnouncements();
    }

    // 건강 콘텐츠 등록
    public void createContent(String category, String title, String desc, String targetRisk) {
        service.createContent(category, title, desc, targetRisk);
    }

    public List<ContentItem> getAllContents() {
        return service.getContents();
    }


    public void announce(String title, String content) {
        service.postAnnouncement(title, content);
    }
}