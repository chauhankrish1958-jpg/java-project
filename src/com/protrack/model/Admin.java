package com.protrack.model;

/**
 * Admin Class representing system administrators.
 * Demonstrates:
 * - Inheritance (extends User)
 * - Polymorphism (Overrides getDashboardName(), displayRoleCapabilities(), getDashboardRoute())
 *
 * Responsibilities (per GUVI Rubric):
 * 1. User Management (CRUD operations with confirmation messages)
 * 2. Project Management (Oversee projects with confirmation messages)
 * 3. System Settings (Configure system-wide settings with confirmation messages)
 * 4. Performance Metrics (Reports and graphs of system usage & user activity)
 */
public class Admin extends User {

    public Admin(int id, String name, String email, String department, String title) {
        super(id, name, email, Role.ADMIN, department, title);
    }

    public Admin(int id, String name, String email, String department, String title, String phone, String bio) {
        super(id, name, email, Role.ADMIN, department, title, phone, bio);
    }

    @Override
    public String getDashboardName() {
        return "Administrator Control Center & System Oversight";
    }

    @Override
    public String getDashboardRoute() {
        return "admin-dashboard.html";
    }

    @Override
    public void displayRoleCapabilities() {
        System.out.println("=================================================");
        System.out.println("  ADMINISTRATOR CAPABILITIES");
        System.out.println("  1. User Management   : Create, update, list & delete user accounts");
        System.out.println("  2. Project Oversight : Oversee and manage all enterprise projects");
        System.out.println("  3. System Settings   : Configure system name, maintenance mode, timeouts");
        System.out.println("  4. Performance Stats : View server metrics, role splits, velocity");
        System.out.println("=================================================");
    }
}
