package com.protrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Entity Class.
 * Demonstrates: Composition & Encapsulation.
 */
public class Project {
    private static int idCounter = 1;

    private int id;
    private String title;
    private String description;
    private String startDate;
    private String endDate;
    private String status; // "Planning", "In Progress", "Completed"
    private String priority; // "Low", "Medium", "High", "Urgent"
    private int managerId;
    private String managerName;
    private String budget;
    private int progress;
    private List<Task> tasks;

    public Project(String title, String description, String startDate, String endDate, int managerId, String managerName, String budget) {
        this.id = idCounter++;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "Planning";
        this.priority = "Medium";
        this.managerId = managerId;
        this.managerName = managerName;
        this.budget = budget;
        this.progress = 0;
        this.tasks = new ArrayList<>();
    }

    public double calculateCompletionRate() {
        if (tasks.isEmpty()) return this.progress;
        int total = 0;
        for (Task t : tasks) {
            total += t.getProgress();
        }
        this.progress = total / tasks.size();
        return this.progress;
    }

    // Getters and Setters
    public int getId() { return id; }
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

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public List<Task> getTasks() { return tasks; }
    public void addTask(Task task) { this.tasks.add(task); }
}
