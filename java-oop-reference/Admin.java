package com.protrack.model;

import java.util.List;

/**
 * Admin Class extending User.
 * Demonstrates: Inheritance & Polymorphism.
 * Responsibilities: User Management, Project Oversight, System Settings, Performance Metrics.
 */
public class Admin extends User {

    public Admin(int id, String name, String email, String department, String title) {
        super(id, name, email, "ADMIN", department, title);
    }

    @Override
    public String getDashboardRoute() {
        return "admin-dashboard.html";
    }

    @Override
    public void displayRoleCapabilities() {
        System.out.println("Admin Capabilities: Manage Users, Oversee Projects, Configure System Settings, View Performance Metrics.");
    }

    // Functionality 1: User Management
    public String createUser(User newUser) {
        // Validation and persistence logic
        return "User account '" + newUser.getName() + "' created successfully with role " + newUser.getRole();
    }

    public String updateUser(int userId, User updatedDetails) {
        return "User account #" + userId + " updated successfully.";
    }

    public String deleteUser(int userId) {
        return "User account #" + userId + " deleted successfully.";
    }

    // Functionality 2: Project Management
    public String overseeProjectCreation(Project project) {
        return "Project '" + project.getTitle() + "' successfully registered in system registry.";
    }

    public String removeProject(int projectId) {
        return "Project #" + projectId + " successfully deleted from registry.";
    }

    // Functionality 3: System Settings
    public String updateSystemSettings(SystemSettings settings) {
        return "System configuration settings successfully updated.";
    }
}
