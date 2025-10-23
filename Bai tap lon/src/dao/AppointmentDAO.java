package dao;

import models.Appointment;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // 🔹 Tạo lịch hẹn mới
    public boolean create(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, date, time, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());

            LocalDate date = appointment.getDate();
            LocalTime time = appointment.getTime();
            
            stmt.setDate(3, date != null ? Date.valueOf(date) : null);
            stmt.setTime(4, time != null ? Time.valueOf(time) : null);

            stmt.setString(5, appointment.getStatus());
            
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error creating appointment (CRITICAL): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 🔹 Lấy danh sách lịch hẹn theo bác sĩ (có tên bệnh nhân)
    public List<Appointment> findByDoctorId(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = """
             SELECT a.*, u.name AS patient_name
             FROM appointments a
             JOIN users u ON a.patient_id = u.id
             WHERE a.doctor_id = ?
             ORDER BY a.date ASC, a.time ASC
         """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Appointment a = mapResultSetToAppointment(rs);
                a.setPatientName(rs.getString("patient_name"));
                list.add(a);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error getting appointments: " + e.getMessage());
        }

        return list;
    }
    
    // ✅ SỬA LỖI: Lấy danh sách lịch hẹn theo bệnh nhân (Bao gồm tên bác sĩ)
    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = """
             SELECT a.*, u.name AS doctor_name
             FROM appointments a
             JOIN users u ON a.doctor_id = u.id  -- JOIN để lấy tên bác sĩ
             WHERE a.patient_id = ? 
             ORDER BY a.date ASC, a.time ASC
         """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment a = mapResultSetToAppointment(rs);
                // Gán tên bác sĩ vào thuộc tính doctorName (giả định nó tồn tại trong model)
                // Cần thêm setDoctorName(String) vào Appointment.java nếu chưa có
                try {
                     a.setPatientName(rs.getString("doctor_name")); // Tạm thời dùng patientName field để lưu DoctorName
                } catch (Exception e) {
                     // Nếu setDoctorName chưa có, bạn có thể cần sửa Appointment.java
                }
                list.add(a);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting patient appointments: " + e.getMessage());
        }
        return list;
    }


    // 🔹 Cập nhật trạng thái lịch hẹn
    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating appointment status: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Kiểm tra bác sĩ có bận vào giờ đó không
    public boolean isDoctorBusy(int doctorId, LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND date = ? AND time = ? AND status = 'confirmed'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(time));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error checking doctor busy status: " + e.getMessage());
        }
        return false;
    }

    // 🔹 Tìm lịch hẹn theo ID
    public Appointment findById(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding appointment by id: " + e.getMessage());
        }
        return null;
    }

    // 🔹 Chuyển ResultSet → Appointment object
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        
        Date date = rs.getDate("date");
        Time time = rs.getTime("time");
        
        if (date != null) {
             a.setDate(date.toLocalDate());
        }
        if (time != null) {
             a.setTime(time.toLocalTime());
        }

        a.setStatus(rs.getString("status"));
        
        // Loại bỏ mapping Notes, RoomNumber, CancelReason khỏi ResultSet nếu chúng không tồn tại trong DB
        // Giữ lại try/catch để tránh lỗi nếu các cột này không tồn tại trong schema gốc
        try { a.setNotes(rs.getString("notes")); } catch (SQLException e) { a.setNotes(null); }
        try { a.setRoomNumber(rs.getString("room_number")); } catch (SQLException e) { a.setRoomNumber(null); }
        try { a.setCancelReason(rs.getString("cancel_reason")); } catch (SQLException e) { a.setCancelReason(null); }
        
        return a;
    }
}