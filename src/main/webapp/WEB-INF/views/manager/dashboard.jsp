<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.Task" %>
<%@ page import="com.protrack.model.ActivityLog" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    request.setAttribute("pageTitle", "Project Manager Dashboard");
    request.setAttribute("pageSubtitle", "Monitor sprint velocity, oversee team deliveries, and track task progression");
    request.setAttribute("activeMenu", "manager-dashboard");

    int totalTasks = (int) (request.getAttribute("totalTasks") != null ? request.getAttribute("totalTasks") : 0);
    int completedTasks = (int) (request.getAttribute("completedTasks") != null ? request.getAttribute("completedTasks") : 0);
    int pendingTasks = (int) (request.getAttribute("pendingTasks") != null ? request.getAttribute("pendingTasks") : 0);
    int overdueTasks = (int) (request.getAttribute("overdueTasks") != null ? request.getAttribute("overdueTasks") : 0);
    int managerCompletionRate = (int) (request.getAttribute("managerCompletionRate") != null ? request.getAttribute("managerCompletionRate") : 0);
    int managedProjectsCount = (int) (request.getAttribute("managedProjectsCount") != null ? request.getAttribute("managedProjectsCount") : 0);

    @SuppressWarnings("unchecked")
    List<Project> managedProjects = (List<Project>) request.getAttribute("managedProjects");
    @SuppressWarnings("unchecked")
    List<Task> recentTasks = (List<Task>) request.getAttribute("recentTasks");
    @SuppressWarnings("unchecked")
    List<Task> tasksDueToday = (List<Task>) request.getAttribute("tasksDueToday");
    @SuppressWarnings("unchecked")
    List<Task> overdueList = (List<Task>) request.getAttribute("overdueTasksList");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <!-- Urgent Alerts Bar (Tasks Due Today or Overdue) -->
        <% if (overdueTasks > 0 || (tasksDueToday != null && !tasksDueToday.isEmpty())) { %>
        <div class="alert alert-warning border-start border-4 border-warning d-flex align-items-center justify-content-between mb-4 shadow-sm bg-white" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi bi-bell-fill text-warning fs-3 me-3"></i>
                <div>
                    <h6 class="fw-bold mb-1 text-dark">Deadline Alerts: Action Recommended</h6>
                    <span class="small text-secondary">
                        <%= overdueTasks %> tasks overdue &bull;
                        <%= tasksDueToday != null ? tasksDueToday.size() : 0 %> tasks due today across your projects.
                    </span>
                </div>
            </div>
            <a href="<%= request.getContextPath() %>/manager/tasks" class="btn btn-sm btn-outline-warning fw-semibold">
                Manage Tasks <i class="bi bi-arrow-right ms-1"></i>
            </a>
        </div>
        <% } %>

        <!-- Row 1: Manager Metrics Cards -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-primary">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">My Projects</span>
                        <div class="stat-icon primary"><i class="bi bi-folder-check"></i></div>
                    </div>
                    <div class="stat-value"><%= managedProjectsCount %></div>
                    <small class="text-muted"><i class="bi bi-shield-check me-1"></i> Under your lead</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-info">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Total Tasks</span>
                        <div class="stat-icon info"><i class="bi bi-list-task"></i></div>
                    </div>
                    <div class="stat-value"><%= totalTasks %></div>
                    <small class="text-muted"><i class="bi bi-kanban me-1"></i> Assigned workload</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-success">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Completed</span>
                        <div class="stat-icon success"><i class="bi bi-check2-circle"></i></div>
                    </div>
                    <div class="stat-value"><%= completedTasks %></div>
                    <small class="text-muted"><i class="bi bi-patch-check me-1"></i> Delivered</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-warning">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Pending</span>
                        <div class="stat-icon warning"><i class="bi bi-hourglass-split"></i></div>
                    </div>
                    <div class="stat-value"><%= pendingTasks %></div>
                    <small class="text-muted"><i class="bi bi-arrow-repeat me-1"></i> In progress/review</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-danger">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Overdue</span>
                        <div class="stat-icon danger"><i class="bi bi-alarm-fill"></i></div>
                    </div>
                    <div class="stat-value text-danger"><%= overdueTasks %></div>
                    <small class="text-danger"><i class="bi bi-exclamation-circle me-1"></i> Critical attention</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-primary">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Completion %</span>
                        <div class="stat-icon primary"><i class="bi bi-pie-chart-fill"></i></div>
                    </div>
                    <div class="stat-value"><%= managerCompletionRate %>%</div>
                    <div class="progress progress-custom mt-2">
                        <div class="progress-bar" style="width: <%= managerCompletionRate %>%;"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Row 2: Projects Overview Cards -->
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="fw-bold text-dark m-0"><i class="bi bi-folder-fill text-primary me-2"></i>Managed Projects</h5>
            <a href="<%= request.getContextPath() %>/manager/projects" class="btn btn-sm btn-outline-custom">
                View All Projects <i class="bi bi-arrow-right ms-1"></i>
            </a>
        </div>

        <div class="row g-3 mb-4">
            <% if (managedProjects != null && !managedProjects.isEmpty()) {
                for (Project p : managedProjects) { %>
                <div class="col-md-6 col-xl-4">
                    <div class="protrack-card h-100">
                        <div class="protrack-card-body p-4 d-flex flex-column justify-content-between">
                            <div>
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="badge badge-custom <%= p.getStatusBadgeClass() %>">
                                        <%= p.getStatus() %>
                                    </span>
                                    <span class="small text-muted"><%= DateUtil.formatDisplayDate(p.getEndDate()) %></span>
                                </div>
                                <h5 class="fw-bold text-dark mb-2">
                                    <a href="<%= request.getContextPath() %>/manager/projects?action=view&id=<%= p.getId() %>" class="text-dark text-decoration-none">
                                        <%= ValidationUtil.escapeXml(p.getTitle()) %>
                                    </a>
                                </h5>
                                <p class="text-secondary small text-truncate" style="max-height: 40px;"><%= p.getDescription() != null ? ValidationUtil.escapeXml(p.getDescription()) : "No description provided." %></p>
                            </div>
                            <div class="pt-3 border-top mt-3">
                                <div class="d-flex justify-content-between align-items-center small mb-1">
                                    <span class="text-muted"><%= p.getCompletedTasks() %> / <%= p.getTotalTasks() %> Tasks</span>
                                    <span class="fw-bold text-dark"><%= p.getProgressPercentage() %>%</span>
                                </div>
                                <div class="progress progress-custom">
                                    <div class="progress-bar" style="width: <%= p.getProgressPercentage() %>%;"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <%  }
               } else { %>
                <div class="col-12">
                    <div class="protrack-card p-4 text-center text-muted">
                        <i class="bi bi-folder-plus fs-1 text-primary d-block mb-2"></i>
                        You have not created any projects yet.
                        <div class="mt-2">
                            <a href="<%= request.getContextPath() %>/manager/projects" class="btn btn-sm btn-primary">Create Your First Project</a>
                        </div>
                    </div>
                </div>
            <% } %>
        </div>

        <!-- Row 3: Recent Tasks Under Manager's Projects -->
        <div class="protrack-card">
            <div class="protrack-card-header">
                <h5 class="protrack-card-title">
                    <i class="bi bi-list-check"></i> Recent Tasks Across Projects
                </h5>
                <a href="<%= request.getContextPath() %>/manager/tasks" class="btn btn-sm btn-outline-custom">
                    Task Workspace <i class="bi bi-arrow-right ms-1"></i>
                </a>
            </div>
            <div class="table-responsive">
                <table class="table table-modern">
                    <thead>
                        <tr>
                            <th>Task Title</th>
                            <th>Project</th>
                            <th>Assigned Team Member</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Deadline</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (recentTasks != null && !recentTasks.isEmpty()) {
                            for (Task t : recentTasks) { %>
                            <tr>
                                <td>
                                    <div class="fw-semibold text-dark"><%= ValidationUtil.escapeXml(t.getTaskTitle()) %></div>
                                    <div class="small text-muted text-truncate" style="max-width: 250px;"><%= t.getDescription() != null ? ValidationUtil.escapeXml(t.getDescription()) : "" %></div>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border"><%= ValidationUtil.escapeXml(t.getProjectName()) %></span>
                                </td>
                                <td>
                                    <span class="small fw-medium"><%= t.getAssignedToName() != null ? ValidationUtil.escapeXml(t.getAssignedToName()) : "Unassigned" %></span>
                                </td>
                                <td>
                                    <span class="badge badge-custom <%= t.getPriority().getBadgeClass() %>">
                                        <%= t.getPriority().getLabel() %>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge badge-custom <%= t.getStatus().getBadgeClass() %>">
                                        <%= t.getStatus().getLabel() %>
                                    </span>
                                </td>
                                <td>
                                    <div class="small fw-medium"><%= DateUtil.formatDisplayDate(t.getDueDate()) %></div>
                                    <% if (t.isOverdue()) { %>
                                        <span class="badge badge-overdue small mt-1"><i class="bi bi-alarm"></i> Overdue</span>
                                    <% } else if (t.isDueToday()) { %>
                                        <span class="badge bg-warning text-dark small mt-1">Due Today</span>
                                    <% } %>
                                </td>
                            </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="6" class="text-center py-4 text-muted">No tasks currently assigned under your projects.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /.content-body -->

<jsp:include page="../common/footer.jsp" />
