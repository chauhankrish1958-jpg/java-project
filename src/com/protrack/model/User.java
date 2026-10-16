package com.protrack.model;

/**
 * Abstract Base Class for all Users in the Online Project Management Tool.
 * Demonstrates:
 * - Abstraction (Abstract class & abstract methods)
 * - Encapsulation (Private fields with validated getters and setters)
 */
public abstract class User {
    private int id;
    private String name;
    private String email;
    private Role role;
    private String department;
    private String title;
    private String phone;
    private String bio;
    private String password;

    public User(int id, String name, String email, Role role, String department, String title) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department != null ? department : "General";
        this.title = title != null ? title : role.getDisplayName();
        this.phone = "+1 (555) 000-0000";
        this.bio = "Member of ProTrack team.";
        this.password = "password123";
    }

    public User(int id, String name, String email, Role role, String department, String title, String phone, String bio) {
        this(id, name, email, role, department, title);
        this.phone = phone != null ? phone : this.phone;
        this.bio = bio != null ? bio : this.bio;
    }

    // Abstract methods demonstrating Polymorphism
    public abstract String getDashboardName();
    public abstract void displayRoleCapabilities();
    public abstract String getDashboardRoute();

    // Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email.trim();
        }
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role != null) {
            this.role = role;
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    public String getInitials() {
        if (name == null || name.isEmpty()) return "U";
        String[] parts = name.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) sb.append(p.charAt(0));
            if (sb.length() == 2) break;
        }
        return sb.toString().toUpperCase();
    }

    @Override
    public String toString() {
        return String.format("[%s] #%d %s (%s) - Dept: %s", role.getDisplayName(), id, name, email, department);
    }
}
