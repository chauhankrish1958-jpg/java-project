package com.protrack.model;

/**
 * System Administrator model class.
 * Inherits from User and overrides administrative permissions.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin() {
        super();
        this.role = Role.ADMIN;
    }

    public Admin(int id, String fullName, String email, String password, String status) {
        super(id, fullName, email, password, Role.ADMIN, status);
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public boolean canManageProjects() {
        return true;
    }

    @Override
    public boolean canAssignTasks() {
        return true;
    }
}
