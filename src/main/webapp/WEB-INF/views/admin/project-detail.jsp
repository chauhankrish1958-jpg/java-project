<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.Task" %>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    Project project = (Project) request.getAttribute("project");
    @SuppressWarnings("unchecked")
    List<User> members = (List<User>) request.getAttribute("members");
    @SuppressWarnings("unchecked")
    List<Task> tasks = (List<Task>) request.getAttribute("tasks");
    @SuppressWarnings("unchecked")
    List<User> allTeamMembers = (List<User>) request.getAttribute("allTeamMembers");

    request.setAttribute("pageTitle", project != null ? ValidationUtil.escapeXml(project.getTitle()) : "Project Workspace");
    request.setAttribute("pageSubtitle", "Deep dive into project milestones, team allocations and task execution");
    request.setAttribute("activeMenu", "admin-projects");
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
                        <h2 class="fw-bold text-dark mb-2"><%= ValidationUtil.escapeXml(project.getTitle()) %></h2>
                        <p class="text-secondary mb-0"><%= project.getDescription() != null ? ValidationUtil.escapeXml(project.getDescription()) : "No detailed description provided." %></p>
                    </div>
                    <div class="d-flex gap-2">
                        <a href="<%= request.getContextPath() %>/admin/projects" class="btn btn-outline-custom">
                            <i class="bi bi-arrow-left me-1"></i> Back to Projects
                        </a>
                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#assignMemberModal">
                            <i class="bi bi-person-plus-fill me-1"></i> Assign Member
                        </button>
                    </div>
                </div>

                <hr class="my-4">

                <div class="row g-3 text-secondary small">
                    <div class="col-sm-6 col-lg-3">
                        <div class="text-muted mb-1"><i class="bi bi-person-badge text-primary me-1"></i> Project Manager</div>
                        <span class="fw-semibold text-dark fs-6"><%= project.getManagerName() != null ? ValidationUtil.escapeXml(project.getManagerName()) : "Unassigned" %></span>
                        <div class="text-muted"><%= project.getManagerEmail() != null ? ValidationUtil.escapeXml(project.getManagerEmail()) : "" %></div>
                    </div>
                    <div class="col-sm-6 col-lg-3">
                        <div class="text-muted mb-1"><i class="bi bi-calendar3 text-primary me-1"></i> Timeline</div>
                        <span class="fw-semibold text-dark"><%= DateUtil.formatDisplayDate(project.getStartDate()) %></span>
                        <div class="text-muted">to <%= DateUtil.formatDisplayDate(project.getEndDate()) %></div>
                    </div>
                    <div class="col-sm-6 col-lg-3">
                        <div class="text-muted mb-1"><i class="bi bi-list-check text-primary me-1"></i> Task Progress</div>
                        <div class="d-flex align-items-center gap-2 mt-1">
                            <div class="progress progress-custom flex-grow-1" style="height: 10px;">
                                <div class="progress-bar" style="width: <%= project.getProgressPercentage() %>%;"></div>
                            </div>
                            <span class="fw-bold text-dark"><%= project.getProgressPercentage() %>%</span>
                        </div>
                        <div class="text-muted"><%= project.getCompletedTasks() %> of <%= project.getTotalTasks() %> tasks completed</div>
                    </div>
                    <div class="col-sm-6 col-lg-3">
                        <div class="text-muted mb-1"><i class="bi bi-people text-primary me-1"></i> Allocated Team</div>
                        <span class="fw-semibold text-dark fs-6"><%= members != null ? members.size() : 0 %> active members</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- Left: Associated Tasks List -->
            <div class="col-lg-8">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-list-task"></i> Project Tasks (<%= tasks != null ? tasks.size() : 0 %>)
                        </h5>
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
                                            <div class="fw-semibold text-dark"><%= ValidationUtil.escapeXml(t.getTaskTitle()) %></div>
                                            <div class="small text-muted text-truncate" style="max-width: 250px;"><%= t.getDescription() != null ? ValidationUtil.escapeXml(t.getDescription()) : "" %></div>
                                        </td>
                                        <td>
                                            <span class="small fw-medium text-dark"><%= t.getAssignedToName() != null ? ValidationUtil.escapeXml(t.getAssignedToName()) : "Unassigned" %></span>
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
                                        <td colspan="5" class="text-center py-4 text-muted">No tasks currently created under this project.</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- Right: Assigned Team Members -->
            <div class="col-lg-4">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-people-fill"></i> Team Members (<%= members != null ? members.size() : 0 %>)
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
                                             <%= m.getFullName() != null && !m.getFullName().isEmpty() ? ValidationUtil.escapeXml(m.getFullName().substring(0, 1).toUpperCase()) : "U" %>
                                        </div>
                                        <div>
                                            <div class="fw-semibold text-dark"><%= ValidationUtil.escapeXml(m.getFullName()) %></div>
                                            <div class="small text-muted"><%= ValidationUtil.escapeXml(m.getEmail()) %></div>
                                        </div>
                                    </div>
                                    <form action="<%= request.getContextPath() %>/admin/projects" method="POST" onsubmit="return confirm('Remove <%= ValidationUtil.escapeXml(m.getFullName()) %> from this project?');">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
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
                                    No team members currently assigned.
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
                    <form action="<%= request.getContextPath() %>/admin/projects" method="POST">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="action" value="assignMember">
                        <input type="hidden" name="projectId" value="<%= project.getId() %>">
                        <div class="modal-header bg-light">
                            <h5 class="modal-title fw-bold"><i class="bi bi-person-plus text-primary me-2"></i>Assign Member</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body p-4">
                            <label class="form-label">Select Team Member</label>
                            <select class="form-select" name="userId" required>
                                <option value="" disabled selected>-- Choose member --</option>
                                <% if (allTeamMembers != null) {
                                    for (User u : allTeamMembers) { %>
                                    <option value="<%= u.getId() %>"><%= ValidationUtil.escapeXml(u.getFullName()) %> (<%= ValidationUtil.escapeXml(u.getEmail()) %>)</option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Assign Member</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <% } else { %>
            <div class="alert alert-danger">Project not found or invalid project ID specified.</div>
        <% } %>

    </div><!-- /.content-body -->

<jsp:include page="../common/footer.jsp" />
