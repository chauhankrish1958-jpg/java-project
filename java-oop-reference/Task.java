package com.protrack.model;

/**
 * Task Entity Class.
 * Demonstrates: Encapsulation & State management.
 */
public class Task {
    private static int idCounter = 101;

    private int id;
    private int projectId;
    private String projectTitle;
    private String title;
    private String description;
    private int assigneeId;
    private String assigneeName;
    private String status; // "Pending", "In Progress", "Completed"
    private String priority; // "Low", "Medium", "High", "Urgent"
    private String dueDate;
    private int progress; // 0 - 100
    private double loggedHours;
    private double estimatedHours;

    public Task(int projectId, String projectTitle, String title, String description, int assigneeId, String assigneeName, String dueDate, String priority) {
        this.id = idCounter++;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = "Pending";
        this.progress = 0;
        this.loggedHours = 0;
        this.estimatedHours = 20;
    }

    public void addLoggedHours(double hours) {
        this.loggedHours += hours;
    }

    // Getters and Setters
    public int getId() { return id; }
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public double getLoggedHours() { return loggedHours; }
    public void setLoggedHours(double loggedHours) { this.loggedHours = loggedHours; }

    public double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }
}
