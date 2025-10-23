package ui;

import dao.NotificationDAO;
import models.Notification;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class NotificationDashboard extends JFrame {

    private final User currentUser;
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private JTable table;

    public NotificationDashboard(User currentUser) {
        this.currentUser = currentUser;
        initUI();
        loadNotifications();
    }

    private void initUI() {
        setTitle("Notifications - " + currentUser.getName());
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"ID", "Title", "Message", "Status", "Created At"};
        table = new JTable(new DefaultTableModel(columns, 0));
        
        // Ẩn cột ID (cột 0) nhưng vẫn giữ trong Model
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);
    }

    private void loadNotifications() {
        // Tải thông báo (ĐÂY LÀ DANH SÁCH CHƯA ĐƯỢC CẬP NHẬT TRẠNG THÁI TRONG BỘ NHỚ)
        List<Notification> notifications = notificationDAO.getNotificationsByUser(currentUser.getId(), currentUser.getRole());
        
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No notifications found for this user.");
            return; 
        }

        // Hiển thị dữ liệu lên bảng (sử dụng trạng thái hiện tại của Notification object)
        displayNotifications(notifications);

        // Chạy ngầm để đánh dấu tất cả các thông báo CHƯA ĐỌC là đã đọc trong DB
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                boolean marked = false;
                for (Notification n : notifications) {
                    if (!n.isRead()) {
                        try {
                            notificationDAO.markAsRead(n.getId());
                            // ✅ QUAN TRỌNG: Cập nhật trạng thái trong bộ nhớ để UI biết
                            n.setRead(true); 
                            marked = true;
                        } catch (SQLException e) {
                            System.out.println("Error marking notification as read: " + e.getMessage());
                        }
                    }
                }
                // Nếu có bất kỳ thông báo nào được đánh dấu, trả về true để refresh UI
                return marked ? null : null; 
            }
            
            @Override
            protected void done() {
                // Sau khi đánh dấu đã đọc xong, refresh lại bảng để cập nhật cột Status
                try {
                    // Nếu cần refresh UI
                    displayNotifications(notifications);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    // Phương thức helper để hiển thị dữ liệu lên bảng
    private void displayNotifications(List<Notification> list) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ
        
        for (Notification n : list) {
            String status = n.isRead() ? "Read" : "Unread";
            
            model.addRow(new Object[]{
                    n.getId(),
                    n.getTitle(),
                    n.getMessage(),
                    status,
                    n.getCreatedAt()
            });
        }
        
        // Nếu có dữ liệu, form sẽ không hiển thị popup "No notifications found" lần thứ hai
    }
}