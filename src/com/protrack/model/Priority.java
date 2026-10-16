package com.protrack.model;

/**
 * Enumeration of Task / Project Priorities.
 */
public enum Priority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Priority fromString(String text) {
        if (text == null) return MEDIUM;
        for (Priority p : Priority.values()) {
            if (p.name().equalsIgnoreCase(text) || p.displayName.equalsIgnoreCase(text)) {
                return p;
            }
        }
        return MEDIUM;
    }
}
