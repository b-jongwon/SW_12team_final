package data.repository;

import com.google.gson.reflect.TypeToken;
import domain.community.CommunityComment;
import domain.community.CommunityPost;
import domain.content.Announcement;
import domain.content.ChecklistItem;
import domain.content.ContentItem;
import infra.IdGenerator;
import infra.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 커뮤니티 관련 모든 엔티티(Post, Comment, Content, Checklist, Announcement)를
 * JSON 파일로 저장/조회하는 리포지토리.
 */
public class CommunityRepository {

    private static final String POST_FILE = "data/community_posts.json";
    private static final String COMMENT_FILE = "data/community_comments.json";
    private static final String CONTENT_FILE = "data/community_contents.json";
    private static final String CHECKLIST_FILE = "data/community_checklist.json";
    private static final String ANNOUNCE_FILE = "data/community_announcements.json";

    private static final Type POST_LIST_TYPE =
            new TypeToken<List<CommunityPost>>() {}.getType();
    private static final Type COMMENT_LIST_TYPE =
            new TypeToken<List<CommunityComment>>() {}.getType();
    private static final Type CONTENT_LIST_TYPE =
            new TypeToken<List<ContentItem>>() {}.getType();
    private static final Type CHECKLIST_LIST_TYPE =
            new TypeToken<List<ChecklistItem>>() {}.getType();
    private static final Type ANNOUNCE_LIST_TYPE =
            new TypeToken<List<Announcement>>() {}.getType();

    // ---------- Post ----------

    private List<CommunityPost> loadPosts() {
        List<CommunityPost> list = JsonUtil.readJson(POST_FILE, POST_LIST_TYPE);
        return list != null ? list : new ArrayList<>();
    }

    private void savePosts(List<CommunityPost> list) {
        JsonUtil.writeJson(POST_FILE, list);
    }

    public List<CommunityPost> getPosts() {
        return loadPosts();
    }

    public CommunityPost savePost(CommunityPost post) {
        List<CommunityPost> list = loadPosts();
        if (post.getId() == null) {
            post.setId(IdGenerator.nextId("post"));
            list.add(post);
        } else {
            boolean updated = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(post.getId())) {
                    list.set(i, post);
                    updated = true;
                    break;
                }
            }
            if (!updated) list.add(post);
        }
        savePosts(list);
        return post;
    }

    public CommunityPost findPostById(Long id) {
        return loadPosts().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void deletePost(Long id) {
        List<CommunityPost> list = loadPosts();
        list.removeIf(p -> p.getId().equals(id));
        savePosts(list);
    }

    // ---------- Comment ----------

    private List<CommunityComment> loadComments() {
        List<CommunityComment> list = JsonUtil.readJson(COMMENT_FILE, COMMENT_LIST_TYPE);
        return list != null ? list : new ArrayList<>();
    }

    private void saveComments(List<CommunityComment> list) {
        JsonUtil.writeJson(COMMENT_FILE, list);
    }

    public List<CommunityComment> getComments(Long postId) {
        List<CommunityComment> all = loadComments();
        List<CommunityComment> result = new ArrayList<>();
        for (CommunityComment c : all) {
            if (c.getPostId().equals(postId)) {
                result.add(c);
            }
        }
        return result;
    }

    public CommunityComment saveComment(CommunityComment comment) {
        List<CommunityComment> list = loadComments();
        if (comment.getId() == null) {
            comment.setId(IdGenerator.nextId("comment"));
            list.add(comment);
        } else {
            boolean updated = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(comment.getId())) {
                    list.set(i, comment);
                    updated = true;
                    break;
                }
            }
            if (!updated) list.add(comment);
        }
        saveComments(list);
        return comment;
    }

    public void deleteCommentsByPostId(Long postId) {
        List<CommunityComment> list = loadComments();
        list.removeIf(c -> c.getPostId().equals(postId));
        saveComments(list);
    }

    // ---------- Content ----------

    private List<ContentItem> loadContents() {
        List<ContentItem> list = JsonUtil.readJson(CONTENT_FILE, CONTENT_LIST_TYPE);
        return list != null ? list : new ArrayList<>();
    }

    private void saveContents(List<ContentItem> list) {
        JsonUtil.writeJson(CONTENT_FILE, list);
    }

    public List<ContentItem> getContents() {
        return loadContents();
    }

    public ContentItem saveContent(ContentItem item) {
        List<ContentItem> list = loadContents();
        if (item.getId() == null) {
            item.setId(IdGenerator.nextId("content"));
            list.add(item);
        } else {
            boolean updated = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(item.getId())) {
                    list.set(i, item);
                    updated = true;
                    break;
                }
            }
            if (!updated) list.add(item);
        }
        saveContents(list);
        return item;
    }

    // ---------- Checklist ----------

    private List<ChecklistItem> loadChecklist() {
        List<ChecklistItem> list = JsonUtil.readJson(CHECKLIST_FILE, CHECKLIST_LIST_TYPE);
        return list != null ? list : new ArrayList<>();
    }

    private void saveChecklist(List<ChecklistItem> list) {
        JsonUtil.writeJson(CHECKLIST_FILE, list);
    }

    public List<ChecklistItem> getChecklist() {
        return loadChecklist();
    }

    public ChecklistItem saveChecklist(ChecklistItem item) {
        List<ChecklistItem> list = loadChecklist();
        if (item.getId() == null) {
            item.setId(IdGenerator.nextId("checklist"));
            list.add(item);
        } else {
            boolean updated = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(item.getId())) {
                    list.set(i, item);
                    updated = true;
                    break;
                }
            }
            if (!updated) list.add(item);
        }
        saveChecklist(list);
        return item;
    }

    // ---------- Announcement ----------

    private List<Announcement> loadAnnouncements() {
        List<Announcement> list = JsonUtil.readJson(ANNOUNCE_FILE, ANNOUNCE_LIST_TYPE);
        return list != null ? list : new ArrayList<>();
    }

    private void saveAnnouncements(List<Announcement> list) {
        JsonUtil.writeJson(ANNOUNCE_FILE, list);
    }

    public List<Announcement> getAnnouncements() {
        return loadAnnouncements();
    }

    public Announcement saveAnnouncement(Announcement item) {
        List<Announcement> list = loadAnnouncements();
        if (item.getId() == null) {
            item.setId(IdGenerator.nextId("announcement"));
            list.add(item);
        } else {
            boolean updated = false;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(item.getId())) {
                    list.set(i, item);
                    updated = true;
                    break;
                }
            }
            if (!updated) list.add(item);
        }
        saveAnnouncements(list);
        return item;
    }
}
