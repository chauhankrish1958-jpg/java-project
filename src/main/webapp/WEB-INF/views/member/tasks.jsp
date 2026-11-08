<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Task" %>
<%@ page import="com.protrack.model.TaskStatus" %>
<%@ page import="com.protrack.model.TaskPriority" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%
    request.setAttribute("pageTitle", "My Assigned Tasks");
    request.setAttribute("pageSubtitle", "View assigned tasks, update completion status and track milestones");
    request.setAttribute("activeMenu", "member-tasks");

    @SuppressWarnings("unchecked")
    List<Task> taskList = (List<Task>) request.getAttribute("tasks");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    String selectedPriority = (String) request.getAttribute("selectedPriority");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <!-- Filter Bar -->
        <div class="protrack-card mb-4">
            <div class="protrack-card-body p-3">
                <form action="<%= request.getContextPath() %>/member/tasks" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-6">
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-search"></i></span>
                            <input type="text" class="form-control table-search-input" data-target="memberTasksTable"
                                   placeholder="Filter tasks by keyword or project...">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" name="status" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equals(selectedStatus) ? "selected" : "" %>>All Statuses</option>
                            <% for (TaskStatus ts : TaskStatus.values()) { %>
                                <option value="<%= ts.name() %>" <%= ts.name().equals(selectedStatus) ? "selected" : "" %>>
                                    <%= ts.getLabel() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="priority" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equals(selectedPriority) ? "selected" : "" %>>All Priorities</option>
                            <% for (TaskPriority tp : TaskPriority.values()) { %>
                                <option value="<%= tp.name() %>" <%= tp.name().equals(selectedPriority) ? "selected" : "" %>>
                                    <%= tp.getLabel() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-1">
                        <a href="<%= request.getContextPath() %>/member/tasks" class="btn btn-outline-custom w-100" title="Reset Filters">
                            <i class="bi bi-arrow-counterclockwise"></i>
                        </a>
                    </div>
                </form>
            </div>
        </div>

        <!-- My Tasks Table Card -->
        <div class="protrack-card">
            <div class="protrack-card-header">
                <h5 class="protrack-card-title">
                    <i class="bi bi-check2-square"></i> Tasks Queue (<%= taskList != null ? taskList.size() : 0 %>)
                </h5>
                <span class="small text-muted">Click the update icon to transition task status</span>
            </div>
            <div class="table-responsive">
                <table class="table table-modern" id="memberTasksTable">
                    <thead>
                        <tr>
                            <th style="width: 70px;">ID</th>
                            <th>Task Title & Scope</th>
                            <th>Project</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Due Date</th>
                            <th class="text-end" style="width: 140px;">Status Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (taskList != null && !taskList.isEmpty()) {
                            for (Task t : taskList) { %>
                            <tr>
                                <td class="text-muted fw-semibold">#<%= t.getId() %></td>
                                <td>
                                    <div class="fw-semibold text-dark"><%= t.getTaskTitle() %></div>
                                    <div class="small text-muted text-truncate" style="max-width: 320px;"><%= t.getDescription() != null ? t.getDescription() : "" %></div>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border text-truncate" style="max-width: 180px;"><%= t.getProjectName() %></span>
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
                                    <button type="button" class="btn btn-sm btn-outline-primary"
                                            data-bs-toggle="modal" data-bs-target="#updateStatusModal"
                                            data-id="<%= t.getId() %>"
                                            data-title="<%= t.getTaskTitle() %>"
                                            data-status="<%= t.getStatus().name() %>">
                                        <i class="bi bi-arrow-repeat me-1"></i> Update
                                    </button>
                                </td>
                            </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="7" class="text-center py-5 text-muted">
                                    <i class="bi bi-check2-circle fs-1 text-success d-block mb-2"></i>
                                    No tasks found matching your filter selection.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /.content-body -->

<!-- Modal: Quick Status Updater -->
<div class="modal fade" id="updateStatusModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/member/tasks" method="POST">
                <input type="hidden" name="action" value="updateStatus">
                <input type="hidden" id="statusTaskId" name="taskId" value="">
                <div class="modal-header bg-light">
                    <h6 class="modal-title fw-bold"><i class="bi bi-arrow-repeat text-primary me-2"></i>Update Task Status</h6>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-3">
                    <p class="small text-secondary mb-2" id="statusTaskTitle">Task Title</p>
                    <label class="form-label small fw-semibold">Select New Status</label>
                    <select class="form-select" id="statusTaskSelect" name="status" required>
                        <option value="TODO">To Do</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="REVIEW">In Review</option>
                        <option value="COMPLETED">Completed</option>
                    </select>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-sm btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-sm btn-primary">Save Status</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
