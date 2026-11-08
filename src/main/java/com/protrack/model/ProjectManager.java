package com.protrack.model;

/**
 * Project Manager model class.
 * Inherits from User and overrides project and task management permissions.
 */
public class ProjectManager extends User {
    private static final long serialVersionUID = 1L;

    public ProjectManager() {
        super();
        this.role = Role.PROJECT_MANAGER;
    }

    public ProjectManager(int id, String fullName, String email, String password, String status) {
        super(id, fullName, email, password, Role.PROJECT_MANAGER, status);
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
