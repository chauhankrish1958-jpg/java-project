package com.protrack.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TeamMember Class representing software developers, QA, and designers.
 * Demonstrates:
 * - Inheritance (extends User)
 * - Polymorphism (Overrides getDashboardName(), displayRoleCapabilities(), getDashboardRoute())
 *
 * Responsibilities (per GUVI Rubric):
 * 1. Task Tracking (Update status, progress % slider, hours logged, confirmation message)
 * 2. Project Details (View scope, deadlines, manager, deliverables)
 * 3. Profile Management (Update name, email, password, phone, bio, confirmation message)
 * 4. Dashboards (Task List, Project Details, Profile Form, Activity Feed)
 */
public class TeamMember extends User {

    private final List<Integer> assignedTaskIds;

    public TeamMember(int id, String name, String email, String department, String title) {
        super(id, name, email, Role.TEAM_MEMBER, department, title);
        this.assignedTaskIds = new ArrayList<>();
    }

    public TeamMember(int id, String name, String email, String department, String title, String phone, String bio) {
        super(id, name, email, Role.TEAM_MEMBER, department, title, phone, bio);
        this.assignedTaskIds = new ArrayList<>();
    }

    @Override
    public String getDashboardName() {
        return "Team Member Execution & Task Workspace";
    }

    @Override
    public String getDashboardRoute() {
        return "team-dashboard.html";
    }

    @Override
    public void displayRoleCapabilities() {
        System.out.println("=================================================");
        System.out.println("  TEAM MEMBER CAPABILITIES");
        System.out.println("  1. Task Tracking    : Update task status, log hours, set progress %");
        System.out.println("  2. Project Details  : View associated projects, milestones, deadlines");
        System.out.println("  3. Profile Settings : Update personal contact info and credentials");
        System.out.println("  4. Activity Feed    : Follow chronological notifications and updates");
        System.out.println("=================================================");
    }

    public void addAssignedTaskId(int taskId) {
        if (!assignedTaskIds.contains(taskId)) {
            assignedTaskIds.add(taskId);
        }
    }

    public void removeAssignedTaskId(int taskId) {
        assignedTaskIds.remove(Integer.valueOf(taskId));
    }

    public List<Integer> getAssignedTaskIds() {
        return new ArrayList<>(assignedTaskIds);
    }
}
