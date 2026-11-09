package com.protrack.servlet;

import com.protrack.exception.AppException;
import com.protrack.model.Project;
import com.protrack.model.Role;
import com.protrack.model.Task;
import com.protrack.model.TaskPriority;
import com.protrack.model.TaskStatus;
import com.protrack.model.User;
import com.protrack.service.ProjectService;
import com.protrack.service.TaskService;
import com.protrack.service.UserService;
import com.protrack.util.DateUtil;
import com.protrack.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

/**
 * Servlet handling task CRUD, assignment, status progression, and filtering.
 */
@WebServlet(name = "TaskServlet", urlPatterns = {
        "/manager/tasks",
        "/member/tasks"
})
public class TaskServlet extends HttpServlet {

    private final TaskService taskService = new TaskService();
    private final ProjectService projectService = new ProjectService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String uri = request.getRequestURI();

        if (uri.contains("/manager/tasks")) {
            String query = request.getParameter("q");
            int projectId = ValidationUtil.parsePositiveInt(request.getParameter("projectId"), 0);
            int assignedTo = ValidationUtil.parsePositiveInt(request.getParameter("assignedTo"), 0);
            String statusParam = request.getParameter("status");
            String priorityParam = request.getParameter("priority");

            TaskStatus status = (statusParam != null && !statusParam.isEmpty() && !"ALL".equalsIgnoreCase(statusParam))
                    ? TaskStatus.fromString(statusParam) : null;
            TaskPriority priority = (priorityParam != null && !priorityParam.isEmpty() && !"ALL".equalsIgnoreCase(priorityParam))
                    ? TaskPriority.fromString(priorityParam) : null;

            List<Task> tasks = taskService.searchTasks(query, projectId > 0 ? projectId : null,
                    assignedTo > 0 ? assignedTo : null, status, priority);

            List<Project> managedProjects = (currentUser.getRole() == Role.ADMIN)
                    ? projectService.getAllProjects()
                    : projectService.getProjectsByManager(currentUser.getId());

            List<User> teamMembers = userService.getUsersByRole(Role.TEAM_MEMBER);

            request.setAttribute("tasks", tasks);
            request.setAttribute("projects", managedProjects);
            request.setAttribute("teamMembers", teamMembers);
            request.setAttribute("searchQuery", query != null ? query.trim() : "");
            request.setAttribute("selectedProject", projectId);
            request.setAttribute("selectedAssignee", assignedTo);
            request.setAttribute("selectedStatus", statusParam != null ? statusParam : "ALL");
            request.setAttribute("selectedPriority", priorityParam != null ? priorityParam : "ALL");
            request.setAttribute("allStatuses", TaskStatus.values());
            request.setAttribute("allPriorities", TaskPriority.values());

            request.getRequestDispatcher("/WEB-INF/views/manager/tasks.jsp").forward(request, response);
        } else {
            // Member view
            String statusParam = request.getParameter("status");
            String priorityParam = request.getParameter("priority");

            List<Task> tasks = taskService.getTasksByMemberAndFilters(currentUser.getId(), statusParam, priorityParam);

            request.setAttribute("tasks", tasks);
            request.setAttribute("selectedStatus", statusParam != null ? statusParam : "ALL");
            request.setAttribute("selectedPriority", priorityParam != null ? priorityParam : "ALL");
            request.setAttribute("allStatuses", TaskStatus.values());
            request.setAttribute("allPriorities", TaskPriority.values());

            request.getRequestDispatcher("/WEB-INF/views/member/tasks.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer actorUserId = currentUser.getId();
        Role userRole = currentUser.getRole();

        // Redirect target based on role to prevent unauthorized URL redirect
        String redirectTarget = (userRole == Role.TEAM_MEMBER)
                ? request.getContextPath() + "/member/tasks"
                : request.getContextPath() + "/manager/tasks";

        String action = request.getParameter("action");
        if (action == null) action = "create";

        try {
            switch (action) {
                case "create" -> {
                    // Security: Only Admins and Project Managers can create tasks
                    if (userRole == Role.TEAM_MEMBER) {
                        throw new com.protrack.exception.UnauthorizedAccessException(
                                "Access Denied: Team members are not permitted to create tasks.");
                    }

                    int projectId = ValidationUtil.parsePositiveInt(request.getParameter("projectId"), 0);

                    // If Project Manager, verify project ownership
                    if (userRole == Role.PROJECT_MANAGER) {
                        Project proj = projectService.getProjectById(projectId);
                        if (proj.getManagerId() != currentUser.getId()) {
                            throw new com.protrack.exception.UnauthorizedAccessException(
                                    "Access Denied: You can only create tasks for projects assigned to you.");
                        }
                    }

                    String title = request.getParameter("title");
                    String description = request.getParameter("description");
                    int assignedTo = ValidationUtil.parsePositiveInt(request.getParameter("assignedTo"), 0);
                    String priorityStr = request.getParameter("priority");
                    String statusStr = request.getParameter("status");
                    Date startDate = DateUtil.parseSqlDate(request.getParameter("startDate"));
                    Date dueDate = DateUtil.parseSqlDate(request.getParameter("dueDate"));

                    TaskPriority priority = TaskPriority.fromString(priorityStr);
                    TaskStatus status = TaskStatus.fromString(statusStr);

                    Task created = taskService.createTask(title, description, projectId,
                            assignedTo > 0 ? assignedTo : null, priority, status, startDate, dueDate, actorUserId);
                    session.setAttribute("flash_success", "Task \"" + created.getTaskTitle() + "\" created successfully.");
                }
                case "update" -> {
                    // Security: Only Admins and Project Managers can modify task definitions
                    if (userRole == Role.TEAM_MEMBER) {
                        throw new com.protrack.exception.UnauthorizedAccessException(
                                "Access Denied: Team members are not permitted to edit task definitions.");
                    }

                    int taskId = ValidationUtil.parsePositiveInt(request.getParameter("taskId"), 0);
                    Task existing = taskService.getTaskById(taskId);

                    // If Project Manager, verify project ownership
                    if (userRole == Role.PROJECT_MANAGER) {
                        Project proj = projectService.getProjectById(existing.getProjectId());
                        if (proj.getManagerId() != currentUser.getId()) {
                            throw new com.protrack.exception.UnauthorizedAccessException(
                                    "Access Denied: You can only edit tasks for projects assigned to you.");
                        }
                    }

                    String title = request.getParameter("title");
                    String description = request.getParameter("description");
                    int projectId = ValidationUtil.parsePositiveInt(request.getParameter("projectId"), 0);
                    int assignedTo = ValidationUtil.parsePositiveInt(request.getParameter("assignedTo"), 0);
                    String priorityStr = request.getParameter("priority");
                    String statusStr = request.getParameter("status");
                    Date startDate = DateUtil.parseSqlDate(request.getParameter("startDate"));
                    Date dueDate = DateUtil.parseSqlDate(request.getParameter("dueDate"));

                    TaskPriority priority = TaskPriority.fromString(priorityStr);
                    TaskStatus status = TaskStatus.fromString(statusStr);

                    taskService.updateTask(taskId, title, description, projectId,
                            assignedTo > 0 ? assignedTo : null, priority, status, startDate, dueDate, actorUserId);
                    session.setAttribute("flash_success", "Task details updated successfully.");
                }
                case "updateStatus" -> {
                    int taskId = ValidationUtil.parsePositiveInt(request.getParameter("taskId"), 0);
                    String statusStr = request.getParameter("status");
                    TaskStatus newStatus = TaskStatus.fromString(statusStr);

                    // Security: Verifies ownership for TEAM_MEMBER (can only change their own assigned task)
                    taskService.updateTaskStatusWithRoleCheck(taskId, newStatus, currentUser);
                    session.setAttribute("flash_success", "Task status updated to " + newStatus.getLabel() + ".");
                }
                case "delete" -> {
                    // Security: Only Admins and Project Managers can delete tasks
                    if (userRole == Role.TEAM_MEMBER) {
                        throw new com.protrack.exception.UnauthorizedAccessException(
                                "Access Denied: Team members are not permitted to delete tasks.");
                    }

                    int taskId = ValidationUtil.parsePositiveInt(request.getParameter("taskId"), 0);
                    Task existing = taskService.getTaskById(taskId);

                    if (userRole == Role.PROJECT_MANAGER) {
                        Project proj = projectService.getProjectById(existing.getProjectId());
                        if (proj.getManagerId() != currentUser.getId()) {
                            throw new com.protrack.exception.UnauthorizedAccessException(
                                    "Access Denied: You can only delete tasks for projects assigned to you.");
                        }
                    }

                    taskService.deleteTask(taskId, actorUserId);
                    session.setAttribute("flash_success", "Task deleted successfully.");
                }
                default -> session.setAttribute("flash_error", "Unknown action: " + action);
            }
        } catch (AppException e) {
            session.setAttribute("flash_error", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flash_error", "An unexpected system error occurred. Please try again.");
        }

        response.sendRedirect(redirectTarget);
    }
}
