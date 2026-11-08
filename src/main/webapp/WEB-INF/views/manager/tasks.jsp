<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Task" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.model.TaskStatus" %>
<%@ page import="com.protrack.model.TaskPriority" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%
    request.setAttribute("pageTitle", "Task Management");
    request.setAttribute("pageSubtitle", "Assign deliverables to team members, adjust priorities and track deadlines");
    request.setAttribute("activeMenu", "manager-tasks");

    @SuppressWarnings("unchecked")
    List<Task> taskList = (List<Task>) request.getAttribute("tasks");
    @SuppressWarnings("unchecked")
    List<Project> projects = (List<Project>) request.getAttribute("projects");
    @SuppressWarnings("unchecked")
    List<User> teamMembers = (List<User>) request.getAttribute("teamMembers");

    String searchQuery = (String) request.getAttribute("searchQuery");
    int selectedProject = (int) (request.getAttribute("selectedProject") != null ? request.getAttribute("selectedProject") : 0);
    int selectedAssignee = (int) (request.getAttribute("selectedAssignee") != null ? request.getAttribute("selectedAssignee") : 0);
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    String selectedPriority = (String) request.getAttribute("selectedPriority");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <!-- Filter & Actions Bar -->
        <div class="protrack-card mb-4">
            <div class="protrack-card-body p-3">
                <form action="<%= request.getContextPath() %>/manager/tasks" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-3">
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-search"></i></span>
                            <input type="text" class="form-control table-search-input" data-target="mgrTasksTable"
                                   name="q" value="<%= searchQuery != null ? searchQuery : "" %>"
                                   placeholder="Search task title...">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="projectId" onchange="this.form.submit()">
                            <option value="0">All Projects</option>
                            <% if (projects != null) {
                                for (Project p : projects) { %>
                                <option value="<%= p.getId() %>" <%= p.getId() == selectedProject ? "selected" : "" %>>
                                    <%= p.getTitle() %>
                                </option>
                            <%  }
                               } %>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="assignedTo" onchange="this.form.submit()">
                            <option value="0">All Assignees</option>
                            <% if (teamMembers != null) {
                                for (User u : teamMembers) { %>
                                <option value="<%= u.getId() %>" <%= u.getId() == selectedAssignee ? "selected" : "" %>>
                                    <%= u.getFullName() %>
                                </option>
                            <%  }
                               } %>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="status" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equals(selectedStatus) ? "selected" : "" %>>All Statuses</option>
                            <% for (TaskStatus ts : TaskStatus.values()) { %>
                                <option value="<%= ts.name() %>" <%= ts.name().equals(selectedStatus) ? "selected" : "" %>>
                                    <%= ts.getLabel() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-1">
                        <select class="form-select" name="priority" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equals(selectedPriority) ? "selected" : "" %>>Priority</option>
                            <% for (TaskPriority tp : TaskPriority.values()) { %>
                                <option value="<%= tp.name() %>" <%= tp.name().equals(selectedPriority) ? "selected" : "" %>>
                                    <%= tp.getLabel() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="col-md-2 text-md-end">
                        <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#createTaskModal">
                            <i class="bi bi-plus-circle-fill me-1"></i> New Task
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Tasks Table Card -->
        <div class="protrack-card">
            <div class="protrack-card-header">
                <h5 class="protrack-card-title">
                    <i class="bi bi-list-task"></i> Task Backlog (<%= taskList != null ? taskList.size() : 0 %>)
                </h5>
            </div>
            <div class="table-responsive">
                <table class="table table-modern" id="mgrTasksTable">
                    <thead>
                        <tr>
                            <th style="width: 70px;">ID</th>
                            <th>Task Details</th>
                            <th>Project</th>
                            <th>Assignee</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Due Date</th>
                            <th class="text-end" style="width: 120px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (taskList != null && !taskList.isEmpty()) {
                            for (Task t : taskList) { %>
                            <tr>
                                <td class="text-muted fw-semibold">#<%= t.getId() %></td>
                                <td>
                                    <div class="fw-semibold text-dark"><%= t.getTaskTitle() %></div>
                                    <div class="small text-muted text-truncate" style="max-width: 250px;"><%= t.getDescription() != null ? t.getDescription() : "" %></div>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border text-truncate" style="max-width: 180px;"><%= t.getProjectName() %></span>
                                </td>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="rounded-circle bg-light border text-secondary fw-semibold d-flex align-items-center justify-content-center me-2"
                                             style="width: 28px; height: 28px; font-size: 0.75rem;">
                                            <%= t.getAssignedToName() != null && !t.getAssignedToName().isEmpty() ? t.getAssignedToName().substring(0, 1).toUpperCase() : "?" %>
                                        </div>
                                        <span class="small fw-medium text-dark"><%= t.getAssignedToName() != null ? t.getAssignedToName() : "Unassigned" %></span>
                                    </div>
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
                                    <div class="btn-group btn-group-sm">
                                        <button type="button" class="btn btn-outline-custom" title="Edit Task"
                                                data-bs-toggle="modal" data-bs-target="#editTaskModal"
                                                data-id="<%= t.getId() %>"
                                                data-title="<%= t.getTaskTitle() %>"
                                                data-desc="<%= t.getDescription() != null ? t.getDescription() : "" %>"
                                                data-project="<%= t.getProjectId() %>"
                                                data-assigned="<%= t.getAssignedTo() != null ? t.getAssignedTo() : "" %>"
                                                data-priority="<%= t.getPriority().name() %>"
                                                data-status="<%= t.getStatus().name() %>"
                                                data-start="<%= DateUtil.formatInputDate(t.getStartDate()) %>"
                                                data-due="<%= DateUtil.formatInputDate(t.getDueDate()) %>">
                                            <i class="bi bi-pencil-square text-secondary"></i>
                                        </button>
                                        <button type="button" class="btn btn-outline-custom" title="Delete Task"
                                                data-bs-toggle="modal" data-bs-target="#deleteConfirmModal"
                                                data-id="<%= t.getId() %>"
                                                data-name="<%= t.getTaskTitle() %>"
                                                data-action="<%= request.getContextPath() %>/manager/tasks">
                                            <i class="bi bi-trash3 text-danger"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="8" class="text-center py-5 text-muted">
                                    <i class="bi bi-check2-all fs-1 d-block mb-2 text-secondary"></i>
                                    No tasks found matching criteria.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /.content-body -->

<!-- =========================================================================
     MODAL: Create Task
     ========================================================================= -->
<div class="modal fade" id="createTaskModal" tabindex="-1" aria-labelledby="createTaskModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/manager/tasks" method="POST">
                <input type="hidden" name="action" value="create">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="createTaskModalLabel"><i class="bi bi-plus-circle text-primary me-2"></i>Create New Task</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label">Task Title</label>
                        <input type="text" class="form-control" name="title" placeholder="e.g. Integrate Stripe Webhooks" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="3" placeholder="Task details, specifications and deliverables..."></textarea>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Associated Project</label>
                            <select class="form-select" name="projectId" required>
                                <option value="" disabled selected>-- Select Project --</option>
                                <% if (projects != null) {
                                    for (Project p : projects) { %>
                                    <option value="<%= p.getId() %>"><%= p.getTitle() %></option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Assigned Team Member</label>
                            <select class="form-select" name="assignedTo">
                                <option value="">-- Unassigned --</option>
                                <% if (teamMembers != null) {
                                    for (User tm : teamMembers) { %>
                                    <option value="<%= tm.getId() %>"><%= tm.getFullName() %> (<%= tm.getEmail() %>)</option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Priority</label>
                            <select class="form-select" name="priority" required>
                                <option value="LOW">Low</option>
                                <option value="MEDIUM" selected>Medium</option>
                                <option value="HIGH">High</option>
                                <option value="CRITICAL">Critical</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Initial Status</label>
                            <select class="form-select" name="status" required>
                                <option value="TODO" selected>To Do</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="REVIEW">In Review</option>
                                <option value="COMPLETED">Completed</option>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Start Date</label>
                            <input type="date" class="form-control" name="startDate">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Due Date</label>
                            <input type="date" class="form-control" name="dueDate" required>
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-check-lg me-1"></i>Create Task</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- =========================================================================
     MODAL: Edit Task
     ========================================================================= -->
<div class="modal fade" id="editTaskModal" tabindex="-1" aria-labelledby="editTaskModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/manager/tasks" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editTaskId" name="taskId" value="">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="editTaskModalLabel"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Task</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label class="form-label">Task Title</label>
                        <input type="text" class="form-control" id="editTaskTitle" name="title" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" id="editTaskDesc" name="description" rows="3"></textarea>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Project</label>
                            <select class="form-select" id="editTaskProject" name="projectId" required>
                                <% if (projects != null) {
                                    for (Project p : projects) { %>
                                    <option value="<%= p.getId() %>"><%= p.getTitle() %></option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Assigned Member</label>
                            <select class="form-select" id="editTaskAssignee" name="assignedTo">
                                <option value="">-- Unassigned --</option>
                                <% if (teamMembers != null) {
                                    for (User tm : teamMembers) { %>
                                    <option value="<%= tm.getId() %>"><%= tm.getFullName() %> (<%= tm.getEmail() %>)</option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Priority</label>
                            <select class="form-select" id="editTaskPriority" name="priority" required>
                                <option value="LOW">Low</option>
                                <option value="MEDIUM">Medium</option>
                                <option value="HIGH">High</option>
                                <option value="CRITICAL">Critical</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Status</label>
                            <select class="form-select" id="editTaskStatus" name="status" required>
                                <option value="TODO">To Do</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="REVIEW">In Review</option>
                                <option value="COMPLETED">Completed</option>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="editTaskStartDate" name="startDate">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Due Date</label>
                            <input type="date" class="form-control" id="editTaskDueDate" name="dueDate" required>
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save Task</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- =========================================================================
     MODAL: Generic Delete Confirmation
     ========================================================================= -->
<div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content border-0 shadow">
            <form id="deleteForm" action="<%= request.getContextPath() %>/manager/tasks" method="POST">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="taskId" value="">
                <div class="modal-body p-4 text-center">
                    <div class="text-danger mb-3">
                        <i class="bi bi-exclamation-triangle-fill fs-1"></i>
                    </div>
                    <h5 class="fw-bold mb-2">Delete Task</h5>
                    <p class="small text-muted mb-4">
                        Are you sure you want to delete <strong id="deleteItemName" class="text-dark">this task</strong>?
                    </p>
                    <div class="d-flex justify-content-center gap-2">
                        <button type="button" class="btn btn-sm btn-outline-secondary px-3" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-sm btn-danger px-3"><i class="bi bi-trash3 me-1"></i>Delete</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
