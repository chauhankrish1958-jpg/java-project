package com.protrack.model;

/**
 * Enumeration of system roles.
 */
public enum Role {
    ADMIN("Administrator", "/admin/dashboard"),
    PROJECT_MANAGER("Project Manager", "/manager/dashboard"),
    TEAM_MEMBER("Team Member", "/member/dashboard");

    private final String displayName;
    private final String dashboardUrl;

    Role(String displayName, String dashboardUrl) {
        this.displayName = displayName;
        this.dashboardUrl = dashboardUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null) return TEAM_MEMBER;
        try {
            return Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TEAM_MEMBER;
        }
    }
}
