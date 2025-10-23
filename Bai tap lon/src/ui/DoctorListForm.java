package ui;

import dao.DoctorDAO;
import dao.SpecialtyDAO; // Import mới
import models.Doctor;
import models.Specialty; // Import mới
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorListForm extends JFrame {

    private JTable doctorTable;
    private JButton btnBook, btnClose;
    private JTextField txtSearch;
    private JComboBox<String> cmbSpecialty; // Combobox mới
    private User patient;
    private DefaultTableModel tableModel;
    
    private final SpecialtyDAO specialtyDAO = new SpecialtyDAO();
    private final Map<String, Integer> specialtyMap = new HashMap<>(); // Lưu tên -> ID

    public DoctorListForm(User patient) {
        this.patient = patient;
        loadSpecialtyData();
        initUI();
    }
    
    // Tải dữ liệu chuyên khoa vào Map
    private void loadSpecialtyData() {
        specialtyMap.put("All Specialties", 0); // Lựa chọn mặc định
        List<Specialty> specialties = specialtyDAO.findAll();
        for (Specialty s : specialties) {
            specialtyMap.put(s.getName(), s.getId());
        }
    }

    private void initUI() {
        setTitle("Select Doctor to Book Appointment");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== NORTH PANEL (Title + Search + Filter) =====
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        
        JLabel lblTitle = new JLabel("Available Doctors", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        northPanel.add(lblTitle, BorderLayout.NORTH);
        
        // Search & Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        txtSearch = new JTextField(20);
        cmbSpecialty = new JComboBox<>(specialtyMap.keySet().toArray(new String[0]));
        JButton btnFilter = new JButton("Filter");

        filterPanel.add(new JLabel("Search Name/Email:"));
        filterPanel.add(txtSearch);
        filterPanel.add(new JLabel("Specialty:"));
        filterPanel.add(cmbSpecialty);
        filterPanel.add(btnFilter);
        
        northPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(northPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL (Table) =====
        String[] columns = {"ID", "Name", "Email", "Specialty"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Load danh sách bác sĩ ban đầu (mặc định)
        loadDoctors();
        
        // Ẩn cột ID (Cột 0)
        doctorTable.getColumnModel().getColumn(0).setMaxWidth(0);
        doctorTable.getColumnModel().getColumn(0).setMinWidth(0);
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);

        // ===== SOUTH PANEL (Buttons) =====
        JPanel bottomPanel = new JPanel();
        btnBook = new JButton("Book Appointment");
        btnClose = new JButton("Close");
        bottomPanel.add(btnBook);
        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== Sự kiện =====
        // Sự kiện tìm kiếm/lọc
        btnFilter.addActionListener(e -> loadDoctors());
        txtSearch.addActionListener(e -> loadDoctors()); // Lọc khi nhấn Enter
        
        btnBook.addActionListener(e -> handleBook());
        btnClose.addActionListener(e -> dispose());
    }

    private void loadDoctors() {
        String searchTerm = txtSearch.getText().trim();
        String selectedSpecialtyName = (String) cmbSpecialty.getSelectedItem();
        int specialtyId = specialtyMap.getOrDefault(selectedSpecialtyName, 0); // Lấy ID chuyên khoa, 0 nếu là "All"

        tableModel.setRowCount(0); // clear table
        DoctorDAO doctorDAO = new DoctorDAO();
        // ✅ GỌI HÀM LỌC KẾT HỢP
        List<Doctor> doctors = doctorDAO.findDoctorsByFilter(searchTerm, specialtyId); 

        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{
                d.getId(), 
                d.getName(),
                d.getEmail(),
                d.getSpecialtyName() != null ? d.getSpecialtyName() : "N/A"
            });
        }
    }

    private void handleBook() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor first!");
            return;
        }

        // Lấy doctorId từ CỘT ẨN (Cột 0)
        int doctorId = (int) doctorTable.getValueAt(selectedRow, 0); 
        
        // Lấy tên bác sĩ từ cột hiển thị (Cột 1)
        String doctorName = (String) doctorTable.getValueAt(selectedRow, 1);

        // Mở form đặt lịch
        new BookAppointmentForm(patient, doctorId, doctorName).setVisible(true);
    }

    public static void main(String[] args) {
        // Test độc lập
        User dummyPatient = new User(1, "Test Patient", "test@mail.com", "123456", "patient");
        SwingUtilities.invokeLater(() -> new DoctorListForm(dummyPatient).setVisible(true));
    }
}