package com.protrack.model;

/**
 * Abstract Base Class representing a User in the Project Management System.
 * Demonstrates: Encapsulation & Abstraction.
 */
public abstract class User {
    private int id;
    private String name;
    private String email;
    private String role; // "ADMIN", "PROJECT_MANAGER", "TEAM_MEMBER"
    private String department;
    private String title;
    private String phone;
    private String bio;

    public User(int id, String name, String email, String role, String department, String title) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.title = title;
    }

    // Abstract method demonstrating Polymorphism across User types
    public abstract String getDashboardRoute();
    public abstract void displayRoleCapabilities();

    // Getters and Setters (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    @Override
    public String toString() {
        return String.format("[%s] #%d: %s <%s>", role, id, name, email);
    }
}
