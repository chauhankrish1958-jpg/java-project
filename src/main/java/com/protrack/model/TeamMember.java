package com.protrack.model;

/**
 * Team Member model class.
 * Inherits from User, restricted to assigned tasks and personal progress.
 */
public class TeamMember extends User {
    private static final long serialVersionUID = 1L;

    public TeamMember() {
        super();
        this.role = Role.TEAM_MEMBER;
    }

    public TeamMember(int id, String fullName, String email, String password, String status) {
        super(id, fullName, email, password, Role.TEAM_MEMBER, status);
    }
}
