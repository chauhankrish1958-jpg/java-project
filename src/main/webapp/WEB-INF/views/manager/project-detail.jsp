<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.Task" %>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%
    Project project = (Project) request.getAttribute("project");
    @SuppressWarnings("unchecked")
    List<User> members = (List<User>) request.getAttribute("members");
    @SuppressWarnings("unchecked")
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
    @SuppressWarnings("unchecked")
    List<User> allTeamMembers = (List<User>) request.getAttribute("allTeamMembers");

    request.setAttribute("pageTitle", project != null ? project.getTitle() : "Project Workspace");
    request.setAttribute("pageSubtitle", "Manage allocated team members and track sprint task execution");
    request.setAttribute("activeMenu", "manager-projects");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <% if (project != null) { %>
        <!-- Project Hero Card -->
        <div class="protrack-card mb-4">
            <div class="protrack-card-body p-4">
                <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-start gap-3 mb-3">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-2">
                            <span class="badge badge-custom <%= project.getStatusBadgeClass() %> fs-6">
                                <%= project.getStatus() %>
                            </span>
                            <span class="text-muted small">Project ID: #<%= project.getId() %></span>
                        </div>
                        <h2 class="fw-bold text-dark mb-2"><%= project.getTitle() %></h2>
                        <p class="text-secondary mb-0"><%= project.getDescription() != null ? project.getDescription() : "No detailed description." %></p>
                    </div>
                    <div class="d-flex gap-2">
                        <a href="<%= request.getContextPath() %>/manager/projects" class="btn btn-outline-custom">
                            <i class="bi bi-arrow-left me-1"></i> Back
                        </a>
                        <button type="button" class="btn btn-outline-custom" data-bs-toggle="modal" data-bs-target="#assignMemberModal">
                            <i class="bi bi-person-plus-fill me-1"></i> Add Member
                        </button>
                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createTaskModal">
                            <i class="bi bi-plus-circle-fill me-1"></i> New Task
                        </button>
                    </div>
                </div>

                <hr class="my-4">

                <div class="row g-3 text-secondary small">
                    <div class="col-sm-6 col-lg-4">
                        <div class="text-muted mb-1"><i class="bi bi-calendar3 text-primary me-1"></i> Timeline</div>
                        <span class="fw-semibold text-dark"><%= DateUtil.formatDisplayDate(project.getStartDate()) %></span>
                        <span class="text-muted">to <%= DateUtil.formatDisplayDate(project.getEndDate()) %></span>
                    </div>
                    <div class="col-sm-6 col-lg-4">
                        <div class="text-muted mb-1"><i class="bi bi-list-check text-primary me-1"></i> Completion Rate</div>
                        <div class="d-flex align-items-center gap-2 mt-1">
                            <div class="progress progress-custom flex-grow-1" style="height: 10px;">
                                <div class="progress-bar" style="width: <%= project.getProgressPercentage() %>%;"></div>
                            </div>
                            <span class="fw-bold text-dark"><%= project.getProgressPercentage() %>%</span>
                        </div>
                        <div class="text-muted"><%= project.getCompletedTasks() %> of <%= project.getTotalTasks() %> tasks completed</div>
                    </div>
                    <div class="col-sm-6 col-lg-4">
                        <div class="text-muted mb-1"><i class="bi bi-people text-primary me-1"></i> Assigned Team</div>
                        <span class="fw-semibold text-dark fs-6"><%= members != null ? members.size() : 0 %> members assigned</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- Left: Tasks Under This Project -->
            <div class="col-lg-8">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-list-task"></i> Project Tasks (<%= tasks != null ? tasks.size() : 0 %>)
                        </h5>
                        <button type="button" class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#createTaskModal">
                            <i class="bi bi-plus-lg me-1"></i> Create Task
                        </button>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-modern">
                            <thead>
                                <tr>
                                    <th>Task Title</th>
                                    <th>Assignee</th>
                                    <th>Priority</th>
                                    <th>Status</th>
                                    <th>Due Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (tasks != null && !tasks.isEmpty()) {
                                    for (Task t : tasks) { %>
                                    <tr>
                                        <td>
                                            <div class="fw-semibold text-dark"><%= t.getTaskTitle() %></div>
                                            <div class="small text-muted text-truncate" style="max-width: 250px;"><%= t.getDescription() != null ? t.getDescription() : "" %></div>
                                        </td>
                                        <td>
                                            <span class="small fw-medium"><%= t.getAssignedToName() != null ? t.getAssignedToName() : "Unassigned" %></span>
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
                                        <td colspan="5" class="text-center py-4 text-muted">No tasks currently created for this project.</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Right: Team Members -->
            <div class="col-lg-4">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-people-fill"></i> Assigned Team (<%= members != null ? members.size() : 0 %>)
                        </h5>
                        <button type="button" class="btn btn-sm btn-outline-custom" data-bs-toggle="modal" data-bs-target="#assignMemberModal">
                            <i class="bi bi-plus-lg"></i>
                        </button>
                    </div>
                    <div class="protrack-card-body p-0">
                        <ul class="list-group list-group-flush">
                            <% if (members != null && !members.isEmpty()) {
                                for (User m : members) { %>
                                <li class="list-group-item d-flex justify-content-between align-items-center py-3 px-4">
                                    <div class="d-flex align-items-center">
                                        <div class="rounded-circle bg-light text-primary border fw-bold d-flex align-items-center justify-content-center me-3"
                                             style="width: 36px; height: 36px; font-size: 0.85rem;">
                                            <%= m.getFullName() != null && !m.getFullName().isEmpty() ? m.getFullName().substring(0, 1).toUpperCase() : "U" %>
                                        </div>
                                        <div>
                                            <div class="fw-semibold text-dark"><%= m.getFullName() %></div>
                                            <div class="small text-muted"><%= m.getEmail() %></div>
                                        </div>
                                    </div>
                                    <form action="<%= request.getContextPath() %>/manager/projects" method="POST" onsubmit="return confirm('Remove <%= m.getFullName() %> from this project?');">
                                        <input type="hidden" name="action" value="removeMember">
                                        <input type="hidden" name="projectId" value="<%= project.getId() %>">
                                        <input type="hidden" name="userId" value="<%= m.getId() %>">
                                        <button type="submit" class="btn btn-sm btn-link text-danger p-0" title="Remove Member">
                                            <i class="bi bi-x-circle fs-5"></i>
                                        </button>
                                    </form>
                                </li>
                            <%  }
                               } else { %>
                                <li class="list-group-item text-center py-4 text-muted">
                                    No team members assigned yet.
                                </li>
                            <% } %>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal: Assign Member to Project -->
        <div class="modal fade" id="assignMemberModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    <form action="<%= request.getContextPath() %>/manager/projects" method="POST">
                        <input type="hidden" name="action" value="assignMember">
                        <input type="hidden" name="projectId" value="<%= project.getId() %>">
                        <div class="modal-header bg-light">
                            <h5 class="modal-title fw-bold"><i class="bi bi-person-plus text-primary me-2"></i>Assign Team Member</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body p-4">
                            <label class="form-label">Choose Team Member</label>
                            <select class="form-select" name="userId" required>
                                <option value="" disabled selected>-- Select Member --</option>
                                <% if (allTeamMembers != null) {
                                    for (User u : allTeamMembers) { %>
                                    <option value="<%= u.getId() %>"><%= u.getFullName() %> (<%= u.getEmail() %>)</option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Assign to Project</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal: Create Task under this project -->
        <div class="modal fade" id="createTaskModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content border-0 shadow">
                    <form action="<%= request.getContextPath() %>/manager/tasks" method="POST">
                        <input type="hidden" name="action" value="create">
                        <input type="hidden" name="projectId" value="<%= project.getId() %>">
                        <div class="modal-header bg-light">
                            <h5 class="modal-title fw-bold"><i class="bi bi-plus-circle text-primary me-2"></i>Create Task for "<%= project.getTitle() %>"</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body p-4">
                            <div class="mb-3">
                                <label class="form-label">Task Title</label>
                                <input type="text" class="form-control" name="title" placeholder="e.g. Implement OAuth login" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Description</label>
                                <textarea class="form-control" name="description" rows="3" placeholder="Task acceptance criteria and details..."></textarea>
                            </div>
                            <div class="row g-3 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label">Assignee</label>
                                    <select class="form-select" name="assignedTo">
                                        <option value="">-- Unassigned --</option>
                                        <% if (members != null) {
                                            for (User m : members) { %>
                                            <option value="<%= m.getId() %>"><%= m.getFullName() %> (<%= m.getEmail() %>)</option>
                                        <%  }
                                           } %>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Priority</label>
                                    <select class="form-select" name="priority" required>
                                        <option value="LOW">Low</option>
                                        <option value="MEDIUM" selected>Medium</option>
                                        <option value="HIGH">High</option>
                                        <option value="CRITICAL">Critical</option>
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
                            <button type="submit" class="btn btn-primary">Create Task</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <% } else { %>
            <div class="alert alert-danger">Project not found or invalid ID.</div>
        <% } %>

    </div><!-- /.content-body -->

<jsp:include page="../common/footer.jsp" />
