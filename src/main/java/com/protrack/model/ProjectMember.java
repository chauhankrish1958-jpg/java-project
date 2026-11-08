package com.protrack.model;

import java.sql.Timestamp;

/**
 * Project Member junction entity mapping team members to projects.
 */
public class ProjectMember extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private int projectId;
    private int userId;
    private Timestamp assignedAt;

    // Joined User Details for display
    private String userName;
    private String userEmail;
    private Role userRole;

    public ProjectMember() {
        super();
    }

    public ProjectMember(int projectId, int userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Timestamp assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }
}
