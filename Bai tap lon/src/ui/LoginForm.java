package ui;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JButton btnRegister; // Thêm nút đăng ký

    private UserDAO userDAO;

    public LoginForm() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Clinic Management - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblTitle = new JLabel("LOGIN SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(80, 20, 240, 30);
        panel.add(lblTitle);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, 70, 80, 25);
        panel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(150, 70, 180, 25);
        panel.add(txtEmail);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 110, 80, 25);
        panel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 110, 180, 25);
        panel.add(txtPassword);

        // Nút Login
        btnLogin = new JButton("Login");
        btnLogin.setBounds(50, 160, 100, 30);
        panel.add(btnLogin);
        
        // Nút Exit
        btnExit = new JButton("Exit");
        btnExit.setBounds(250, 160, 100, 30);
        panel.add(btnExit);
        
        // ✅ Nút Register mới
        btnRegister = new JButton("Register");
        btnRegister.setBounds(150, 160, 100, 30);
        panel.add(btnRegister);


        add(panel);

        // Handle login event
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Handle exit event
        btnExit.addActionListener(e -> System.exit(0));
        
        // ✅ Handle register event
        btnRegister.addActionListener(e -> {
            new RegistrationForm().setVisible(true);
            dispose();
        });
    }

    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.");
            return;
        }

        User user = userDAO.login(email, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
            return;
        }

        // Login success
        JOptionPane.showMessageDialog(this, "Login successful as " + user.getRole());

        // Open dashboard according to role
        switch (user.getRole()) {
            case "admin":
                new AdminDashboard(user).setVisible(true);
                break;
            case "doctor":
                new DoctorDashboard(user).setVisible(true);
                break;
            case "patient":
                new PatientDashboard(user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown role: " + user.getRole());
                return;
        }

        // Close login form
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}