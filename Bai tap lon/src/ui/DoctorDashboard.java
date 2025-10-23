package ui;

import dao.AppointmentDAO;
import models.Appointment;
import models.User;
import services.AppointmentService; // Cần import Service

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorDashboard extends JFrame {

    private User doctor;
    private JTable tblAppointments;
    private JButton btnConfirm, btnReject, btnLogout;
    private final AppointmentService appointmentService = new AppointmentService(); // Khai báo Service

    public DoctorDashboard(User doctor) {
        this.doctor = doctor;
        initUI();
        loadAppointments(); // 🔥 load danh sách lịch hẹn với bác sĩ này
    }

    private void initUI() {
        setTitle("Doctor Dashboard - " + doctor.getUsername());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Welcome, Dr. " + doctor.getUsername(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(lblTitle, BorderLayout.CENTER);

        btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
        topPanel.add(btnLogout, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ===== TABLE OF APPOINTMENTS =====
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Patient Name", "Date", "Time", "Status"}, 0);
        tblAppointments = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblAppointments);
        add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel();
        btnConfirm = new JButton("Confirm");
        btnReject = new JButton("Reject");
        btnPanel.add(btnConfirm);
        btnPanel.add(btnReject);
        add(btnPanel, BorderLayout.SOUTH);

        // ===== EVENT HANDLERS =====
        btnConfirm.addActionListener(e -> updateAppointmentStatus("confirmed"));
        btnReject.addActionListener(e -> updateAppointmentStatus("rejected"));
    }

    // 🔹 Load danh sách lịch hẹn theo bác sĩ
    private void loadAppointments() {
        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            List<Appointment> appointments = appointmentDAO.findByDoctorId(doctor.getId());

            DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
            model.setRowCount(0);

            for (Appointment a : appointments) {
                model.addRow(new Object[]{
                        a.getId(),
                        a.getPatientName(),
                        a.getDate(),
                        a.getTime(),
                        a.getStatus()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔹 Cập nhật trạng thái lịch hẹn (confirm / reject)
    private void updateAppointmentStatus(String newStatus) {
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int appointmentId = (int) tblAppointments.getValueAt(selectedRow, 0);

        try {
            boolean success = false;
            
            if (newStatus.equals("confirmed")) {
                // ✅ SỬA LỖI: Gọi Service để xác nhận (kích hoạt logic thông báo)
                success = appointmentService.confirmAppointment(appointmentId, doctor.getId());
                
            } else if (newStatus.equals("rejected")) {
                // Lấy lý do từ chối
                String reason = JOptionPane.showInputDialog(this, "Enter rejection reason:");
                if (reason == null || reason.trim().isEmpty()) return;
                
                // ✅ SỬA LỖI: Gọi Service để từ chối (kích hoạt logic thông báo)
                success = appointmentService.rejectAppointment(appointmentId, doctor.getId(), reason);
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Appointment has been " + newStatus + " successfully!");
                loadAppointments(); // refresh bảng
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update appointment status. Doctor may be busy or appointment not found.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }
}