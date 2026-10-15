package com.protrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectManager Class extending User.
 * Demonstrates: Inheritance & Association.
 * Responsibilities: Project Creation, Task Assignment, Progress Monitoring.
 */
public class ProjectManager extends User {

    private List<Project> managedProjects;

    public ProjectManager(int id, String name, String email, String department, String title) {
        super(id, name, email, "PROJECT_MANAGER", department, title);
        this.managedProjects = new ArrayList<>();
    }

    @Override
    public String getDashboardRoute() {
        return "pm-dashboard.html";
    }

    @Override
    public void displayRoleCapabilities() {
        System.out.println("Project Manager: Create Projects, Assign Tasks, Monitor Velocity & Progress.");
    }

    // Functionality 1: Project Creation
    public String createProject(String title, String description, String startDate, String endDate, String budget) {
        Project p = new Project(title, description, startDate, endDate, this.getId(), this.getName(), budget);
        managedProjects.add(p);
        return "Project '" + title + "' created successfully with timeline " + startDate + " to " + endDate;
    }

    // Functionality 2: Task Assignment
    public String assignTask(Task task, TeamMember member) {
        task.setAssigneeId(member.getId());
        task.setAssigneeName(member.getName());
        return "Task '" + task.getTitle() + "' successfully assigned to " + member.getName();
    }

    // Functionality 3: Progress Monitoring
    public double monitorProgress(Project project) {
        return project.calculateCompletionRate();
    }

    public List<Project> getManagedProjects() {
        return managedProjects;
    }
}
