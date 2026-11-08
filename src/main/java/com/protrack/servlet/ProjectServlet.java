package com.protrack.servlet;

import com.protrack.exception.AppException;
import com.protrack.model.Project;
import com.protrack.model.Role;
import com.protrack.model.Task;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet managing projects across Admin, Manager and Team Member roles.
 */
@WebServlet(name = "ProjectServlet", urlPatterns = {
        "/admin/projects",
        "/manager/projects",
        "/member/projects"
})
public class ProjectServlet extends HttpServlet {

    private final ProjectService projectService = new ProjectService();
    private final UserService userService = new UserService();
    private final TaskService taskService = new TaskService();

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
        String action = request.getParameter("action");

        // Single Project Detail View
        if ("view".equalsIgnoreCase(action)) {
            int projectId = ValidationUtil.parsePositiveInt(request.getParameter("id"), 0);
            try {
                Project project = projectService.getProjectById(projectId);
                List<User> members = projectService.getProjectMembers(projectId);
                List<Task> tasks = taskService.getTasksByProject(projectId);
                List<User> allTeamMembers = userService.getUsersByRole(Role.TEAM_MEMBER);

                request.setAttribute("project", project);
                request.setAttribute("members", members);
                request.setAttribute("tasks", tasks);
                request.setAttribute("allTeamMembers", allTeamMembers);

                // View template depends on role
                if (uri.contains("/admin/")) {
                    request.getRequestDispatcher("/WEB-INF/views/admin/project-detail.jsp").forward(request, response);
                } else if (uri.contains("/manager/")) {
                    request.getRequestDispatcher("/WEB-INF/views/manager/project-detail.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/WEB-INF/views/member/project-detail.jsp").forward(request, response);
                }
                return;
            } catch (AppException e) {
                session.setAttribute("flash_error", e.getMessage());
                response.sendRedirect(request.getContextPath() + currentUser.getDashboardUrl());
                return;
            }
        }

        String query = request.getParameter("q");
        String status = request.getParameter("status");

        if (uri.contains("/admin/projects")) {
            List<Project> projects = projectService.searchProjects(query, status, null);
            List<User> managers = userService.getUsersByRole(Role.PROJECT_MANAGER);
            List<User> teamMembers = userService.getUsersByRole(Role.TEAM_MEMBER);

            request.setAttribute("projects", projects);
            request.setAttribute("managers", managers);
            request.setAttribute("teamMembers", teamMembers);
            request.setAttribute("searchQuery", query != null ? query.trim() : "");
            request.setAttribute("selectedStatus", status != null ? status.trim() : "ALL");

            request.getRequestDispatcher("/WEB-INF/views/admin/projects.jsp").forward(request, response);
        } else if (uri.contains("/manager/projects")) {
            List<Project> projects = projectService.searchProjects(query, status, currentUser.getId());
            List<User> teamMembers = userService.getUsersByRole(Role.TEAM_MEMBER);

            request.setAttribute("projects", projects);
            request.setAttribute("teamMembers", teamMembers);
            request.setAttribute("searchQuery", query != null ? query.trim() : "");
            request.setAttribute("selectedStatus", status != null ? status.trim() : "ALL");

            request.getRequestDispatcher("/WEB-INF/views/manager/projects.jsp").forward(request, response);
        } else {
            // Member view
            List<Project> projects = projectService.getProjectsByMember(currentUser.getId());
            request.setAttribute("projects", projects);
            request.getRequestDispatcher("/WEB-INF/views/member/projects.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        Integer actorUserId = (currentUser != null) ? currentUser.getId() : null;

        String uri = request.getRequestURI();
        String redirectTarget = uri.contains("/admin/") ? request.getContextPath() + "/admin/projects"
                : request.getContextPath() + "/manager/projects";

        String action = request.getParameter("action");
        if (action == null) action = "create";

        try {
            switch (action) {
                case "create" -> {
                    String title = request.getParameter("title");
                    String description = request.getParameter("description");
                    Date startDate = DateUtil.parseSqlDate(request.getParameter("startDate"));
                    Date endDate = DateUtil.parseSqlDate(request.getParameter("endDate"));
                    String status = request.getParameter("status");

                    int managerId;
                    if (currentUser != null && currentUser.getRole() == Role.PROJECT_MANAGER) {
                        managerId = currentUser.getId();
                    } else {
                        managerId = ValidationUtil.parsePositiveInt(request.getParameter("managerId"), (currentUser != null ? currentUser.getId() : 0));
                    }

                    String[] memberIdsRaw = request.getParameterValues("memberIds");
                    List<Integer> memberIds = new ArrayList<>();
                    if (memberIdsRaw != null) {
                        for (String m : memberIdsRaw) {
                            int mid = ValidationUtil.parsePositiveInt(m, 0);
                            if (mid > 0) memberIds.add(mid);
                        }
                    }

                    Project created = projectService.createProject(title, description, startDate, endDate, managerId, status, memberIds, actorUserId);
                    session.setAttribute("flash_success", "Project \"" + created.getTitle() + "\" created successfully.");
                }
                case "update" -> {
                    int projectId = ValidationUtil.parsePositiveInt(request.getParameter("projectId"), 0);
                    String title = request.getParameter("title");
                    String description = request.getParameter("description");
                    Date startDate = DateUtil.parseSqlDate(request.getParameter("startDate"));
                    Date endDate = DateUtil.parseSqlDate(request.getParameter("endDate"));
                    String status = request.getParameter("status");

                    int managerId;
                    if (currentUser != null && currentUser.getRole() == Role.PROJECT_MANAGER) {
                        managerId = currentUser.getId();
                    } else {
                        managerId = ValidationUtil.parsePositiveInt(request.getParameter("managerId"), (currentUser != null ? currentUser.getId() : 0));
                    }

                    projectService.updateProject(projectId, title, description, startDate, endDate, managerId, status, actorUserId);
                    session.setAttribute("flash_success", "Project updated successfully.");
                }
                case "delete" -> {
                    int projectId = ValidationUtil.parsePositiveInt(request.getParameter("projectId"), 0);
                    projectService.deleteProject(projectId, actorUserId);
                    session.setAttribute("flash_success", "Project deleted successfully.");
                }
                case "assignMember" -> {
                    int projectId = ValidationUtil.parsePositiveInt(request.getParameter("projectId"), 0);
                    int userId = ValidationUtil.parsePositiveInt(request.getParameter("userId"), 0);
                    projectService.assignMember(projectId, userId, actorUserId);
                    session.setAttribute("flash_success", "Team member assigned to project.");
                    redirectTarget += "?action=view&id=" + projectId;
                }
                case "removeMember" -> {
                    int projectId = ValidationUtil.parsePositiveInt(request.getParameter("projectId"), 0);
                    int userId = ValidationUtil.parsePositiveInt(request.getParameter("userId"), 0);
                    projectService.removeMember(projectId, userId, actorUserId);
                    session.setAttribute("flash_success", "Team member removed from project.");
                    redirectTarget += "?action=view&id=" + projectId;
                }
                default -> session.setAttribute("flash_error", "Unknown action: " + action);
            }
        } catch (AppException e) {
            session.setAttribute("flash_error", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flash_error", "Operation failed: " + e.getMessage());
        }

        response.sendRedirect(redirectTarget);
    }
}
