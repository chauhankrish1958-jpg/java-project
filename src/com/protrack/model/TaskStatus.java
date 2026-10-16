package com.protrack.model;

/**
 * Enumeration of Task Progress Statuses.
 */
public enum TaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskStatus fromString(String text) {
        if (text == null) return PENDING;
        for (TaskStatus s : TaskStatus.values()) {
            if (s.name().equalsIgnoreCase(text) || s.displayName.equalsIgnoreCase(text) || s.name().replace("_", " ").equalsIgnoreCase(text)) {
                return s;
            }
        }
        return PENDING;
    }
}
