package dao;

import models.Notification;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // Create new notification
    public boolean create(Notification n) {
        String sql = "INSERT INTO notifications (user_id, message, title, user_type, is_read, created_at, updated_at) VALUES (?, ?, ?, ?, 0, NOW(), NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, n.getUserId());
            stmt.setString(2, n.getMessage());
            stmt.setString(3, n.getTitle());
            stmt.setString(4, n.getUserType());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating notification: " + e.getMessage());
            return false;
        }
    }

    // Get all notifications by user id
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToNotification(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting notifications: " + e.getMessage());
        }
        return list;
    }

    // Mark notification as read
    public boolean markAsRead(int id) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error marking notification as read: " + e.getMessage());
            return false;
        }
    }

    // Delete notification
    public boolean delete(int id) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    // Map ResultSet to Notification object
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setMessage(rs.getString("message"));
        n.setTitle(rs.getString("title"));
        n.setUserType(rs.getString("user_type"));
        n.setIsRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        n.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return n;
    }
}
