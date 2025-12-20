package presentation.controller;

import domain.service.AdminService;
import domain.content.Announcement;
import domain.content.ContentItem;
import java.util.List;

public class AdminController {

    private final AdminService service = new AdminService();

    // [수정] 인자 4개 받도록 변경
    public void createContent(String category, String title, String desc, String targetRisk) {
        service.createContent(category, title, desc, targetRisk);
    }

    public List<ContentItem> getAllContents() {
        return service.getContents();
    }

    // 공지사항 메서드 이름 통일
    public void announce(String title, String content) {
        service.postAnnouncement(title, content);
    }
    public List<Announcement> getAnnouncements() {
        return service.getAnnouncements();
    }

    public void postAnnouncement(String text, String text1) {
    }
}