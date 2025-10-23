package ui;

import dao.AppointmentDAO;
import models.Appointment;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewAppointmentsForm extends JFrame {

    private User patient;
    private JTable table;
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public ViewAppointmentsForm(User patient) {
        this.patient = patient;
        initUI();
        loadAppointments();
    }

    private void initUI() {
        setTitle("My Appointments - " + patient.getName());
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ✅ SỬA LỖI: Chỉ định rõ các cột hiển thị (Bỏ ID)
        String[] columns = {"Doctor Name", "Date", "Time", "Status"}; 
        table = new JTable(new DefaultTableModel(columns, 0));
        table.getTableHeader().setReorderingAllowed(false);
        
        // Thiết lập kích thước cột (tùy chọn)
        table.getColumnModel().getColumn(0).setPreferredWidth(200); 

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);
    }

    private void loadAppointments() {
        List<Appointment> list = appointmentDAO.getAppointmentsByPatientId(patient.getId());
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Appointment a : list) {
            model.addRow(new Object[]{
                    // BỎ CỘT ID
                    a.getPatientName(), // Hiển thị tên Bác sĩ
                    a.getDate(),
                    a.getTime(),
                    a.getStatus()
            });
        }
    }
}