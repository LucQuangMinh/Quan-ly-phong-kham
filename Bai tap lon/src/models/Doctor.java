package models;

public class Doctor extends User {
    private int specialtyId;
    private int userId;

    public Doctor() {}

    public Doctor(int id, String name, String email, int specialtyId, int userId) {
        super(id, name, email, null, "doctor", null, null);
        this.specialtyId = specialtyId;
        this.userId = userId;
    }

    public int getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(int specialtyId) { this.specialtyId = specialtyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String getRoleDescription() {
        return "Doctor with specialty id: " + specialtyId;
    }
}
