package ui;

import dao.UserDAO;
import models.User;
import utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

public class RegistrationForm extends JFrame {

    private JTextField txtName, txtEmail;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnBack;

    private final UserDAO userDAO = new UserDAO();

    public RegistrationForm() {
        initUI();
    }

    private void initUI() {
        setTitle("Clinic Management - Register Patient");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);

        JLabel lblTitle = new JLabel("PATIENT REGISTRATION", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(50, 20, 300, 30);
        panel.add(lblTitle);

        int y = 70;
        int height = 25;
        int spacing = 40;

        // Name
        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(50, y, 80, height);
        panel.add(lblName);
        txtName = new JTextField();
        txtName.setBounds(150, y, 180, height);
        panel.add(txtName);
        y += spacing;

        // Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(50, y, 80, height);
        panel.add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(150, y, 180, height);
        panel.add(txtEmail);
        y += spacing;

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, y, 80, height);
        panel.add(lblPassword);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(150, y, 180, height);
        panel.add(txtPassword);
        y += spacing;

        // Register Button
        btnRegister = new JButton("Register");
        btnRegister.setBounds(80, y, 100, 30);
        btnRegister.addActionListener(e -> handleRegister());
        panel.add(btnRegister);

        // Back Button
        btnBack = new JButton("Back to Login");
        btnBack.setBounds(200, y, 130, 30);
        btnBack.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
        panel.add(btnBack);

        add(panel);
    }

    private void handleRegister() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        // Ràng buộc Email: Phải chứa "@" và có đuôi email hợp lệ
        // Mặc dù bạn yêu cầu @clinic.com, ta dùng kiểm tra cơ bản.
        // Nếu cần kiểm tra nghiêm ngặt: Pattern.matches(".*@clinic\\.com$", email.toLowerCase())
        if (!email.contains("@") || email.length() < 5) {
             JOptionPane.showMessageDialog(this, "Invalid email format!");
             return;
        }

        // Ràng buộc mật khẩu: Tối thiểu 6 ký tự
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long!");
            return;
        }

        // Kiểm tra email đã tồn tại chưa
        if (userDAO.findByEmail(email) != null) {
            JOptionPane.showMessageDialog(this, "Email already exists!");
            return;
        }

        // 1. Hash mật khẩu
        String hashedPassword = PasswordUtils.hashPassword(password);

        // 2. Tạo User object (vai trò luôn là patient)
        User newUser = new User(name, email, hashedPassword, "patient");

        // 3. Insert vào DB
        if (userDAO.insert(newUser)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
            
            // Mở lại form Login và đóng form đăng ký
            new LoginForm().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed due to a system error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}