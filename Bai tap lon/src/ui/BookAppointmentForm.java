package ui;

import services.AppointmentService; // Import Service
import models.Appointment;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class BookAppointmentForm extends JFrame {
    private final User patient;
    private final int doctorId;
    private final String doctorName;

    private JTextField txtDate;
    private JTextField txtTime;
    private JButton btnConfirm, btnCancel;
    
    private final AppointmentService appointmentService = new AppointmentService(); // Khai báo Service

    public BookAppointmentForm(User patient, int doctorId, String doctorName) {
        this.patient = patient;
        this.doctorId = doctorId;
        this.doctorName = doctorName;

        initUI();
    }
    
    // ... (Giữ nguyên initUI)
    private void initUI() {
        setTitle("Book Appointment - Dr. " + doctorName);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10));

        JLabel lblDoctor = new JLabel("Doctor: " + doctorName, SwingConstants.CENTER);
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):", SwingConstants.CENTER);
        txtDate = new JTextField(LocalDate.now().toString());

        JLabel lblTime = new JLabel("Time (HH:MM):", SwingConstants.CENTER);
        txtTime = new JTextField("09:00");

        btnConfirm = new JButton("Confirm Appointment");
        btnCancel = new JButton("Cancel");

        add(lblDoctor);
        add(lblDate);
        add(txtDate);
        add(lblTime);
        add(txtTime);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnConfirm);
        btnPanel.add(btnCancel);
        add(btnPanel);

        // Sự kiện
        btnConfirm.addActionListener(e -> confirmBookingAsync());
        btnCancel.addActionListener(e -> dispose());
    }

    /**
     * ✅ Chạy xác nhận lịch hẹn trong luồng nền (không chặn UI)
     */
    private void confirmBookingAsync() {
        btnConfirm.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    LocalDate date = LocalDate.parse(txtDate.getText().trim());
                    LocalTime time = LocalTime.parse(txtTime.getText().trim());

                    Appointment appointment = new Appointment();
                    appointment.setDoctorId(doctorId);
                    appointment.setPatientId(patient.getId());
                    appointment.setDate(date);
                    appointment.setTime(time);
                    appointment.setStatus("pending");

                    // ✅ SỬA LỖI: Gọi AppointmentService thay vì AppointmentDAO
                    return appointmentService.createAppointment(appointment);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(BookAppointmentForm.this,
                            "Error booking appointment:\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            @Override
            protected void done() {
                btnConfirm.setEnabled(true);
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(BookAppointmentForm.this,
                                "Appointment booked successfully!\nDr. " + doctorName +
                                        "\nDate: " + txtDate.getText() +
                                        "\nTime: " + txtTime.getText(),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        // Test độc lập
        User dummyPatient = new User(1, "Test Patient", "test@mail.com", "123456", "patient");
        SwingUtilities.invokeLater(() -> new DoctorListForm(dummyPatient).setVisible(true));
    }
}