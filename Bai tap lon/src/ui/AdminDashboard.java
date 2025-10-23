package ui;

import models.User;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private User user;
    private JButton btnCreateDoctor, btnLogout;

    public AdminDashboard(User user) {
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("Admin Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Welcome, Admin: " + user.getUsername(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        // Nút tạo bác sĩ
        btnCreateDoctor = new JButton("Create New Doctor");
        btnCreateDoctor.setPreferredSize(new Dimension(200, 40));
        btnCreateDoctor.addActionListener(e -> new CreateDoctorForm().setVisible(true));
        
        // Nút Logout
        btnLogout = new JButton("Logout");
        btnLogout.setPreferredSize(new Dimension(200, 40));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        panel.add(btnCreateDoctor);
        panel.add(btnLogout);
        
        add(panel, BorderLayout.CENTER);
    }
}