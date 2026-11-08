package com.protrack.model;

import com.protrack.util.DateUtil;
import java.sql.Date;

/**
 * Task Entity representing work items assigned to team members.
 */
public class Task extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String taskTitle;
    private String description;
    private int projectId;
    private Integer assignedTo; // Nullable if unassigned
    private TaskPriority priority = TaskPriority.MEDIUM;
    private TaskStatus status = TaskStatus.TODO;
    private Date startDate;
    private Date dueDate;
    private Integer createdBy;

    // Display fields joined from other tables
    private String projectName;
    private String assignedToName;
    private String assignedToEmail;
    private String createdByName;

    public Task() {
        super();
    }

    public Task(int id, String taskTitle, String description, int projectId, Integer assignedTo,
                TaskPriority priority, TaskStatus status, Date startDate, Date dueDate, Integer createdBy) {
        super(id);
        this.taskTitle = taskTitle;
        this.description = description;
        this.projectId = projectId;
        this.assignedTo = assignedTo;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.status = status != null ? status : TaskStatus.TODO;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.createdBy = createdBy;
    }

    // Deadline and Alert Helper Methods
    public boolean isOverdue() {
        return DateUtil.isOverdue(this.dueDate, this.status != null ? this.status.name() : "TODO");
    }

    public boolean isDueToday() {
        return DateUtil.isDueToday(this.dueDate, this.status != null ? this.status.name() : "TODO");
    }

    public String getRelativeDueStatus() {
        return DateUtil.getRelativeDueStatus(this.dueDate, this.status != null ? this.status.name() : "TODO");
    }

    // Getters and Setters
    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public String getAssignedToEmail() {
        return assignedToEmail;
    }

    public void setAssignedToEmail(String assignedToEmail) {
        this.assignedToEmail = assignedToEmail;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}
