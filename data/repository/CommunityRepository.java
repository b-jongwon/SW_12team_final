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
import java.util.Iterator;
import java.util.List;

public class CommunityRepository {

    private static final String POST_FILE = "data/community_posts.json";
    private static final String COMMENT_FILE = "data/community_comments.json";
    private static final String CONTENT_FILE = "data/community_contents.json";
    private static final String CHECKLIST_FILE = "data/community_checklist.json";
    private static final String ANNOUNCEMENT_FILE = "data/announcements.json";

    // ------------------ 내부 공통 헬퍼 ------------------

    private <T> List<T> loadList(String path, Type type) {
        List<T> list = JsonUtil.readJson(path, type);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    private <T> void saveList(String path, List<T> list) {
        JsonUtil.writeJson(path, list);
    }

    // ------------------ 게시글(Post) ------------------

    public CommunityPost savePost(CommunityPost post) {
        Type type = new TypeToken<List<CommunityPost>>() {}.getType();
        List<CommunityPost> list = loadList(POST_FILE, type);
        if (post.getId() == null) {
            post.setId(IdGenerator.nextId("community_post"));
        }
        list.add(post);
        saveList(POST_FILE, list);
        return post;
    }

    public List<CommunityPost> getPosts() {
        Type type = new TypeToken<List<CommunityPost>>() {}.getType();
        return loadList(POST_FILE, type);
    }

    public CommunityPost findPostById(Long id) {
        Type type = new TypeToken<List<CommunityPost>>() {}.getType();
        List<CommunityPost> list = loadList(POST_FILE, type);
        for (CommunityPost p : list) {
            if (p.getId() != null && p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public void deletePost(Long postId) {
        Type type = new TypeToken<List<CommunityPost>>() {}.getType();
        List<CommunityPost> list = loadList(POST_FILE, type);
        list.removeIf(p -> p.getId() != null && p.getId().equals(postId));
        saveList(POST_FILE, list);
    }

    // ------------------ 댓글(Comment) ------------------

    public CommunityComment saveComment(CommunityComment comment) {
        Type type = new TypeToken<List<CommunityComment>>() {}.getType();
        List<CommunityComment> list = loadList(COMMENT_FILE, type);
        if (comment.getId() == null) {
            comment.setId(IdGenerator.nextId("community_comment"));
        }
        list.add(comment);
        saveList(COMMENT_FILE, list);
        return comment;
    }

    public List<CommunityComment> getComments(Long postId) {
        Type type = new TypeToken<List<CommunityComment>>() {}.getType();
        List<CommunityComment> list = loadList(COMMENT_FILE, type);
        List<CommunityComment> result = new ArrayList<>();
        for (CommunityComment c : list) {
            if (c.getPostId() != null && c.getPostId().equals(postId)) {
                result.add(c);
            }
        }
        return result;
    }

    public void deleteCommentsByPostId(Long postId) {
        Type type = new TypeToken<List<CommunityComment>>() {}.getType();
        List<CommunityComment> list = loadList(COMMENT_FILE, type);
        list.removeIf(c -> c.getPostId() != null && c.getPostId().equals(postId));
        saveList(COMMENT_FILE, list);
    }

    // ------------------ 건강 콘텐츠(ContentItem) ------------------

    public ContentItem saveContent(ContentItem item) {
        Type type = new TypeToken<List<ContentItem>>() {}.getType();
        List<ContentItem> list = loadList(CONTENT_FILE, type);
        if (item.getId() == null) {
            item.setId(IdGenerator.nextId("content_item"));
        }
        list.add(item);
        saveList(CONTENT_FILE, list);
        return item;
    }

    public List<ContentItem> getContents() {
        Type type = new TypeToken<List<ContentItem>>() {}.getType();
        return loadList(CONTENT_FILE, type);
    }

    // ------------------ 체크리스트(ChecklistItem) ------------------

    public ChecklistItem saveChecklist(ChecklistItem item) {
        Type type = new TypeToken<List<ChecklistItem>>() {}.getType();
        List<ChecklistItem> list = loadList(CHECKLIST_FILE, type);
        if (item.getId() == null) {
            item.setId(IdGenerator.nextId("checklist_item"));
        }
        list.add(item);
        saveList(CHECKLIST_FILE, list);
        return item;
    }

    public List<ChecklistItem> getChecklist() {
        Type type = new TypeToken<List<ChecklistItem>>() {}.getType();
        return loadList(CHECKLIST_FILE, type);
    }

    // ------------------ 공지사항(Announcement) ------------------

    public Announcement saveAnnouncement(Announcement a) {
        Type type = new TypeToken<List<Announcement>>() {}.getType();
        List<Announcement> list = loadList(ANNOUNCEMENT_FILE, type);
        if (a.getId() == null) {
            a.setId(IdGenerator.nextId("announcement"));
        }
        list.add(a);
        saveList(ANNOUNCEMENT_FILE, list);
        return a;
    }

    public List<Announcement> getAnnouncements() {
        Type type = new TypeToken<List<Announcement>>() {}.getType();
        return loadList(ANNOUNCEMENT_FILE, type);
    }
}
