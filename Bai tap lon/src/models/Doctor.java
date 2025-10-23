package models;

public class Doctor extends User {
    private int specialtyId;
    private int userId;
    private String specialtyName;

    public Doctor() {
        super();
    }

    // constructor chính (Gọi constructor 5 tham số của User)
    public Doctor(int id, String name, String email, int specialtyId, int userId) {
        super(id, name, email, null, "doctor"); // User(ID, name, email, password=null, role)
        this.specialtyId = specialtyId;
        this.userId = userId;
    }

    // constructor mở rộng có tên chuyên khoa (Gọi constructor 5 tham số của User)
    public Doctor(int id, String name, String email, int specialtyId, int userId, String specialtyName) {
        super(id, name, email, null, "doctor"); // User(ID, name, email, password=null, role)
        this.specialtyId = specialtyId;
        this.userId = userId;
        this.specialtyName = specialtyName;
    }

    // constructor cho DatabaseSeeder (Gọi constructor 4 tham số của User)
    public Doctor(String name, String email, String specialtyName) { 
        super(name, email, null, "doctor"); // User(name, email, password=null, role)
        this.specialtyName = specialtyName;
    }

    public int getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(int specialtyId) { this.specialtyId = specialtyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSpecialtyName() { return specialtyName; }
    public void setSpecialtyName(String specialtyName) { this.specialtyName = specialtyName; }

    @Override
    public String getRoleDescription() {
        return "Doctor - Specialty ID: " + specialtyId +
               (specialtyName != null ? " (" + specialtyName + ")" : "");
    }
}