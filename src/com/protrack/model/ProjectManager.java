package com.protrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectManager Class representing project leads.
 * Demonstrates:
 * - Inheritance (extends User)
 * - Association (manages List of Project IDs)
 * - Polymorphism (Overrides getDashboardName(), displayRoleCapabilities(), getDashboardRoute())
 *
 * Responsibilities (per GUVI Rubric):
 * 1. Project Creation (Title, description, timeline, confirmation message)
 * 2. Task Assignment (Task details, assignee, confirmation message)
 * 3. Progress Monitoring (Project progress data, reports & visualizations)
 * 4. Dashboards (Project Overview, Task Management, Progress Reports, Team Overview)
 */
public class ProjectManager extends User {

    private final List<Integer> managedProjectIds;

    public ProjectManager(int id, String name, String email, String department, String title) {
        super(id, name, email, Role.PROJECT_MANAGER, department, title);
        this.managedProjectIds = new ArrayList<>();
    }

    public ProjectManager(int id, String name, String email, String department, String title, String phone, String bio) {
        super(id, name, email, Role.PROJECT_MANAGER, department, title, phone, bio);
        this.managedProjectIds = new ArrayList<>();
    }

    @Override
    public String getDashboardName() {
        return "Project Manager Workspace & Delivery Dashboard";
    }

    @Override
    public String getDashboardRoute() {
        return "pm-dashboard.html";
    }

    @Override
    public void displayRoleCapabilities() {
        System.out.println("=================================================");
        System.out.println("  PROJECT MANAGER CAPABILITIES");
        System.out.println("  1. Project Creation  : Initiate and configure new projects with timelines");
        System.out.println("  2. Task Assignment   : Delegate tasks to engineers with due dates & priorities");
        System.out.println("  3. Progress Tracking : Monitor milestone progress and status distributions");
        System.out.println("  4. Team Overview     : Inspect team member workload and efficiency");
        System.out.println("=================================================");
    }

    public void addManagedProject(int projectId) {
        if (!managedProjectIds.contains(projectId)) {
            managedProjectIds.add(projectId);
        }
    }

    public void removeManagedProject(int projectId) {
        managedProjectIds.remove(Integer.valueOf(projectId));
    }

    public List<Integer> getManagedProjectIds() {
        return new ArrayList<>(managedProjectIds);
    }
}
