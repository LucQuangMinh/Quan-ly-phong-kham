package ui;

import models.User;
import javax.swing.*;
import java.awt.*;

public class PatientDashboard extends JFrame {

    private User currentUser;
    private JButton btnBookAppointment, btnViewAppointments, btnNotifications, btnLogout;

    public PatientDashboard(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Patient Dashboard - " + currentUser.getName());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel lblWelcome = new JLabel("Welcome, " + currentUser.getName() + " (Patient)", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));

        btnBookAppointment = new JButton("Book Appointment");
        btnViewAppointments = new JButton("View My Appointments");
        btnNotifications = new JButton("View Notifications");
        btnLogout = new JButton("Logout");

        panel.add(lblWelcome);
        panel.add(btnBookAppointment);
        panel.add(btnViewAppointments);
        panel.add(btnNotifications);
        panel.add(btnLogout);

        add(panel);

        // ✅ Kích hoạt sự kiện xem thông báo
        btnNotifications.addActionListener(e -> {
            new NotificationDashboard(currentUser).setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
        
        btnBookAppointment.addActionListener(e -> {
            new DoctorListForm(currentUser).setVisible(true);
        });
        
        btnViewAppointments.addActionListener(e -> {
            new ViewAppointmentsForm(currentUser).setVisible(true);
        });

    }
}