package com.protrack.model;

import java.sql.Timestamp;

/**
 * Base User class representing registered accounts.
 * Demonstrates Inheritance (extends BaseEntity), Encapsulation (private fields),
 * and Polymorphism (overridable role capability methods).
 */
public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    protected String fullName;
    protected String email;
    protected String password;
    protected Role role;
    protected String status = "ACTIVE"; // ACTIVE or INACTIVE

    public User() {
        super();
        this.role = Role.TEAM_MEMBER;
    }

    public User(int id, String fullName, String email, String password, Role role, String status) {
        super(id);
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : Role.TEAM_MEMBER;
        this.status = status != null ? status : "ACTIVE";
    }

    // Role-based capability checks (Demonstrates Polymorphism in subclasses)
    public String getDashboardUrl() {
        return role != null ? role.getDashboardUrl() : "/member/dashboard";
    }

    public boolean canManageUsers() {
        return false;
    }

    public boolean canManageProjects() {
        return false;
    }

    public boolean canAssignTasks() {
        return false;
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status='" + status + '\'' +
                '}';
    }
}
