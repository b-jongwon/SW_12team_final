
package domain.messaging;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private Long threadId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    public Message() {}

    public void send(Long senderId, String content) {
        this.senderId = senderId;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }

    public void markRead() {
        this.readAt = LocalDateTime.now();
    }

    public Long getThreadId() { return threadId; }
    public void setThreadId(Long t) { this.threadId = t; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public LocalDateTime getReadAt() { return readAt; }
}
