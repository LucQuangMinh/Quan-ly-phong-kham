package dao;

import models.Doctor;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    // ✅ SỬA LỖI SQL: Chỉ chèn specialty_id và user_id
    public void create(Doctor doctor) {
        String sql = "INSERT INTO doctors (specialty_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctor.getSpecialtyId()); // specialty_id
            stmt.setInt(2, doctor.getUserId());     // user_id
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error creating doctor (DoctorDAO.create): " + e.getMessage());
        }
    }

    // Count all doctors
    public int count() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("❌ Error counting doctors: " + e.getMessage());
        }
        return 0;
    }

    // Get all doctors with specialty name (JOIN)
    public List<Doctor> getAllDoctors() {
        // Mặc định trả về tất cả, không lọc
        return findDoctorsByFilter("", 0); 
    }
    
    // ✅ HÀM ĐÃ SỬA: Tìm kiếm bác sĩ theo Tên và ID Chuyên khoa
    public List<Doctor> findDoctorsByFilter(String searchName, int specialtyId) {
        List<Doctor> doctors = new ArrayList<>();
        
        // Bắt đầu truy vấn với điều kiện cơ sở (luôn là TRUE)
        String sql = """
             SELECT d.id AS doctor_info_id, u.id AS user_id, u.name, u.email, s.name AS specialty
             FROM doctors d
             JOIN users u ON d.user_id = u.id
             LEFT JOIN specialties s ON d.specialty_id = s.id
             WHERE 1=1 
        """;
        
        boolean isSearchingByName = searchName != null && !searchName.trim().isEmpty();
        boolean isFilteringBySpecialty = specialtyId > 0;
        
        // Thêm điều kiện Tên/Email
        if (isSearchingByName) {
            sql += " AND (u.name LIKE ? OR u.email LIKE ?)";
        }
        
        // Thêm điều kiện lọc theo chuyên khoa
        if (isFilteringBySpecialty) {
            sql += " AND d.specialty_id = ?";
        }
        
        sql += " ORDER BY u.name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            String pattern = "%" + searchName.trim() + "%";
            int paramIndex = 1;

            if (isSearchingByName) {
                // Đặt tham số cho Tên và Email
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
            }
            
            if (isFilteringBySpecialty) {
                // Đặt tham số cho ID Chuyên khoa
                stmt.setInt(paramIndex++, specialtyId); 
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("user_id")); 
                    doctor.setName(rs.getString("name"));
                    doctor.setEmail(rs.getString("email"));
                    doctor.setSpecialtyName(rs.getString("specialty"));
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error searching doctors by filter: " + e.getMessage());
        }

        return doctors;
    }

    // ✅ Thêm hàm này để DatabaseSeeder gọi đúng
    public void insert(Doctor doctor) {
        create(doctor);
    }
}