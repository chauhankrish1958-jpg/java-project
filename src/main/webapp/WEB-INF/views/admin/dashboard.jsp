<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.Task" %>
<%@ page import="com.protrack.model.ActivityLog" %>
<%@ page import="com.protrack.model.TaskPriority" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    request.setAttribute("pageTitle", "Admin Dashboard");
    request.setAttribute("pageSubtitle", "System overview, active project lifecycles and performance analytics");
    request.setAttribute("activeMenu", "admin-dashboard");

    int totalUsers = (int) (request.getAttribute("totalUsers") != null ? request.getAttribute("totalUsers") : 0);
    int totalProjects = (int) (request.getAttribute("totalProjects") != null ? request.getAttribute("totalProjects") : 0);
    int activeProjects = (int) (request.getAttribute("activeProjects") != null ? request.getAttribute("activeProjects") : 0);
    int completedProjects = (int) (request.getAttribute("completedProjects") != null ? request.getAttribute("completedProjects") : 0);
    int totalTasks = (int) (request.getAttribute("totalTasks") != null ? request.getAttribute("totalTasks") : 0);
    int overdueTasks = (int) (request.getAttribute("overdueTasks") != null ? request.getAttribute("overdueTasks") : 0);
    int overallCompletion = (int) (request.getAttribute("overallCompletion") != null ? request.getAttribute("overallCompletion") : 0);

    @SuppressWarnings("unchecked")
    Map<String, Integer> statusCounts = (Map<String, Integer>) request.getAttribute("projectStatusCounts");
    @SuppressWarnings("unchecked")
    Map<TaskPriority, Integer> priorityCounts = (Map<TaskPriority, Integer>) request.getAttribute("priorityDistribution");
    @SuppressWarnings("unchecked")
    List<Project> recentProjects = (List<Project>) request.getAttribute("recentProjects");
    @SuppressWarnings("unchecked")
    List<ActivityLog> recentActivities = (List<ActivityLog>) request.getAttribute("recentActivities");
    @SuppressWarnings("unchecked")
    List<Task> overdueList = (List<Task>) request.getAttribute("overdueTasksList");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <!-- Overdue Tasks Urgent Alert Banner (if any) -->
        <% if (overdueTasks > 0) { %>
        <div class="alert alert-warning border-start border-4 border-warning d-flex align-items-center justify-content-between mb-4 shadow-sm bg-white" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi bi-exclamation-octagon-fill text-warning fs-3 me-3"></i>
                <div>
                    <h6 class="fw-bold mb-1 text-dark">Attention Required: <%= overdueTasks %> Overdue Tasks Identified</h6>
                    <span class="small text-muted">Several assigned tasks have exceeded their target delivery date across active projects.</span>
                </div>
            </div>
            <a href="<%= request.getContextPath() %>/admin/projects" class="btn btn-sm btn-outline-warning fw-semibold">
                Inspect Projects
            </a>
        </div>
        <% } %>

        <!-- Row 1: Key Performance Metric Cards -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-primary">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Total Users</span>
                        <div class="stat-icon primary"><i class="bi bi-people-fill"></i></div>
                    </div>
                    <div class="stat-value"><%= totalUsers %></div>
                    <small class="text-muted"><i class="bi bi-person-check me-1"></i> Active accounts</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-info">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Total Projects</span>
                        <div class="stat-icon info"><i class="bi bi-folder-fill"></i></div>
                    </div>
                    <div class="stat-value"><%= totalProjects %></div>
                    <small class="text-muted"><i class="bi bi-collection me-1"></i> Managed initiatives</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-primary">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Active Projects</span>
                        <div class="stat-icon primary"><i class="bi bi-arrow-repeat"></i></div>
                    </div>
                    <div class="stat-value"><%= activeProjects %></div>
                    <small class="text-muted"><i class="bi bi-activity me-1"></i> In execution/plan</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-success">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Completed</span>
                        <div class="stat-icon success"><i class="bi bi-check-circle-fill"></i></div>
                    </div>
                    <div class="stat-value"><%= completedProjects %></div>
                    <small class="text-muted"><i class="bi bi-trophy me-1"></i> Delivered on time</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-warning">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Total Tasks</span>
                        <div class="stat-icon warning"><i class="bi bi-list-task"></i></div>
                    </div>
                    <div class="stat-value"><%= totalTasks %></div>
                    <small class="text-muted"><i class="bi bi-pie-chart me-1"></i> <%= overallCompletion %>% completed</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-danger">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Overdue Tasks</span>
                        <div class="stat-icon danger"><i class="bi bi-alarm-fill"></i></div>
                    </div>
                    <div class="stat-value text-danger"><%= overdueTasks %></div>
                    <small class="text-danger"><i class="bi bi-exclamation-triangle me-1"></i> Past due date</small>
                </div>
            </div>
        </div>

        <!-- Row 2: Analytics Charts & System Progress -->
        <div class="row g-4 mb-4">
            <!-- Left Chart: Project Status Breakdown -->
            <div class="col-lg-6">
                <div class="protrack-card h-100">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-pie-chart-fill"></i> Projects by Status Distribution
                        </h5>
                        <span class="badge bg-light text-secondary border">Real-time DB</span>
                    </div>
                    <div class="protrack-card-body">
                        <div style="height: 250px;">
                            <canvas id="projectStatusChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Chart: Tasks by Priority -->
            <div class="col-lg-6">
                <div class="protrack-card h-100">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-bar-chart-fill"></i> Task Priority Breakdown
                        </h5>
                        <span class="badge bg-light text-secondary border">Active Backlog</span>
                    </div>
                    <div class="protrack-card-body">
                        <div style="height: 250px;">
                            <canvas id="taskPriorityChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Row 3: Recent Projects Table & Activity Log Timeline -->
        <div class="row g-4">
            <!-- Left: Recent Projects -->
            <div class="col-lg-8">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-kanban"></i> Monitored Projects
                        </h5>
                        <a href="<%= request.getContextPath() %>/admin/projects" class="btn btn-sm btn-outline-custom">
                            View All Projects <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-modern">
                            <thead>
                                <tr>
                                    <th>Project Title</th>
                                    <th>Manager</th>
                                    <th>Status</th>
                                    <th>Progress</th>
                                    <th>Deadline</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (recentProjects != null && !recentProjects.isEmpty()) {
                                    for (Project p : recentProjects) { %>
                                    <tr>
                                        <td>
                                            <a href="<%= request.getContextPath() %>/admin/projects?action=view&id=<%= p.getId() %>" class="fw-semibold text-dark text-decoration-none">
                                                <%= ValidationUtil.escapeXml(p.getTitle()) %>
                                            </a>
                                            <div class="small text-muted"><%= p.getTotalTasks() %> total tasks</div>
                                        </td>
                                        <td>
                                            <span class="small fw-medium"><%= p.getManagerName() != null ? ValidationUtil.escapeXml(p.getManagerName()) : "Unassigned" %></span>
                                        </td>
                                        <td>
                                            <span class="badge badge-custom <%= p.getStatusBadgeClass() %>">
                                                <%= p.getStatus() %>
                                            </span>
                                        </td>
                                        <td style="min-width: 140px;">
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="progress progress-custom flex-grow-1">
                                                    <div class="progress-bar" style="width: <%= p.getProgressPercentage() %>%;"></div>
                                                </div>
                                                <span class="small fw-semibold text-secondary"><%= p.getProgressPercentage() %>%</span>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="small text-muted"><%= DateUtil.formatDisplayDate(p.getEndDate()) %></span>
                                        </td>
                                        <td class="text-end">
                                            <a href="<%= request.getContextPath() %>/admin/projects?action=view&id=<%= p.getId() %>" class="btn btn-sm btn-outline-custom py-1 px-2" title="View Details">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                <%  }
                                   } else { %>
                                    <tr>
                                        <td colspan="6" class="text-center py-4 text-muted">No projects found in database.</td>
                                     </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Right: Live Activity Feed -->
            <div class="col-lg-4">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-clock-history"></i> Recent System Audit
                        </h5>
                        <a href="<%= request.getContextPath() %>/activity" class="btn btn-sm btn-outline-custom">
                            Full Log
                        </a>
                    </div>
                    <div class="protrack-card-body">
                        <% if (recentActivities != null && !recentActivities.isEmpty()) { %>
                        <div class="timeline">
                            <% for (ActivityLog act : recentActivities) { %>
                            <div class="timeline-item">
                                <div class="timeline-marker"></div>
                                <div class="timeline-content">
                                    <div class="timeline-title"><%= ValidationUtil.escapeXml(act.getDetails()) %></div>
                                    <div class="timeline-time">
                                        <i class="bi bi-clock me-1"></i><%= act.getFormattedCreatedAt() %> &bull;
                                        <strong><%= act.getUserName() != null ? ValidationUtil.escapeXml(act.getUserName()) : "System" %></strong>
                                    </div>
                                </div>
                            </div>
                            <% } %>
                        </div>
                        <% } else { %>
                        <p class="text-muted small text-center my-3">No recent activities recorded.</p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

    </div><!-- /.content-body -->

<jsp:include page="../common/footer.jsp" />

<!-- Initialize Chart.js with dynamic database aggregates -->
<script>
document.addEventListener('DOMContentLoaded', () => {
    const statusData = {
        PLANNING: <%= statusCounts != null && statusCounts.get("PLANNING") != null ? statusCounts.get("PLANNING") : 0 %>,
        IN_PROGRESS: <%= statusCounts != null && statusCounts.get("IN_PROGRESS") != null ? statusCounts.get("IN_PROGRESS") : 0 %>,
        ON_HOLD: <%= statusCounts != null && statusCounts.get("ON_HOLD") != null ? statusCounts.get("ON_HOLD") : 0 %>,
        COMPLETED: <%= statusCounts != null && statusCounts.get("COMPLETED") != null ? statusCounts.get("COMPLETED") : 0 %>,
        CANCELLED: <%= statusCounts != null && statusCounts.get("CANCELLED") != null ? statusCounts.get("CANCELLED") : 0 %>
    };

    const priorityData = {
        LOW: <%= priorityCounts != null && priorityCounts.get(TaskPriority.LOW) != null ? priorityCounts.get(TaskPriority.LOW) : 0 %>,
        MEDIUM: <%= priorityCounts != null && priorityCounts.get(TaskPriority.MEDIUM) != null ? priorityCounts.get(TaskPriority.MEDIUM) : 0 %>,
        HIGH: <%= priorityCounts != null && priorityCounts.get(TaskPriority.HIGH) != null ? priorityCounts.get(TaskPriority.HIGH) : 0 %>,
        CRITICAL: <%= priorityCounts != null && priorityCounts.get(TaskPriority.CRITICAL) != null ? priorityCounts.get(TaskPriority.CRITICAL) : 0 %>
    };

    if (window.initAdminCharts) {
        window.initAdminCharts(statusData, priorityData);
    }
});
</script>
