<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Task" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.ActivityLog" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    request.setAttribute("pageTitle", "Team Member Cockpit");
    request.setAttribute("pageSubtitle", "Track your assigned sprint deliverables, update progress and hit deadlines");
    request.setAttribute("activeMenu", "member-dashboard");

    int totalTasks = (int) (request.getAttribute("totalTasks") != null ? request.getAttribute("totalTasks") : 0);
    int completedTasks = (int) (request.getAttribute("completedTasks") != null ? request.getAttribute("completedTasks") : 0);
    int inProgressTasks = (int) (request.getAttribute("inProgressTasks") != null ? request.getAttribute("inProgressTasks") : 0);
    int reviewTasks = (int) (request.getAttribute("reviewTasks") != null ? request.getAttribute("reviewTasks") : 0);
    int overdueTasks = (int) (request.getAttribute("overdueTasks") != null ? request.getAttribute("overdueTasks") : 0);
    int memberCompletionRate = (int) (request.getAttribute("memberCompletionRate") != null ? request.getAttribute("memberCompletionRate") : 0);

    @SuppressWarnings("unchecked")
    List<Task> myTasks = (List<Task>) request.getAttribute("myTasks");
    @SuppressWarnings("unchecked")
    List<Task> dueToday = (List<Task>) request.getAttribute("tasksDueToday");
    @SuppressWarnings("unchecked")
    List<Project> myProjects = (List<Project>) request.getAttribute("myProjects");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <!-- Urgent Deadline Alerts -->
        <% if (overdueTasks > 0 || (dueToday != null && !dueToday.isEmpty())) { %>
        <div class="alert alert-danger border-start border-4 border-danger d-flex align-items-center justify-content-between mb-4 shadow-sm bg-white" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi bi-alarm-fill text-danger fs-3 me-3"></i>
                <div>
                    <h6 class="fw-bold mb-1 text-dark">Upcoming & Overdue Deadlines</h6>
                    <span class="small text-secondary">
                        You have <%= overdueTasks %> overdue tasks and <%= dueToday != null ? dueToday.size() : 0 %> tasks due today.
                    </span>
                </div>
            </div>
            <a href="<%= request.getContextPath() %>/member/tasks" class="btn btn-sm btn-outline-danger fw-semibold">
                View My Tasks
            </a>
        </div>
        <% } %>

        <!-- Row 1: Member Metrics Cards -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-info">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Assigned</span>
                        <div class="stat-icon info"><i class="bi bi-list-task"></i></div>
                    </div>
                    <div class="stat-value"><%= totalTasks %></div>
                    <small class="text-muted"><i class="bi bi-person-check me-1"></i> Total deliverables</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-primary">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">In Progress</span>
                        <div class="stat-icon primary"><i class="bi bi-gear-wide-connected"></i></div>
                    </div>
                    <div class="stat-value text-primary"><%= inProgressTasks %></div>
                    <small class="text-muted"><i class="bi bi-arrow-repeat me-1"></i> Active work</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-warning">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">In Review</span>
                        <div class="stat-icon warning"><i class="bi bi-eyeglasses"></i></div>
                    </div>
                    <div class="stat-value text-warning"><%= reviewTasks %></div>
                    <small class="text-muted"><i class="bi bi-clipboard-check me-1"></i> Pending QA</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-success">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Completed</span>
                        <div class="stat-icon success"><i class="bi bi-check-circle-fill"></i></div>
                    </div>
                    <div class="stat-value text-success"><%= completedTasks %></div>
                    <small class="text-muted"><i class="bi bi-trophy me-1"></i> Finished</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-danger">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Overdue</span>
                        <div class="stat-icon danger"><i class="bi bi-alarm-fill"></i></div>
                    </div>
                    <div class="stat-value text-danger"><%= overdueTasks %></div>
                    <small class="text-danger"><i class="bi bi-exclamation-octagon me-1"></i> Past due date</small>
                </div>
            </div>

            <div class="col-sm-6 col-xl-2">
                <div class="stat-card stat-primary">
                    <div class="d-flex align-items-center justify-content-between">
                        <span class="stat-label">Progress</span>
                        <div class="stat-icon primary"><i class="bi bi-graph-up"></i></div>
                    </div>
                    <div class="stat-value"><%= memberCompletionRate %>%</div>
                    <div class="progress progress-custom mt-2">
                        <div class="progress-bar" style="width: <%= memberCompletionRate %>%;"></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- Left: My Tasks List with Quick Status Switcher -->
            <div class="col-lg-8">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-list-check"></i> My Assigned Tasks
                        </h5>
                        <a href="<%= request.getContextPath() %>/member/tasks" class="btn btn-sm btn-outline-custom">
                            Full Task List <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-modern">
                            <thead>
                                <tr>
                                    <th>Task Details</th>
                                    <th>Project</th>
                                    <th>Priority</th>
                                    <th>Status</th>
                                    <th>Due Date</th>
                                    <th class="text-end">Update</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (myTasks != null && !myTasks.isEmpty()) {
                                    for (Task t : myTasks) { %>
                                    <tr>
                                        <td>
                                            <div class="fw-semibold text-dark"><%= ValidationUtil.escapeXml(t.getTaskTitle()) %></div>
                                            <div class="small text-muted text-truncate" style="max-width: 250px;"><%= t.getDescription() != null ? ValidationUtil.escapeXml(t.getDescription()) : "" %></div>
                                        </td>
                                        <td>
                                            <span class="badge bg-light text-dark border text-truncate" style="max-width: 150px;"><%= ValidationUtil.escapeXml(t.getProjectName()) %></span>
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
                                        <td class="text-end">
                                            <button type="button" class="btn btn-sm btn-outline-custom" title="Change Status"
                                                    data-bs-toggle="modal" data-bs-target="#updateStatusModal"
                                                    data-id="<%= t.getId() %>"
                                                    data-title="<%= ValidationUtil.escapeXml(t.getTaskTitle()) %>"
                                                    data-status="<%= t.getStatus().name() %>">
                                                <i class="bi bi-arrow-repeat text-primary"></i>
                                            </button>
                                        </td>
                                    </tr>
                                <%  }
                                   } else { %>
                                    <tr>
                                        <td colspan="6" class="text-center py-5 text-muted">
                                            <i class="bi bi-check-circle fs-1 text-success d-block mb-2"></i>
                                            All caught up! No active tasks currently assigned to you.
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Right: My Assigned Projects -->
            <div class="col-lg-4">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-folder2-open"></i> My Projects (<%= myProjects != null ? myProjects.size() : 0 %>)
                        </h5>
                    </div>
                    <div class="protrack-card-body p-0">
                        <ul class="list-group list-group-flush">
                            <% if (myProjects != null && !myProjects.isEmpty()) {
                                for (Project p : myProjects) { %>
                                <li class="list-group-item p-3">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <h6 class="fw-bold mb-0 text-dark"><%= ValidationUtil.escapeXml(p.getTitle()) %></h6>
                                        <span class="badge badge-custom <%= p.getStatusBadgeClass() %>"><%= p.getStatus() %></span>
                                    </div>
                                    <div class="small text-muted mb-2">
                                        <i class="bi bi-person me-1"></i> PM: <%= p.getManagerName() != null ? ValidationUtil.escapeXml(p.getManagerName()) : "Unassigned" %>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center small mb-1">
                                        <span class="text-muted"><%= p.getCompletedTasks() %> / <%= p.getTotalTasks() %> tasks</span>
                                        <span class="fw-bold text-dark"><%= p.getProgressPercentage() %>%</span>
                                    </div>
                                    <div class="progress progress-custom">
                                        <div class="progress-bar" style="width: <%= p.getProgressPercentage() %>%;"></div>
                                    </div>
                                </li>
                            <%  }
                               } else { %>
                                <li class="list-group-item text-center py-4 text-muted">
                                    Not currently allocated to any project teams.
                                </li>
                            <% } %>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

    </div><!-- /.content-body -->

<!-- Modal: Quick Status Updater -->
<div class="modal fade" id="updateStatusModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/member/tasks" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="updateStatus">
                <input type="hidden" id="statusTaskId" name="taskId" value="">
                <div class="modal-header bg-light">
                    <h6 class="modal-title fw-bold"><i class="bi bi-arrow-repeat text-primary me-2"></i>Update Task Status</h6>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-3">
                    <p class="small text-secondary mb-2" id="statusTaskTitle">Task Title</p>
                    <label class="form-label small fw-semibold">New Status</label>
                    <select class="form-select" id="statusTaskSelect" name="status" required>
                        <option value="TODO">To Do</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="REVIEW">In Review</option>
                        <option value="COMPLETED">Completed</option>
                    </select>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-sm btn-primary">Update Status</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
