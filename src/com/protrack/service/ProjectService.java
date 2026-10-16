package com.protrack.service;

import com.protrack.model.Priority;
import com.protrack.model.Project;
import com.protrack.model.Task;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing Enterprise Projects and Timelines.
 * Demonstrates:
 * - Business Logic Layer
 * - Method Overloading (searchProjects)
 */
public class ProjectService {
    private final Map<Integer, Project> projectMap;
    private int idCounter;

    public ProjectService() {
        this.projectMap = new LinkedHashMap<>();
        this.idCounter = 1;
    }

    public List<Project> getAllProjects() {
        return new ArrayList<>(projectMap.values());
    }

    public Optional<Project> getProjectById(int id) {
        return Optional.ofNullable(projectMap.get(id));
    }

    public List<Project> getProjectsByManager(int managerId) {
        return projectMap.values().stream()
                .filter(p -> p.getManagerId() == managerId)
                .collect(Collectors.toList());
    }

    public List<Project> getProjectsByStatus(String status) {
        return projectMap.values().stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(status.trim()))
                .collect(Collectors.toList());
    }

    // Functionality 2 for Admin & Functionality 1 for PM: Project Creation
    public Project createProject(String title, String description, String startDate, String endDate,
                                  int managerId, String managerName, String budget, Priority priority) {
        int newId = idCounter++;
        Project project = new Project(newId, title, description, startDate, endDate, managerId, managerName, budget, priority, "Planning");
        projectMap.put(newId, project);
        return project;
    }

    // Functionality 2 for Admin: Project Update
    public boolean updateProject(int id, String title, String description, String startDate, String endDate,
                                 int managerId, String managerName, String budget, Priority priority, String status) {
        Project project = projectMap.get(id);
        if (project == null) return false;

        project.setTitle(title);
        project.setDescription(description);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setManagerId(managerId);
        project.setManagerName(managerName);
        project.setBudget(budget);
        project.setPriority(priority);
        project.setStatus(status);
        return true;
    }

    // Functionality 2 for Admin: Project Deletion
    public boolean deleteProject(int id) {
        return projectMap.remove(id) != null;
    }

    public void updateProgressFromTasks(int projectId, List<Task> tasks) {
        Project project = projectMap.get(projectId);
        if (project != null) {
            project.recalculateProgress(tasks);
        }
    }

    public double getAverageProgress() {
        if (projectMap.isEmpty()) return 0.0;
        int sum = projectMap.values().stream().mapToInt(Project::getProgress).sum();
        return (double) sum / projectMap.size();
    }

    // --- Compile-Time Polymorphism (Method Overloading) ---
    public List<Project> searchProjects(String query) {
        if (query == null || query.trim().isEmpty()) return getAllProjects();
        String q = query.toLowerCase().trim();
        return projectMap.values().stream()
                .filter(p -> p.getTitle().toLowerCase().contains(q) ||
                             p.getDescription().toLowerCase().contains(q) ||
                             p.getManagerName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Project> searchProjects(Priority priority) {
        return projectMap.values().stream()
                .filter(p -> p.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Project> searchProjects(String query, Priority priority) {
        return searchProjects(query).stream()
                .filter(p -> p.getPriority() == priority)
                .collect(Collectors.toList());
    }
}
