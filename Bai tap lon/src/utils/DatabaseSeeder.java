package utils;

import dao.SpecialtyDAO;
import dao.DoctorDAO;
import dao.UserDAO;
import models.Specialty;
import models.Doctor;
import models.User;

import java.util.Arrays;
import java.util.List;

public class DatabaseSeeder {

    public static void seed() {
        try {
            SpecialtyDAO specialtyDAO = new SpecialtyDAO();
            DoctorDAO doctorDAO = new DoctorDAO();
            UserDAO userDAO = new UserDAO();

            // 1. Seed chuyen khoa
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

            // 2. Seed admin mac dinh
            if (userDAO.findByEmail("admin@clinic.com") == null) {
                userDAO.insert(new User("Admin", "admin@clinic.com", PasswordUtils.hashPassword("123456"), "admin", "0000000000", null));
                System.out.println("✅ Da tao admin mac dinh (email: admin@clinic.com, pass: 123456)");
            }

            // 3. Seed bac si mau (lay theo chuyen khoa)
            List<Doctor> doctors = Arrays.asList(
                new Doctor("TS.BS. Chu Trong Hiep", "hiep@clinic.com", "Khoa tim mach", "0901234567", "avatars/avatar-TS.BS_.Chu-Trong-Hiep.webp"),
                new Doctor("ThS.BS. Pham Minh Cuong", "cuong@clinic.com", "Khoa tim mach", "0902345678", "avatars/BS-PHAM-MINH-CUONG.png"),
                new Doctor("BS. Tu Thi Thu Ha", "ha@clinic.com", "Khoa tim mach", "0902230042", "avatars/Tu-Thi-Thu-Ha-2024.jpg"),
                new Doctor("BS. Nguyen Thi Tuyet Mai", "mai@clinic.com", "Khoa than kinh", "0223423434", "avatars/BS-NGUYEN-THI-TUYET-MAI.jpg"),
                new Doctor("BS. Phan Phuoc Hoang Minh", "minh@clinic.com", "Khoa tai mui hong", "0698933772", "avatars/PHAN-PHUOC-HOANG-MINH.png")
            );

            for (Doctor doc : doctors) {
                if (userDAO.findByEmail(doc.getEmail()) == null) {
                    Specialty sp = specialtyDAO.findByName(doc.getSpecialtyName());
                    if (sp != null) {
                        // tao user truoc
                        User user = new User(doc.getName(), doc.getEmail(), PasswordUtils.hashPassword("123456"), "doctor", doc.getPhone(), doc.getAvatar());
                        userDAO.insert(user);

                        // tao bac si gan specialty
                        doc.setSpecialtyId(sp.getId());
                        doc.setUserId(user.getId());
                        doctorDAO.insert(doc);
                        System.out.println("✅ Da tao bac si: " + doc.getName());
                    }
                }
            }

            System.out.println("🎉 Seed du lieu mac dinh thanh cong!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
