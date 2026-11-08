package com.protrack.model;

/**
 * Task lifecycle status enum with badge styling metadata.
 */
public enum TaskStatus {
    TODO("To Do", "bg-secondary"),
    IN_PROGRESS("In Progress", "bg-primary"),
    REVIEW("In Review", "bg-warning text-dark"),
    COMPLETED("Completed", "bg-success");

    private final String label;
    private final String badgeClass;

    TaskStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static TaskStatus fromString(String statusStr) {
        if (statusStr == null) return TODO;
        try {
            return TaskStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TODO;
        }
    }
}
