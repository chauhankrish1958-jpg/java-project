package com.protrack.service;

import com.protrack.model.Priority;
import com.protrack.model.Task;
import com.protrack.model.TaskStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing Task deliverables, assignments, and tracking.
 * Demonstrates:
 * - Business Logic Layer
 * - Method Overloading (searchTasks)
 */
public class TaskService {
    private final Map<Integer, Task> taskMap;
    private int idCounter;

    public TaskService() {
        this.taskMap = new LinkedHashMap<>();
        this.idCounter = 101;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public Optional<Task> getTaskById(int id) {
        return Optional.ofNullable(taskMap.get(id));
    }

    public List<Task> getTasksByProject(int projectId) {
        return taskMap.values().stream()
                .filter(t -> t.getProjectId() == projectId)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByAssignee(int assigneeId) {
        return taskMap.values().stream()
                .filter(t -> t.getAssigneeId() == assigneeId)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskMap.values().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    // Functionality 2 for PM: Task Assignment
    public Task assignTask(int projectId, String projectTitle, String title, String description,
                            int assigneeId, String assigneeName, String dueDate, Priority priority, double estimatedHours) {
        int newId = idCounter++;
        Task task = new Task(newId, projectId, projectTitle, title, description, assigneeId, assigneeName,
                dueDate, priority, TaskStatus.PENDING, 0, 0, estimatedHours);
        taskMap.put(newId, task);
        return task;
    }

    // Functionality 1 for Team Member: Task Tracking & Status Update
    public boolean updateTaskStatus(int taskId, TaskStatus newStatus, int progressPercentage, double additionalHours) {
        Task task = taskMap.get(taskId);
        if (task == null) return false;

        task.updateStatus(newStatus, progressPercentage);
        if (additionalHours > 0) {
            task.addLoggedHours(additionalHours);
        }
        return true;
    }

    public boolean updateTaskDetails(int taskId, String title, String description, int projectId, String projectTitle,
                                     int assigneeId, String assigneeName, String dueDate, Priority priority,
                                     TaskStatus status, int progress, double loggedHours, double estimatedHours) {
        Task task = taskMap.get(taskId);
        if (task == null) return false;

        task.setTitle(title);
        task.setDescription(description);
        task.setProjectId(projectId);
        task.setProjectTitle(projectTitle);
        task.setAssigneeId(assigneeId);
        task.setAssigneeName(assigneeName);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setStatus(status);
        task.setProgress(progress);
        task.setLoggedHours(loggedHours);
        task.setEstimatedHours(estimatedHours);
        return true;
    }

    public boolean deleteTask(int taskId) {
        return taskMap.remove(taskId) != null;
    }

    public double getCompletionRate() {
        if (taskMap.isEmpty()) return 0.0;
        long completed = taskMap.values().stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        return ((double) completed / taskMap.size()) * 100.0;
    }

    // --- Compile-Time Polymorphism (Method Overloading) ---
    public List<Task> searchTasks(String query) {
        if (query == null || query.trim().isEmpty()) return getAllTasks();
        String q = query.toLowerCase().trim();
        return taskMap.values().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(q) ||
                             t.getDescription().toLowerCase().contains(q) ||
                             t.getAssigneeName().toLowerCase().contains(q) ||
                             t.getProjectTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Task> searchTasks(TaskStatus status) {
        return getTasksByStatus(status);
    }

    public List<Task> searchTasks(String query, TaskStatus status) {
        return searchTasks(query).stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }
}
