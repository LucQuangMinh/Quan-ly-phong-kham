// File: models/User.java (THAY THẾ HOÀN TOÀN)
package models;

public class User extends Person { 
    private String password;
    private String role;

    public User() { super(); }

    // Constructor 5 tham số: ID, name, email, password, role
    public User(int id, String name, String email, String password, String role) {
        super(id, name, email); // Gọi constructor 3 tham số của Person
        this.password = password;
        this.role = role;
    }

    // Constructor 4 tham số: name, email, password, role
    public User(String name, String email, String password, String role) {
        super(0, name, email); // Gọi constructor 3 tham số của Person (ID=0)
        this.password = password;
        this.role = role;
    }

    // ✅ Thêm getUsername() để sửa lỗi trong Dashboard
    public String getUsername() { return this.name; }
    
    // ✅ Ghi đè phương thức từ Person
    @Override
    public String getRoleDescription() { return "User Role: " + this.role; }
    
    // 🧱 Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    @Override
    public String toString() { return name + " (" + role + ")"; }
}