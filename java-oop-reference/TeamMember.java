package com.protrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TeamMember Class extending User.
 * Demonstrates: Inheritance & Encapsulation.
 * Responsibilities: Task Tracking, View Project Details, Profile Management.
 */
public class TeamMember extends User {

    private List<Task> assignedTasks;

    public TeamMember(int id, String name, String email, String department, String title) {
        super(id, name, email, "TEAM_MEMBER", department, title);
        this.assignedTasks = new ArrayList<>();
    }

    @Override
    public String getDashboardRoute() {
        return "team-dashboard.html";
    }

    @Override
    public void displayRoleCapabilities() {
        System.out.println("Team Member: Track assigned tasks, inspect project deadlines, maintain user profile.");
    }

    // Functionality 1: Task Tracking
    public String updateTaskStatus(Task task, String newStatus, int progressPercentage, double hours) {
        task.setStatus(newStatus);
        task.setProgress(progressPercentage);
        task.addLoggedHours(hours);
        return "Task '" + task.getTitle() + "' status updated to " + newStatus + " (" + progressPercentage + "% complete).";
    }

    // Functionality 2: Project Details Access
    public String viewProjectDetails(Project project) {
        return String.format("Project: %s | Deadline: %s | Progress: %d%%", 
            project.getTitle(), project.getEndDate(), project.getProgress());
    }

    // Functionality 3: Profile Management
    public String updateProfile(String name, String email, String phone, String bio) {
        this.setName(name);
        this.setEmail(email);
        this.setPhone(phone);
        this.setBio(bio);
        return "Personal profile updated successfully for " + name;
    }

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void addTask(Task t) {
        this.assignedTasks.add(t);
    }
}
