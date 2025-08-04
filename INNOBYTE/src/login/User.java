package login;

public class User {
    private String id; 
    private String username;
    private String password;
    private Role role;

    // Constructor diubah untuk menerima parameter id
    public User(String id, String username, String password, Role role) {
        this.id = id; 
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter untuk mendapatkan data
    
    public String getId() { 
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
