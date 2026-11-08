package com.protrack.model;

/**
 * Task priority levels with bootstrap badge and severity weights.
 */
public enum TaskPriority {
    LOW("Low", "bg-info text-dark", 1),
    MEDIUM("Medium", "bg-secondary", 2),
    HIGH("High", "bg-warning text-dark", 3),
    CRITICAL("Critical", "bg-danger", 4);

    private final String label;
    private final String badgeClass;
    private final int weight;

    TaskPriority(String label, String badgeClass, int weight) {
        this.label = label;
        this.badgeClass = badgeClass;
        this.weight = weight;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public int getWeight() {
        return weight;
    }

    public static TaskPriority fromString(String priorityStr) {
        if (priorityStr == null) return MEDIUM;
        try {
            return TaskPriority.valueOf(priorityStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
