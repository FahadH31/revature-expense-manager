package model;

public class User {
    private final int id;
    private final String username;
    private final String role;
    private final String fullName;

    public User(int id, String username, String role, String fullName) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
}
