package domain.service;

import data.repository.ContentRepository;
import domain.content.Announcement;
import domain.content.ContentItem;
import java.util.List;

public class AdminService {

    private final ContentRepository repo = new ContentRepository();

    // 공지사항 등록
    public Announcement postAnnouncement(String title, String content) {
        Announcement a = new Announcement();
        a.create(title, content);
        return repo.saveAnnouncement(a);
    }

    public List<Announcement> getAnnouncements() {
        return repo.findAllAnnouncements();
    }

    // [수정] 건강 콘텐츠 등록 (targetRisk 인자 추가)
    public ContentItem createContent(String category, String title, String desc, String targetRisk) {
        ContentItem item = new ContentItem();
        item.create(category, title, desc, targetRisk); // 수정된 create 호출
        return repo.saveContent(item);
    }

    public List<ContentItem> getContents() {
        return repo.findAllContents();
    }
}