package dao;

import models.Notification;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // 🔹 Tạo mới thông báo
    public boolean create(Notification n) {
        // Log thông báo đang được chèn
        System.out.println("Attempting to insert notification for user_id=" + n.getUserId() + " (" + n.getUserType() + ")");
        
        String sql = "INSERT INTO notifications (user_id, title, message, user_type, is_read, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, 0, NOW(), NOW())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.out.println("❌ DBConnection is NULL! Cannot insert notification.");
                return false;
            }

            stmt.setInt(1, n.getUserId());
            stmt.setString(2, n.getTitle());
            stmt.setString(3, n.getMessage());
            stmt.setString(4, n.getUserType());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Notification inserted successfully.");
                return true;
            } else {
                System.out.println("⚠️ Insert executed but no rows affected.");
                return false;
            }

        } catch (SQLException e) {
            // ✅ Báo cáo lỗi SQL chi tiết nhất nếu INSERT thất bại
            System.out.println("❌ CRITICAL SQL ERROR during notification creation: " + e.getMessage());
            e.printStackTrace(); // In stack trace để tìm lỗi chi tiết
            return false;
        }
    }

    // 🔹 Lấy tất cả thông báo theo user_id (Hàm truy vấn chính)
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (conn == null) return list;

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToNotification(rs));
            }

            return list;

        } catch (SQLException e) {
            System.out.println("❌ Error getting notifications: " + e.getMessage());
        }
        return list;
    }

    // 🔹 Lấy thông báo theo userId + userType (Được gọi từ Dashboard)
    public List<Notification> getNotificationsByUser(int userId, String userType) {
        System.out.println("🔍 Querying notifications for user_id=" + userId + ", userType=" + userType);
        
        List<Notification> list = getNotificationsByUserId(userId); 

        if (list.isEmpty()) {
            System.out.println("ℹ️ No notifications found for user_id=" + userId + " after query.");
        } else {
            System.out.println("📩 Loaded " + list.size() + " notifications for user_id=" + userId);
        }

        return list;
    }

    // 🔹 Xoá thông báo
    public boolean delete(int id) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Đánh dấu đã đọc
    public void markAsRead(int id) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("📖 Notification ID " + id + " marked as read.");
        }
    }

    // 🔹 Helper: Map ResultSet -> Notification
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setUserType(rs.getString("user_type"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime()
                : null);
        return n;
    }
}