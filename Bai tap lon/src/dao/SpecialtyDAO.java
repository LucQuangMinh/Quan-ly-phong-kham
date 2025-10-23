package dao;

import models.Specialty;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialtyDAO {

    public void create(Specialty specialty) {
        String sql = "INSERT INTO specialties (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, specialty.getName());
            stmt.executeUpdate();
            System.out.println("✅ Specialty created: " + specialty.getName());
        } catch (SQLException e) {
            System.out.println("❌ Error creating specialty: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM specialties WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error checking specialty existence: " + e.getMessage());
        }
        return false;
    }

    public Specialty findByName(String name) {
        String sql = "SELECT * FROM specialties WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Specialty(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding specialty by name: " + e.getMessage());
        }
        return null;
    }

    public List<Specialty> findAll() {
        List<Specialty> list = new ArrayList<>();
        String sql = "SELECT * FROM specialties ORDER BY name ASC"; // Thêm ORDER BY cho dễ đọc
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Specialty(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching all specialties: " + e.getMessage());
        }
        return list;
    }

    // ✅ Hàm DatabaseSeeder gọi để chèn nếu chưa tồn tại
    public void insertIfNotExists(String name) {
        if (!existsByName(name)) {
            create(new Specialty(0, name));
        }
    }
}