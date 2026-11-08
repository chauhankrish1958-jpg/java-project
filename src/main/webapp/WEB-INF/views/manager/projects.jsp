<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%
    request.setAttribute("pageTitle", "Project Overview");
    request.setAttribute("pageSubtitle", "Plan project lifecycles, monitor completion percentages, and manage timelines");
    request.setAttribute("activeMenu", "manager-projects");

    @SuppressWarnings("unchecked")
    List<Project> projectList = (List<Project>) request.getAttribute("projects");
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

        <!-- Search, Status Filter & Create Project Bar -->
        <div class="protrack-card mb-4">
            <div class="protrack-card-body p-3">
                <form action="<%= request.getContextPath() %>/manager/projects" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-6">
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-search"></i></span>
                            <input type="text" class="form-control table-search-input" data-target="mgrProjectsTable"
                                   name="q" value="<%= searchQuery != null ? searchQuery : "" %>"
                                   placeholder="Search project title or description...">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" name="status" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equals(selectedStatus) ? "selected" : "" %>>All Statuses</option>
                            <option value="PLANNING" <%= "PLANNING".equals(selectedStatus) ? "selected" : "" %>>Planning</option>
                            <option value="IN_PROGRESS" <%= "IN_PROGRESS".equals(selectedStatus) ? "selected" : "" %>>In Progress</option>
                            <option value="ON_HOLD" <%= "ON_HOLD".equals(selectedStatus) ? "selected" : "" %>>On Hold</option>
                            <option value="COMPLETED" <%= "COMPLETED".equals(selectedStatus) ? "selected" : "" %>>Completed</option>
                        </select>
                    </div>
                    <div class="col-md-1">
                        <a href="<%= request.getContextPath() %>/manager/projects" class="btn btn-outline-custom w-100" title="Reset Filters">
                            <i class="bi bi-arrow-counterclockwise"></i>
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

        <!-- Managed Projects Table -->
        <div class="protrack-card">
            <div class="protrack-card-header">
                <h5 class="protrack-card-title">
                    <i class="bi bi-folder2-open"></i> Projects Under Your Supervision (<%= projectList != null ? projectList.size() : 0 %>)
                </h5>
            </div>
            <div class="table-responsive">
                <table class="table table-modern" id="mgrProjectsTable">
                    <thead>
                        <tr>
                            <th style="width: 70px;">ID</th>
                            <th>Project Title</th>
                            <th>Start Date</th>
                            <th>Due Date</th>
                            <th>Status</th>
                            <th>Completion Rate</th>
                            <th class="text-end" style="width: 120px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (projectList != null && !projectList.isEmpty()) {
                            for (Project p : projectList) { %>
                            <tr>
                                <td class="text-muted fw-semibold">#<%= p.getId() %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/manager/projects?action=view&id=<%= p.getId() %>" class="fw-semibold text-dark text-decoration-none">
                                        <%= p.getTitle() %>
                                    </a>
                                    <div class="small text-muted text-truncate" style="max-width: 320px;"><%= p.getDescription() != null ? p.getDescription() : "" %></div>
                                </td>
                                <td>
                                    <span class="small fw-medium text-dark"><%= DateUtil.formatDisplayDate(p.getStartDate()) %></span>
                                </td>
                                <td>
                                    <span class="small fw-medium text-dark"><%= DateUtil.formatDisplayDate(p.getEndDate()) %></span>
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
                                    <div class="small text-muted"><%= p.getCompletedTasks() %> / <%= p.getTotalTasks() %> completed</div>
                                </td>
                                <td class="text-end">
                                    <div class="btn-group btn-group-sm">
                                        <a href="<%= request.getContextPath() %>/manager/projects?action=view&id=<%= p.getId() %>" class="btn btn-outline-custom" title="View Details">
                                            <i class="bi bi-eye text-primary"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-custom" title="Edit Project"
                                                data-bs-toggle="modal" data-bs-target="#editProjectModal"
                                                data-id="<%= p.getId() %>"
                                                data-title="<%= p.getTitle() %>"
                                                data-desc="<%= p.getDescription() != null ? p.getDescription() : "" %>"
                                                data-start="<%= DateUtil.formatInputDate(p.getStartDate()) %>"
                                                data-end="<%= DateUtil.formatInputDate(p.getEndDate()) %>"
                                                data-status="<%= p.getStatus() %>">
                                            <i class="bi bi-pencil-square text-secondary"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="7" class="text-center py-5 text-muted">
                                    <i class="bi bi-folder-x fs-1 d-block mb-2 text-secondary"></i>
                                    No managed projects found. Click "New Project" to create one.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /.content-body -->

<!-- =========================================================================
     MODAL: Create Project (PM)
     ========================================================================= -->
<div class="modal fade" id="createProjectModal" tabindex="-1" aria-labelledby="createProjectModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/manager/projects" method="POST">
                <input type="hidden" name="action" value="create">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="createProjectModalLabel"><i class="bi bi-folder-plus text-primary me-2"></i>Create New Project</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="createTitle" class="form-label">Project Title</label>
                        <input type="text" class="form-control" id="createTitle" name="title" placeholder="e.g. Mobile Banking App Redesign" required>
                    </div>
                    <div class="mb-3">
                        <label for="createDesc" class="form-label">Project Description</label>
                        <textarea class="form-control" id="createDesc" name="description" rows="3" placeholder="Define the scope and sprint targets..."></textarea>
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
                        <label for="createStatus" class="form-label">Status</label>
                        <select class="form-select" id="createStatus" name="status" required>
                            <option value="PLANNING" selected>Planning</option>
                            <option value="IN_PROGRESS">In Progress</option>
                            <option value="ON_HOLD">On Hold</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Assign Team Members (Hold Ctrl / Cmd to multi-select)</label>
                        <select class="form-select" name="memberIds" multiple size="4">
                            <% if (teamMembers != null) {
                                for (User tm : teamMembers) { %>
                                <option value="<%= tm.getId() %>"><%= tm.getFullName() %> &lt;<%= tm.getEmail() %>&gt;</option>
                            <%  }
                               } %>
                        </select>
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
     MODAL: Edit Project (PM)
     ========================================================================= -->
<div class="modal fade" id="editProjectModal" tabindex="-1" aria-labelledby="editProjectModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/manager/projects" method="POST">
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
                            <label for="editProjectStartDate" class="form-label">Start Date</label>
                            <input type="date" class="form-control" id="editProjectStartDate" name="startDate" required>
                        </div>
                        <div class="col-md-6">
                            <label for="editProjectEndDate" class="form-label">Target Completion Date</label>
                            <input type="date" class="form-control" id="editProjectEndDate" name="endDate" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="editProjectStatus" class="form-label">Project Status</label>
                        <select class="form-select" id="editProjectStatus" name="status" required>
                            <option value="PLANNING">Planning</option>
                            <option value="IN_PROGRESS">In Progress</option>
                            <option value="ON_HOLD">On Hold</option>
                            <option value="COMPLETED">Completed</option>
                        </select>
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

<jsp:include page="../common/footer.jsp" />
