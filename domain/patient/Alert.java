
package domain.patient;

import java.time.LocalDateTime;

public class Alert {
    private Long id;
    private Long userId;
    private String message;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Alert(Long id, Long userId, String message, LocalDateTime createdAt, boolean read) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    private LocalDateTime createdAt;
    private boolean read;

    public Alert() {}

    public void create(Long userId, String message) {
        this.userId = userId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public void markRead() {
        this.read = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }
}
