
package data.repository;

import domain.content.Announcement;
import domain.content.ChecklistItem;
import domain.content.ContentItem;
import infra.BaseJsonRepository;
import infra.IdGenerator;

import domain.community.*;

import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityRepository {

    private final BaseJsonRepository<CommunityPost> postRepo =
        new BaseJsonRepository<>("data/community_posts.json",
            new TypeToken<List<CommunityPost>>() {}) {};

    private final BaseJsonRepository<CommunityComment> commentRepo =
        new BaseJsonRepository<>("data/community_comments.json",
            new TypeToken<List<CommunityComment>>() {}) {};

    private final BaseJsonRepository<ContentItem> contentRepo =
        new BaseJsonRepository<>("data/content_items.json",
            new TypeToken<List<ContentItem>>() {}) {};

    private final BaseJsonRepository<ChecklistItem> checklistRepo =
        new BaseJsonRepository<>("data/checklist_items.json",
            new TypeToken<List<ChecklistItem>>() {}) {};

    private final BaseJsonRepository<Announcement> announcementRepo =
        new BaseJsonRepository<>("data/announcements.json",
            new TypeToken<List<Announcement>>() {}) {};

    // Post
    public CommunityPost savePost(CommunityPost p) {
        p.setId(IdGenerator.nextId("post"));
        postRepo.save(p);
        return p;
    }

    public List<CommunityPost> getPosts() {
        return postRepo.findAll();
    }

    // [NEW] 삭제 후 갱신된 리스트를 저장하기 위함
    public void saveAllPosts(List<CommunityPost> posts) {
        postRepo.saveAll(posts);
    }

    // Comment
    public CommunityComment saveComment(CommunityComment c) {
        c.setId(IdGenerator.nextId("comment"));
        commentRepo.save(c);
        return c;
    }

    public List<CommunityComment> getComments(Long postId) {
        return commentRepo.findAll().stream()
            .filter(c -> c.getPostId().equals(postId))
            .collect(Collectors.toList());
    }

    public CommunityPost findPostById(Long postId) {
        return postRepo.findAll().stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElse(null);
    }

    // Content
    public ContentItem saveContent(ContentItem item) {
        item.setId(IdGenerator.nextId("content_item"));
        contentRepo.save(item);
        return item;
    }

    public List<ContentItem> getContents() { return contentRepo.findAll(); }

    // Checklist
    public ChecklistItem saveChecklist(ChecklistItem item) {
        item.setId(IdGenerator.nextId("checklist"));
        checklistRepo.save(item);
        return item;
    }

    public List<ChecklistItem> getChecklist() { return checklistRepo.findAll(); }

    // Announcement
    public Announcement saveAnnouncement(Announcement a) {
        a.setId(IdGenerator.nextId("announcement"));
        announcementRepo.save(a);
        return a;
    }

    public List<Announcement> getAnnouncements() {
        return announcementRepo.findAll();
    }
}
