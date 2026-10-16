package com.protrack.model;

/**
 * Enumeration of User Roles in the system.
 */
public enum Role {
    ADMIN("Admin"),
    PROJECT_MANAGER("Project Manager"),
    TEAM_MEMBER("Team Member");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String text) {
        if (text == null) return TEAM_MEMBER;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(text) || r.displayName.equalsIgnoreCase(text)) {
                return r;
            }
        }
        return TEAM_MEMBER;
    }
}
