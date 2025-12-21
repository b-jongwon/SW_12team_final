package data.repository;

import infra.BaseJsonRepository;
import infra.IdGenerator;
import domain.content.Announcement;
import domain.content.ContentItem;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.stream.Collectors; // import 추가

public class ContentRepository {

    private final BaseJsonRepository<ContentItem> contentRepo =
            new BaseJsonRepository<>("data/content_items.json",
                    new TypeToken<List<ContentItem>>() {}) {};

    private final BaseJsonRepository<Announcement> announcementRepo =
            new BaseJsonRepository<>("data/announcements.json",
                    new TypeToken<List<Announcement>>() {}) {};

    // --- 건강 콘텐츠 (ContentItem) ---
    public ContentItem saveContent(ContentItem item) {
        item.setId(IdGenerator.nextId("content_item"));
        contentRepo.save(item);
        return item;
    }

    public List<ContentItem> findAllContents() {
        return contentRepo.findAll();
    }



    public List<ContentItem> findContentsByRisk(String userRiskLevel) {
        return contentRepo.findAll().stream()
                .filter(item -> "ALL".equals(item.getTargetRisk()) ||
                        item.getTargetRisk().equals(userRiskLevel))
                .collect(Collectors.toList());
    }

    // --- 공지사항 (Announcement) ---
    public Announcement saveAnnouncement(Announcement a) {
        a.setId(IdGenerator.nextId("announcement"));
        announcementRepo.save(a);
        return a;
    }

    public List<Announcement> findAllAnnouncements() {
        return announcementRepo.findAll();
    }
}