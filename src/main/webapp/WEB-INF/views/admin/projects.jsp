<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    request.setAttribute("pageTitle", "Project Governance");
    request.setAttribute("pageSubtitle", "Create projects, track milestone deliveries, and assign project managers");
    request.setAttribute("activeMenu", "admin-projects");

    @SuppressWarnings("unchecked")
    List<Project> projectList = (List<Project>) request.getAttribute("projects");
    @SuppressWarnings("unchecked")
    List<User> managers = (List<User>) request.getAttribute("managers");
    @SuppressWarnings("unchecked")
    List<User> teamMembers = (List<User>) request.getAttribute("teamMembers");

    String searchQuery = (String) request.getAttribute("searchQuery");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <!-- Top Actions Bar: Search, Status Filter & Create Project Button -->
        <div class="protrack-card mb-4">
            <div class="protrack-card-body p-3">
                <form action="<%= request.getContextPath() %>/admin/projects" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-5">
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-search"></i></span>
                            <input type="text" class="form-control table-search-input" data-target="projectsTable"
                                   name="q" value="<%= searchQuery != null ? ValidationUtil.escapeXml(searchQuery) : "" %>"
                                   placeholder="Search project title or description...">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" name="status" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equals(selectedStatus) ? "selected" : "" %>>All Project Statuses</option>
                            <option value="PLANNING" <%= "PLANNING".equals(selectedStatus) ? "selected" : "" %>>Planning</option>
                            <option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(selectedStatus) ? "selected" : "" %>>In Progress</option>
                            <option value="ON_HOLD" <%= "ON_HOLD".equals(selectedStatus) ? "selected" : "" %>>On Hold</option>
                            <option value="COMPLETED" <%= "COMPLETED".equals(selectedStatus) ? "selected" : "" %>>Completed</option>
                            <option value="CANCELLED" <%= "CANCELLED".equals(selectedStatus) ? "selected" : "" %>>Cancelled</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <a href="<%= request.getContextPath() %>/admin/projects" class="btn btn-outline-custom w-100">
                            <i class="bi bi-arrow-counterclockwise me-1"></i> Reset
                        </a>
                    </div>
                    <div class="col-md-2 text-md-end">
                        <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#createProjectModal">
                            <i class="bi bi-folder-plus me-1"></i> New Project
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Projects Table Card -->
        <div class="protrack-card">
            <div class="protrack-card-header">
                <h5 class="protrack-card-title">
                    <i class="bi bi-kanban"></i> All Managed Projects (<%= projectList != null ? projectList.size() : 0 %>)
                </h5>
                <span class="small text-muted">Real-time status & completion analytics</span>
            </div>
            <div class="table-responsive">
                <table class="table table-modern" id="projectsTable">
                    <thead>
                        <tr>
                            <th style="width: 70px;">ID</th>
                            <th>Project Title</th>
                            <th>Project Manager</th>
                            <th>Timeline</th>
                            <th>Status</th>
                            <th>Completion</th>
                            <th class="text-end" style="width: 140px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (projectList != null && !projectList.isEmpty()) {
                            for (Project p : projectList) { %>
                            <tr>
                                <td class="text-muted fw-semibold">#<%= p.getId() %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/admin/projects?action=view&id=<%= p.getId() %>" class="fw-semibold text-dark text-decoration-none">
                                        <%= ValidationUtil.escapeXml(p.getTitle()) %>
                                    </a>
                                    <div class="small text-muted text-truncate" style="max-width: 280px;"><%= p.getDescription() != null ? ValidationUtil.escapeXml(p.getDescription()) : "No description" %></div>
                                </td>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="rounded-circle bg-light border text-secondary fw-semibold d-flex align-items-center justify-content-center me-2"
                                             style="width: 30px; height: 30px; font-size: 0.8rem;">
                                            <%= p.getManagerName() != null && !p.getManagerName().isEmpty() ? ValidationUtil.escapeXml(p.getManagerName().substring(0, 1).toUpperCase()) : "M" %>
                                        </div>
                                        <div>
                                            <span class="fw-medium text-dark"><%= p.getManagerName() != null ? ValidationUtil.escapeXml(p.getManagerName()) : "Unassigned" %></span>
                                            <div class="small text-muted"><%= p.getManagerEmail() != null ? ValidationUtil.escapeXml(p.getManagerEmail()) : "" %></div>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <span class="small text-dark fw-medium"><%= DateUtil.formatDisplayDate(p.getStartDate()) %></span>
                                    <div class="small text-muted">to <%= DateUtil.formatDisplayDate(p.getEndDate()) %></div>
                                </td>
                                <td>
                                    <span class="badge badge-custom <%= p.getStatusBadgeClass() %>">
                                        <%= p.getStatus() %>
                                    </span>
                                </td>
                                <td style="min-width: 130px;">
                                    <div class="d-flex align-items-center gap-2">
                                        <div class="progress progress-custom flex-grow-1">
                                            <div class="progress-bar" style="width: <%= p.getProgressPercentage() %>%;"></div>
                                        </div>
                                        <span class="small fw-semibold text-secondary"><%= p.getProgressPercentage() %>%</span>
                                    </div>
                                    <div class="small text-muted"><%= p.getCompletedTasks() %> / <%= p.getTotalTasks() %> tasks</div>
                                </td>
                                <td class="text-end">
                                    <div class="btn-group btn-group-sm">
                                        <a href="<%= request.getContextPath() %>/admin/projects?action=view&id=<%= p.getId() %>" class="btn btn-outline-custom" title="View Project Workspace">
                                            <i class="bi bi-eye text-primary"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-custom" title="Edit Project"
                                                data-bs-toggle="modal" data-bs-target="#editProjectModal"
                                                data-id="<%= p.getId() %>"
                                                data-title="<%= ValidationUtil.escapeXml(p.getTitle()) %>"
                                                data-desc="<%= p.getDescription() != null ? ValidationUtil.escapeXml(p.getDescription()) : "" %>"
                                                data-start="<%= DateUtil.formatInputDate(p.getStartDate()) %>"
                                                data-end="<%= DateUtil.formatInputDate(p.getEndDate()) %>"
                                                data-manager="<%= p.getManagerId() %>"
                                                data-status="<%= p.getStatus() %>">
                                            <i class="bi bi-pencil-square text-secondary"></i>
                                        </button>
                                        <button type="button" class="btn btn-outline-custom" title="Delete Project"
                                                data-bs-toggle="modal" data-bs-target="#deleteConfirmModal"
                                                data-id="<%= p.getId() %>"
                                                data-name="<%= ValidationUtil.escapeXml(p.getTitle()) %>"
                                                data-action="<%= request.getContextPath() %>/admin/projects">
                                            <i class="bi bi-trash3 text-danger"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="7" class="text-center py-5 text-muted">
                                    <i class="bi bi-folder-x fs-1 d-block mb-2 text-secondary"></i>
                                    No projects found matching the filter.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /.content-body -->

<!-- =========================================================================
     MODAL: Create Project
     ========================================================================= -->
<div class="modal fade" id="createProjectModal" tabindex="-1" aria-labelledby="createProjectModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/admin/projects" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="create">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="createProjectModalLabel"><i class="bi bi-folder-plus text-primary me-2"></i>Create New Project</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="createTitle" class="form-label">Project Title</label>
                        <input type="text" class="form-control" id="createTitle" name="title" placeholder="e.g. Next-Gen Mobile Banking" required>
                    </div>
                    <div class="mb-3">
                        <label for="createDesc" class="form-label">Project Description</label>
                        <textarea class="form-control" id="createDesc" name="description" rows="3" placeholder="Outline the primary deliverables and milestones..."></textarea>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="createManager" class="form-label">Assigned Project Manager</label>
                            <select class="form-select" id="createManager" name="managerId" required>
                                <option value="" disabled selected>-- Select Manager --</option>
                                <% if (managers != null) {
                                    for (User m : managers) { %>
                                    <option value="<%= m.getId() %>"><%= ValidationUtil.escapeXml(m.getFullName()) %> (<%= ValidationUtil.escapeXml(m.getEmail()) %>)</option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="createStatus" class="form-label">Initial Status</label>
                            <select class="form-select" id="createStatus" name="status" required>
                                <option value="PLANNING" selected>Planning</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="ON_HOLD">On Hold</option>
                                <option value="COMPLETED">Completed</option>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="createStartDate" class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="createStartDate" name="startDate" required>
                        </div>
                        <div class="col-md-6">
                            <label for="createEndDate" class="form-label">Target Completion Date</label>
                            <input type="date" class="form-control" id="createEndDate" name="endDate" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Initial Team Members (Hold Ctrl / Cmd to multi-select)</label>
                        <select class="form-select" name="memberIds" multiple size="4">
                            <% if (teamMembers != null) {
                                for (User tm : teamMembers) { %>
                                <option value="<%= tm.getId() %>"><%= ValidationUtil.escapeXml(tm.getFullName()) %> &lt;<%= ValidationUtil.escapeXml(tm.getEmail()) %>&gt;</option>
                            <%  }
                               } %>
                        </select>
                        <div class="form-text small text-muted">You can also assign additional members after project creation.</div>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-check-lg me-1"></i>Create Project</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- =========================================================================
     MODAL: Edit Project
     ========================================================================= -->
<div class="modal fade" id="editProjectModal" tabindex="-1" aria-labelledby="editProjectModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/admin/projects" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editProjectId" name="projectId" value="">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="editProjectModalLabel"><i class="bi bi-pencil-square text-primary me-2"></i>Edit Project</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="editProjectTitle" class="form-label">Project Title</label>
                        <input type="text" class="form-control" id="editProjectTitle" name="title" required>
                    </div>
                    <div class="mb-3">
                        <label for="editProjectDesc" class="form-label">Project Description</label>
                        <textarea class="form-control" id="editProjectDesc" name="description" rows="3"></textarea>
                    </div>
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="editProjectManager" class="form-label">Assigned Project Manager</label>
                            <select class="form-select" id="editProjectManager" name="managerId" required>
                                <% if (managers != null) {
                                    for (User m : managers) { %>
                                    <option value="<%= m.getId() %>"><%= ValidationUtil.escapeXml(m.getFullName()) %> (<%= ValidationUtil.escapeXml(m.getEmail()) %>)</option>
                                <%  }
                                   } %>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="editProjectStatus" class="form-label">Project Status</label>
                            <select class="form-select" id="editProjectStatus" name="status" required>
                                <option value="PLANNING">Planning</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="ON_HOLD">On Hold</option>
                                <option value="COMPLETED">Completed</option>
                                <option value="CANCELLED">Cancelled</option>
                            </select>
                        </div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="editProjectStartDate" class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="editProjectStartDate" name="startDate" required>
                        </div>
                        <div class="col-md-6">
                            <label for="editProjectEndDate" class="form-label">Target Completion Date</label>
                            <input type="date" class="form-control" id="editProjectEndDate" name="endDate" required>
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i>Save Changes</button>
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
            <form id="deleteForm" action="<%= request.getContextPath() %>/admin/projects" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="projectId" value="">
                <div class="modal-body p-4 text-center">
                    <div class="text-danger mb-3">
                        <i class="bi bi-exclamation-triangle-fill fs-1"></i>
                    </div>
                    <h5 class="fw-bold mb-2">Delete Project</h5>
                    <p class="small text-muted mb-4">
                        Are you sure you want to delete <strong id="deleteItemName" class="text-dark">this project</strong>? All associated tasks and member assignments will be deleted.
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
