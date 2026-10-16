package com.protrack.model;

/**
 * Task Entity Class representing assigned deliverables.
 * Demonstrates:
 * - Encapsulation (Private fields with validated getters and setters)
 */
public class Task {
    private int id;
    private int projectId;
    private String projectTitle;
    private String title;
    private String description;
    private int assigneeId;
    private String assigneeName;
    private TaskStatus status;
    private Priority priority;
    private String dueDate;
    private int progress; // 0 - 100
    private double loggedHours;
    private double estimatedHours;

    public Task(int id, int projectId, String projectTitle, String title, String description,
                int assigneeId, String assigneeName, String dueDate, Priority priority,
                TaskStatus status, int progress, double loggedHours, double estimatedHours) {
        this.id = id;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = status != null ? status : TaskStatus.PENDING;
        this.progress = Math.max(0, Math.min(100, progress));
        this.loggedHours = Math.max(0, loggedHours);
        this.estimatedHours = Math.max(1, estimatedHours);
    }

    public Task(int id, int projectId, String projectTitle, String title, String description,
                int assigneeId, String assigneeName, String dueDate, Priority priority) {
        this(id, projectId, projectTitle, title, description, assigneeId, assigneeName, dueDate, priority,
                TaskStatus.PENDING, 0, 0, 20.0);
    }

    public void addLoggedHours(double hours) {
        if (hours > 0) {
            this.loggedHours += hours;
        }
    }

    public void updateStatus(TaskStatus newStatus, int progressPercentage) {
        if (newStatus != null) {
            this.status = newStatus;
        }
        this.progress = Math.max(0, Math.min(100, progressPercentage));
        if (this.status == TaskStatus.COMPLETED && this.progress < 100) {
            this.progress = 100;
        } else if (this.progress == 100 && this.status != TaskStatus.COMPLETED) {
            this.status = TaskStatus.COMPLETED;
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAssigneeId() { return assigneeId; }
    public void setAssigneeId(int assigneeId) { this.assigneeId = assigneeId; }

    public String getAssigneeName() { return assigneeName; }
    public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
    }

    public double getLoggedHours() { return loggedHours; }
    public void setLoggedHours(double loggedHours) { this.loggedHours = loggedHours; }

    public double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }

    @Override
    public String toString() {
        return String.format("Task #%d: '%s' [%s | %s] - Assigned: %s | %d%% | Due: %s",
                id, title, status.getDisplayName(), priority.getDisplayName(), assigneeName, progress, dueDate);
    }
}
