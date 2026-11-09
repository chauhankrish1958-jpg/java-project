package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.TaskDAO;
import com.protrack.dao.impl.ActivityDAOImpl;
import com.protrack.dao.impl.TaskDAOImpl;
import com.protrack.exception.AppException;
import com.protrack.exception.TaskAssignmentException;
import com.protrack.model.Task;
import com.protrack.model.TaskPriority;
import com.protrack.model.TaskStatus;
import com.protrack.util.ValidationUtil;

import java.sql.Date;
import java.util.List;

/**
 * Service managing task creation, assignment, updates and deadline tracking.
 */
public class TaskService {

    private final TaskDAO taskDAO;
    private final ActivityDAO activityDAO;

    public TaskService() {
        this.taskDAO = new TaskDAOImpl();
        this.activityDAO = new ActivityDAOImpl();
    }

    public TaskService(TaskDAO taskDAO, ActivityDAO activityDAO) {
        this.taskDAO = taskDAO;
        this.activityDAO = activityDAO;
    }

    public List<Task> getAllTasks() {
        return taskDAO.findAll();
    }

    public Task getTaskById(int id) throws AppException {
        return taskDAO.findById(id)
                .orElseThrow(() -> new AppException("Task not found with ID: " + id));
    }

    public List<Task> getTasksByProject(int projectId) {
        return taskDAO.findByProjectId(projectId);
    }

    public List<Task> getTasksByMember(int userId) {
        return taskDAO.findByAssignedTo(userId);
    }

    public List<Task> getTasksByMemberAndFilters(int userId, String status, String priority) {
        return taskDAO.findByAssignedToAndFilters(userId, status, priority);
    }

    public List<Task> searchTasks(String query, Integer projectId, Integer assignedTo, TaskStatus status, TaskPriority priority) {
        return taskDAO.searchTasks(query, projectId, assignedTo, status, priority);
    }

    public Task createTask(String title, String description, int projectId, Integer assignedTo,
                           TaskPriority priority, TaskStatus status, Date startDate, Date dueDate, Integer creatorUserId) throws AppException {
        String cleanTitle = ValidationUtil.sanitize(title);
        String cleanDesc = ValidationUtil.sanitize(description);

        if (!ValidationUtil.isNotEmpty(cleanTitle)) {
            throw new AppException("Task title is required.");
        }
        if (projectId <= 0) {
            throw new AppException("A valid project must be selected.");
        }
        if (dueDate == null) {
            throw new AppException("Task due date is required.");
        }
        if (startDate != null && dueDate.before(startDate)) {
            throw new AppException("Due date cannot be before start date.");
        }

        Task task = new Task(0, cleanTitle, cleanDesc, projectId, assignedTo,
                priority != null ? priority : TaskPriority.MEDIUM,
                status != null ? status : TaskStatus.TODO,
                startDate, dueDate, creatorUserId);

        Task saved = taskDAO.save(task);

        activityDAO.log(creatorUserId, "TASK_CREATE", "Created task: \"" + saved.getTaskTitle() + "\"", "TASK", saved.getId());
        return saved;
    }

    public void updateTask(int taskId, String title, String description, int projectId, Integer assignedTo,
                           TaskPriority priority, TaskStatus status, Date startDate, Date dueDate, Integer actorUserId) throws AppException {
        Task existing = getTaskById(taskId);

        String cleanTitle = ValidationUtil.sanitize(title);
        String cleanDesc = ValidationUtil.sanitize(description);

        if (!ValidationUtil.isNotEmpty(cleanTitle)) {
            throw new AppException("Task title is required.");
        }
        if (dueDate == null) {
            throw new AppException("Task due date is required.");
        }
        if (startDate != null && dueDate.before(startDate)) {
            throw new AppException("Due date cannot be before start date.");
        }

        existing.setTaskTitle(cleanTitle);
        existing.setDescription(cleanDesc);
        existing.setProjectId(projectId);
        existing.setAssignedTo(assignedTo);
        if (priority != null) existing.setPriority(priority);
        if (status != null) existing.setStatus(status);
        existing.setStartDate(startDate);
        existing.setDueDate(dueDate);

        boolean updated = taskDAO.update(existing);
        if (!updated) {
            throw new AppException("Failed to update task.");
        }

        activityDAO.log(actorUserId, "TASK_UPDATE", "Updated task details for: \"" + existing.getTaskTitle() + "\"", "TASK", taskId);
    }

    public void updateTaskStatusWithRoleCheck(int taskId, TaskStatus newStatus, com.protrack.model.User actor) throws AppException {
        if (actor == null) {
            throw new com.protrack.exception.UnauthorizedAccessException("Authentication required.");
        }
        Task existing = getTaskById(taskId);
        if (actor.getRole() == com.protrack.model.Role.TEAM_MEMBER) {
            if (existing.getAssignedTo() == null || !existing.getAssignedTo().equals(actor.getId())) {
                throw new com.protrack.exception.UnauthorizedAccessException("Access Denied: You can only update the status of tasks assigned directly to you.");
            }
        }
        updateTaskStatus(taskId, newStatus, actor.getId());
    }

    public void updateTaskStatus(int taskId, TaskStatus newStatus, Integer actorUserId) throws AppException {
        Task existing = getTaskById(taskId);

        if (newStatus == null) {
            throw new AppException("Invalid task status.");
        }

        boolean updated = taskDAO.updateStatus(taskId, newStatus);
        if (!updated) {
            throw new AppException("Failed to update task status.");
        }

        activityDAO.log(actorUserId, "TASK_STATUS_UPDATE",
                "Changed status of task \"" + existing.getTaskTitle() + "\" to " + newStatus.getLabel(), "TASK", taskId);
    }

    public void deleteTask(int taskId, Integer actorUserId) throws AppException {
        Task existing = getTaskById(taskId);

        boolean deleted = taskDAO.deleteById(taskId);
        if (!deleted) {
            throw new AppException("Failed to delete task.");
        }

        activityDAO.log(actorUserId, "TASK_DELETE", "Deleted task: \"" + existing.getTaskTitle() + "\"", "TASK", taskId);
    }

    public List<Task> getOverdueTasks(Integer userId) {
        return taskDAO.findOverdueTasks(userId);
    }

    public List<Task> getTasksDueToday(Integer userId) {
        return taskDAO.findTasksDueToday(userId);
    }
}
