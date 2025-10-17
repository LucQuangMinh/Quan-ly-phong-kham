package dao;

import models.Appointment;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    // Create a new appointment
    public boolean create(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_time, notes, status, room_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getAppointmentTime()));
            stmt.setString(4, appointment.getNotes());
            stmt.setString(5, appointment.getStatus());
            stmt.setString(6, appointment.getRoomNumber());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating appointment: " + e.getMessage());
            return false;
        }
    }

    // Get all appointments of a doctor
    public List<Appointment> getAppointmentsByDoctorId(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting doctor appointments: " + e.getMessage());
        }
        return list;
    }

    // Get all appointments of a patient
    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_time ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting patient appointments: " + e.getMessage());
        }
        return list;
    }

    // Update appointment status
    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating appointment status: " + e.getMessage());
            return false;
        }
    }

    // Check if doctor is busy at a given time
    public boolean isDoctorBusy(int doctorId, LocalDateTime time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_time = ? AND status = 'confirmed'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(time));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking doctor busy status: " + e.getMessage());
        }
        return false;
    }

    // Get appointment by id
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
            System.out.println("Error finding appointment by id: " + e.getMessage());
        }
        return null;
    }

    // Map ResultSet to Appointment object
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setAppointmentTime(rs.getTimestamp("appointment_time").toLocalDateTime());
        a.setNotes(rs.getString("notes"));
        a.setStatus(rs.getString("status"));
        a.setRoomNumber(rs.getString("room_number"));
        a.setCancelReason(rs.getString("cancel_reason"));
        return a;
    }
}
