<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.model.Role" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%
    request.setAttribute("pageTitle", "User Management");
    request.setAttribute("pageSubtitle", "Provision team credentials, configure system roles, and govern account permissions");
    request.setAttribute("activeMenu", "admin-users");

    @SuppressWarnings("unchecked")
    List<User> userList = (List<User>) request.getAttribute("users");
    String searchQuery = (String) request.getAttribute("searchQuery");
    String selectedRole = (String) request.getAttribute("selectedRole");
    Role[] allRoles = (Role[]) request.getAttribute("allRoles");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <!-- Top Actions Bar: Search, Role Filter, and Add User Button -->
        <div class="protrack-card mb-4">
            <div class="protrack-card-body p-3">
                <form action="<%= request.getContextPath() %>/admin/users" method="GET" class="row g-2 align-items-center">
                    <div class="col-md-5">
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-search"></i></span>
                            <input type="text" class="form-control table-search-input" data-target="usersTable"
                                   name="q" value="<%= searchQuery != null ? searchQuery : "" %>"
                                   placeholder="Search by name or email address...">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <select class="form-select" name="role" onchange="this.form.submit()">
                            <option value="ALL" <%= "ALL".equals(selectedRole) ? "selected" : "" %>>All System Roles</option>
                            <% if (allRoles != null) {
                                for (Role r : allRoles) { %>
                                <option value="<%= r.name() %>" <%= r.name().equals(selectedRole) ? "selected" : "" %>>
                                    <%= r.getDisplayName() %>
                                </option>
                            <%  }
                               } %>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <a href="<%= request.getContextPath() %>/admin/users" class="btn btn-outline-custom w-100">
                            <i class="bi bi-arrow-counterclockwise me-1"></i> Reset
                        </a>
                    </div>
                    <div class="col-md-2 text-md-end">
                        <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#addUserModal">
                            <i class="bi bi-person-plus-fill me-1"></i> Add User
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Registered Users Table Card -->
        <div class="protrack-card">
            <div class="protrack-card-header">
                <h5 class="protrack-card-title">
                    <i class="bi bi-people-fill"></i> System Accounts (<%= userList != null ? userList.size() : 0 %>)
                </h5>
                <span class="small text-muted">Instant filter active via search box</span>
            </div>
            <div class="table-responsive">
                <table class="table table-modern" id="usersTable">
                    <thead>
                        <tr>
                            <th style="width: 70px;">ID</th>
                            <th>Full Name</th>
                            <th>Email Address</th>
                            <th>Assigned Role</th>
                            <th>Status</th>
                            <th>Created Date</th>
                            <th class="text-end" style="width: 140px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (userList != null && !userList.isEmpty()) {
                            for (User u : userList) { %>
                            <tr>
                                <td class="text-muted fw-semibold">#<%= u.getId() %></td>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="rounded-circle bg-light text-primary border fw-bold d-flex align-items-center justify-content-center me-2"
                                             style="width: 34px; height: 34px; font-size: 0.85rem;">
                                            <%= u.getFullName() != null && !u.getFullName().isEmpty() ? u.getFullName().substring(0, 1).toUpperCase() : "U" %>
                                        </div>
                                        <span class="fw-semibold text-dark"><%= u.getFullName() %></span>
                                    </div>
                                </td>
                                <td>
                                    <span class="text-secondary"><%= u.getEmail() %></span>
                                </td>
                                <td>
                                    <span class="badge badge-custom badge-role-<%= u.getRole().name() %>">
                                        <%= u.getRole().getDisplayName() %>
                                    </span>
                                </td>
                                <td>
                                    <% if (u.isActive()) { %>
                                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">Active</span>
                                    <% } else { %>
                                        <span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1">Inactive</span>
                                    <% } %>
                                </td>
                                <td>
                                    <span class="small text-muted"><%= DateUtil.formatDisplayDateTime(u.getCreatedAt()) %></span>
                                </td>
                                <td class="text-end">
                                    <div class="btn-group btn-group-sm">
                                        <button type="button" class="btn btn-outline-custom" title="Edit User"
                                                data-bs-toggle="modal" data-bs-target="#editUserModal"
                                                data-id="<%= u.getId() %>"
                                                data-name="<%= u.getFullName() %>"
                                                data-email="<%= u.getEmail() %>"
                                                data-role="<%= u.getRole().name() %>"
                                                data-status="<%= u.getStatus() %>">
                                            <i class="bi bi-pencil-square text-primary"></i>
                                        </button>
                                        <button type="button" class="btn btn-outline-custom" title="Delete User"
                                                data-bs-toggle="modal" data-bs-target="#deleteConfirmModal"
                                                data-id="<%= u.getId() %>"
                                                data-name="<%= u.getFullName() %> (<%= u.getEmail() %>)"
                                                data-action="<%= request.getContextPath() %>/admin/users">
                                            <i class="bi bi-trash3 text-danger"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        <%  }
                           } else { %>
                            <tr>
                                <td colspan="7" class="text-center py-5 text-muted">
                                    <i class="bi bi-person-x fs-1 d-block mb-2 text-secondary"></i>
                                    No user accounts matched your search criteria.
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>

    </div><!-- /.content-body -->

<!-- =========================================================================
     MODAL: Add New User
     ========================================================================= -->
<div class="modal fade" id="addUserModal" tabindex="-1" aria-labelledby="addUserModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/admin/users" method="POST">
                <input type="hidden" name="action" value="create">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="addUserModalLabel"><i class="bi bi-person-plus text-primary me-2"></i>Add New User</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="addFullName" class="form-label">Full Name</label>
                        <input type="text" class="form-control" id="addFullName" name="fullName" placeholder="e.g. Rachel Adams" required>
                    </div>
                    <div class="mb-3">
                        <label for="addEmail" class="form-label">Email Address</label>
                        <input type="email" class="form-control" id="addEmail" name="email" placeholder="rachel@protrack.com" required>
                    </div>
                    <div class="mb-3">
                        <label for="addPassword" class="form-label">Temporary Password</label>
                        <input type="password" class="form-control" id="addPassword" name="password" placeholder="Min 6 characters" minlength="6" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-md-6">
                            <label for="addRole" class="form-label">System Role</label>
                            <select class="form-select" id="addRole" name="role" required>
                                <option value="TEAM_MEMBER" selected>Team Member</option>
                                <option value="PROJECT_MANAGER">Project Manager</option>
                                <option value="ADMIN">Administrator</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="addStatus" class="form-label">Account Status</label>
                            <select class="form-select" id="addStatus" name="status" required>
                                <option value="ACTIVE" selected>Active</option>
                                <option value="INACTIVE">Inactive</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-check-lg me-1"></i>Create User</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- =========================================================================
     MODAL: Edit User
     ========================================================================= -->
<div class="modal fade" id="editUserModal" tabindex="-1" aria-labelledby="editUserModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="<%= request.getContextPath() %>/admin/users" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editUserId" name="userId" value="">
                <div class="modal-header bg-light">
                    <h5 class="modal-title fw-bold" id="editUserModalLabel"><i class="bi bi-pencil-square text-primary me-2"></i>Edit User Information</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="editUserName" class="form-label">Full Name</label>
                        <input type="text" class="form-control" id="editUserName" name="fullName" required>
                    </div>
                    <div class="mb-3">
                        <label for="editUserEmail" class="form-label">Email Address</label>
                        <input type="email" class="form-control" id="editUserEmail" name="email" required>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-md-6">
                            <label for="editUserRole" class="form-label">System Role</label>
                            <select class="form-select" id="editUserRole" name="role" required>
                                <option value="TEAM_MEMBER">Team Member</option>
                                <option value="PROJECT_MANAGER">Project Manager</option>
                                <option value="ADMIN">Administrator</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="editUserStatus" class="form-label">Account Status</label>
                            <select class="form-select" id="editUserStatus" name="status" required>
                                <option value="ACTIVE">Active</option>
                                <option value="INACTIVE">Inactive</option>
                            </select>
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
            <form id="deleteForm" action="<%= request.getContextPath() %>/admin/users" method="POST">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="userId" value="">
                <div class="modal-body p-4 text-center">
                    <div class="text-danger mb-3">
                        <i class="bi bi-exclamation-triangle-fill fs-1"></i>
                    </div>
                    <h5 class="fw-bold mb-2">Delete Confirmation</h5>
                    <p class="small text-muted mb-4">
                        Are you sure you want to delete <strong id="deleteItemName" class="text-dark">this item</strong>? This action cannot be undone.
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
