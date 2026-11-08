package com.protrack.model;

import java.sql.Date;

/**
 * Project Entity representing managed software initiatives.
 */
public class Project extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private int managerId;
    private String status = "PLANNING"; // PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED

    // Joined / Aggregate helper fields
    private String managerName;
    private String managerEmail;
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;

    public Project() {
        super();
    }

    public Project(int id, String title, String description, Date startDate, Date endDate, int managerId, String status) {
        super(id);
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.managerId = managerId;
        this.status = status != null ? status : "PLANNING";
    }

    public int getProgressPercentage() {
        if (totalTasks == 0) return 0;
        return (int) Math.round(((double) completedTasks / totalTasks) * 100);
    }

    public String getStatusBadgeClass() {
        if (status == null) return "bg-secondary";
        return switch (status.toUpperCase()) {
            case "COMPLETED" -> "bg-success";
            case "IN_PROGRESS" -> "bg-primary";
            case "PLANNING" -> "bg-info text-dark";
            case "ON_HOLD" -> "bg-warning text-dark";
            case "CANCELLED" -> "bg-danger";
            default -> "bg-secondary";
        };
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(int pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
}
