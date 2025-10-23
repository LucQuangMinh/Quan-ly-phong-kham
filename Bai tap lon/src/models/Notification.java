package models;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String userType;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(int id, int userId, String userType, String title, String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userType = userType;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // ✅ SỬA LỖI: Cập nhật thứ tự tham số để khớp với AppointmentService.java
    // Thứ tự mong muốn: (userId, title, message, userType)
    public Notification(int userId, String title, String message, String userType) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.userType = userType;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}