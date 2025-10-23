package ui;

import dao.DoctorDAO;
import dao.SpecialtyDAO;
import dao.UserDAO;
import models.Doctor;
import models.Specialty;
import models.User;
import utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateDoctorForm extends JFrame {

    private JTextField txtName, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbSpecialty;
    private JButton btnCreate, btnCancel;

    private final UserDAO userDAO = new UserDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final SpecialtyDAO specialtyDAO = new SpecialtyDAO();
    private final Map<String, Integer> specialtyMap = new HashMap<>();

    public CreateDoctorForm() {
        loadSpecialties();
        initUI();
    }
    
    private void loadSpecialties() {
        List<Specialty> specialties = specialtyDAO.findAll();
        for (Specialty s : specialties) {
            specialtyMap.put(s.getName(), s.getId());
        }
    }

    private void initUI() {
        setTitle("Admin - Create New Doctor");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("CREATE NEW DOCTOR ACCOUNT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Name
        formPanel.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        // Email
        formPanel.add(new JLabel("Email (Doctor):"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        // Password
        formPanel.add(new JLabel("Password (Min 6 chars):"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        // Specialty
        formPanel.add(new JLabel("Specialty:"));
        cmbSpecialty = new JComboBox<>(specialtyMap.keySet().toArray(new String[0]));
        formPanel.add(cmbSpecialty);
        
        // Empty row for spacing
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnCreate = new JButton("Create Doctor");
        btnCreate.addActionListener(e -> handleCreate());
        
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        
        btnPanel.add(btnCreate);
        btnPanel.add(btnCancel);
        
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private void handleCreate() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String specialtyName = (String) cmbSpecialty.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || specialtyName == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and select a specialty!");
            return;
        }

        if (!Pattern.matches(".*@.*\\..*", email)) {
             JOptionPane.showMessageDialog(this, "Invalid email format!");
             return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long!");
            return;
        }
        
        if (userDAO.findByEmail(email) != null) {
            JOptionPane.showMessageDialog(this, "Email already exists!");
            return;
        }
        
        try {
            int specialtyId = specialtyMap.get(specialtyName);
            String hashedPassword = PasswordUtils.hashPassword(password);

            // 1. Tạo User (Vai trò: doctor)
            User newUser = new User(name, email, hashedPassword, "doctor");
            
            if (userDAO.insert(newUser)) {
                
                // 2. Tạo Doctor (Liên kết đến User vừa tạo và Chuyên khoa)
                Doctor newDoctor = new Doctor(name, email, specialtyName);
                newDoctor.setUserId(newUser.getId());
                newDoctor.setSpecialtyId(specialtyId);

                doctorDAO.insert(newDoctor);

                JOptionPane.showMessageDialog(this, "Doctor account created successfully!");
                dispose();
            } else {
                 JOptionPane.showMessageDialog(this, "Failed to create user account.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred during creation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}