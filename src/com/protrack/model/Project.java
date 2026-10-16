package com.protrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Entity Class.
 * Demonstrates:
 * - Encapsulation (Private fields with validated getters and setters)
 * - Composition (Manages relationship with Tasks)
 */
public class Project {
    private int id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String status; // "Planning", "In Progress", "Completed"
    private Priority priority;
    private int managerId;
    private String managerName;
    private String budget;
    private int progress;
    private final List<Integer> taskIds;

    public Project(int id, String title, String description, String startDate, String endDate,
                   int managerId, String managerName, String budget, Priority priority, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.managerId = managerId;
        this.managerName = managerName;
        this.budget = budget != null ? budget : "$50,000";
        this.priority = priority != null ? priority : Priority.MEDIUM;
        this.status = status != null ? status : "Planning";
        this.progress = 0;
        this.taskIds = new ArrayList<>();
    }

    public Project(int id, String title, String description, String startDate, String endDate,
                   int managerId, String managerName, String budget) {
        this(id, title, description, startDate, endDate, managerId, managerName, budget, Priority.MEDIUM, "Planning");
    }

    public void recalculateProgress(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        int total = 0;
        int count = 0;
        for (Task t : tasks) {
            if (t.getProjectId() == this.id) {
                total += t.getProgress();
                count++;
            }
        }
        if (count > 0) {
            this.progress = total / count;
            if (this.progress == 100) {
                this.status = "Completed";
            } else if (this.progress > 0 && "Planning".equalsIgnoreCase(this.status)) {
                this.status = "In Progress";
            }
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
    }

    public List<Integer> getTaskIds() {
        return new ArrayList<>(taskIds);
    }

    public void addTaskId(int taskId) {
        if (!taskIds.contains(taskId)) {
            taskIds.add(taskId);
        }
    }

    public void removeTaskId(int taskId) {
        taskIds.remove(Integer.valueOf(taskId));
    }

    @Override
    public String toString() {
        return String.format("Project #%d: '%s' [%s] - PM: %s | Progress: %d%% | Due: %s",
                id, title, status, managerName, progress, endDate);
    }
}
