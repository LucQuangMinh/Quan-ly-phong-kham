package models;

public class Person {
    protected int id;
    protected String name;
    protected String email;
    protected String phone;
    protected String avatar;

    public Person() {}

    // ✅ Thêm constructor 3 tham số để User có thể gọi super(id, name, email)
    public Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = null; // Mặc định là null khi gọi constructor này
        this.avatar = null; // Mặc định là null khi gọi constructor này
    }

    public Person(int id, String name, String email, String phone, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    // Polymorphism: subclasses can override
    public String getRoleDescription() {
        return "This is a generic person in the system.";
    }
}