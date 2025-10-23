package utils;

import dao.SpecialtyDAO;
import dao.DoctorDAO;
import dao.UserDAO;
import models.Specialty;
import models.Doctor;
import models.User;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class DatabaseSeeder {

    public void seed() {
        try {
            // 🔹 Xoá dữ liệu cũ và ĐẶT LẠI AUTO_INCREMENT
            try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement()) {
                
                // Tắt ràng buộc khóa ngoại (cần thiết cho TRUNCATE)
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0"); 
                
                // ✅ Dùng TRUNCATE để xóa và reset AUTO_INCREMENT về 1
                st.executeUpdate("TRUNCATE TABLE appointments");
                st.executeUpdate("TRUNCATE TABLE notifications");
                st.executeUpdate("TRUNCATE TABLE doctors");
                st.executeUpdate("TRUNCATE TABLE users");
                st.executeUpdate("TRUNCATE TABLE specialties"); 
                
                // Bật lại ràng buộc khóa ngoại
                st.executeUpdate("SET FOREIGN_KEY_CHECKS = 1"); 
                
                System.out.println("✅ Da xoa du lieu cu va reset AUTO_INCREMENT.");
            } catch (Exception ex) {
                System.out.println("❌ Khong the xoa du lieu cu: " + ex.getMessage());
            }

            SpecialtyDAO specialtyDAO = new SpecialtyDAO();
            DoctorDAO doctorDAO = new DoctorDAO();
            UserDAO userDAO = new UserDAO();

            // 1. Seed chuyên khoa
            List<String> specialties = Arrays.asList(
                    "Khoa tim mach",
                    "Khoa da lieu",
                    "Khoa than kinh",
                    "Khoa noi tong quat",
                    "Khoa tai mui hong"
            );

            for (String name : specialties) {
                specialtyDAO.insertIfNotExists(name); 
            }

            // 2. Seed admin mặc định
            if (userDAO.findByEmail("admin@clinic.com") == null) {
                userDAO.insert(new User("Admin", "admin@clinic.com", PasswordUtils.hashPassword("123456"), "admin"));
                System.out.println("✅ Da tao admin mac dinh (email: admin@clinic.com, pass: 123456)");
            }

            // 3. Seed bác sĩ mẫu (5 bác sĩ/chuyên khoa, tổng 25 bác sĩ)
            List<Doctor> doctors = Arrays.asList(
                // --- Khoa tim mach (ID 1) ---
                new Doctor("TS.BS. Chu Trong Hiep", "hiep@clinic.com", "Khoa tim mach"),
                new Doctor("ThS.BS. Pham Minh Cuong", "cuong@clinic.com", "Khoa tim mach"),
                new Doctor("BS. Nguyen Van Toan", "toan.nv@clinic.com", "Khoa tim mach"),
                new Doctor("PGS.TS. Le Thi Hong", "hong.le@clinic.com", "Khoa tim mach"),
                new Doctor("BS. Dinh Van Bach", "bach.dv@clinic.com", "Khoa tim mach"),

                // --- Khoa da lieu (ID 2) ---
                new Doctor("ThS.BS. Tran Van Hung", "hung.tv@clinic.com", "Khoa da lieu"),
                new Doctor("BS.CKI. Nguyen Thi My", "my.nt@clinic.com", "Khoa da lieu"),
                new Doctor("BS. Pham Quoc Viet", "viet.pq@clinic.com", "Khoa da lieu"),
                new Doctor("BS. Doan Thi Thuy", "thuy.dt@clinic.com", "Khoa da lieu"),
                new Doctor("TS.BS. Ha Cong Hau", "hau.hc@clinic.com", "Khoa da lieu"),

                // --- Khoa than kinh (ID 3) ---
                new Doctor("BS. Nguyen Thi Tuyet Mai", "mai.nt@clinic.com", "Khoa than kinh"),
                new Doctor("GS.TS. Nguyen Quoc Anh", "anh.nq@clinic.com", "Khoa than kinh"),
                new Doctor("BS.CKII. Vu Thi Lan", "lan.vt@clinic.com", "Khoa than kinh"),
                new Doctor("BS. Tran Minh Phuc", "phuc.tm@clinic.com", "Khoa than kinh"),
                new Doctor("ThS.BS. Duong Quynh Trang", "trang.dq@clinic.com", "Khoa than kinh"),

                // --- Khoa noi tong quat (ID 4) ---
                new Doctor("TS.BS. Mai Anh Kiet", "kiet.ma@clinic.com", "Khoa noi tong quat"),
                new Doctor("BS. Huynh Tan Loc", "loc.ht@clinic.com", "Khoa noi tong quat"),
                new Doctor("BS. Le Van Khanh", "khanh.lv@clinic.com", "Khoa noi tong quat"),
                new Doctor("BS.CKI. Phan Thi Diep", "diep.pt@clinic.com", "Khoa noi tong quat"),
                new Doctor("ThS.BS. Vo Thanh Danh", "danh.vt@clinic.com", "Khoa noi tong quat"),

                // --- Khoa tai mui hong (ID 5) ---
                new Doctor("BS. Phan Phuoc Hoang Minh", "minh.pph@clinic.com", "Khoa tai mui hong"),
                new Doctor("BS. Nguyen Van Dat", "dat.nv@clinic.com", "Khoa tai mui hong"),
                new Doctor("BS.CKI. Bui Thi Hien", "hien.bt@clinic.com", "Khoa tai mui hong"),
                new Doctor("ThS.BS. Nguyen Duc Thinh", "thinh.nd@clinic.com", "Khoa tai mui hong"),
                new Doctor("BS. Hoang Minh Tri", "tri.hm@clinic.com", "Khoa tai mui hong")
            );

            for (Doctor doc : doctors) {
                if (userDAO.findByEmail(doc.getEmail()) == null) {
                    Specialty sp = specialtyDAO.findByName(doc.getSpecialtyName());
                    if (sp != null) {
                        // 1. Tạo user
                        User user = new User(doc.getName(), doc.getEmail(), PasswordUtils.hashPassword("123456"), "doctor");
                        userDAO.insert(user); 

                        // 2. Tạo bác sĩ
                        doc.setSpecialtyId(sp.getId());
                        doc.setUserId(user.getId());
                        doctorDAO.insert(doc);
                        System.out.println("✅ Da tao bac si: " + doc.getName());
                    }
                }
            }
            
            // 4. Seed bệnh nhân mẫu
            List<User> patients = Arrays.asList(
                new User("Nguyen Van A", "a@clinic.com", PasswordUtils.hashPassword("123456"), "patient"),
                new User("Tran Thi B", "b@clinic.com", PasswordUtils.hashPassword("123456"), "patient"),
                new User("Le Van C", "c@clinic.com", PasswordUtils.hashPassword("123456"), "patient")
            );

            for (User patient : patients) {
                if (userDAO.findByEmail(patient.getEmail()) == null) {
                    userDAO.insert(patient);
                    System.out.println("️Da tao benh nhan: " + patient.getName());
                }
            }

            System.out.println("🎉 Seed du lieu mac dinh thanh cong!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // main() phải nằm bên trong class DatabaseSeeder
    public static void main(String[] args) {
        new DatabaseSeeder().seed();
        System.out.println("✅ Database seeded successfully!");
    }
}