<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.model.ActivityLog" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%
    User profileUser = (User) session.getAttribute("currentUser");
    request.setAttribute("pageTitle", "User Profile");
    request.setAttribute("pageSubtitle", "Manage personal account credentials and review account security");
    request.setAttribute("activeMenu", "profile");

    @SuppressWarnings("unchecked")
    List<ActivityLog> myActivities = (List<ActivityLog>) request.getAttribute("myActivities");
%>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="common/alerts.jsp" />

        <div class="row g-4">
            <!-- Left Column: User Profile Card & Information Form -->
            <div class="col-lg-6">
                <!-- Profile Overview Header -->
                <div class="protrack-card mb-4">
                    <div class="protrack-card-body p-4 text-center">
                        <div class="rounded-circle bg-primary text-white fw-bold d-inline-flex align-items-center justify-content-center mb-3"
                             style="width: 72px; height: 72px; font-size: 1.8rem; box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);">
                            <%= profileUser != null && !profileUser.getFullName().isEmpty() ? profileUser.getFullName().substring(0, 1).toUpperCase() : "U" %>
                        </div>
                        <h4 class="fw-bold text-dark mb-1"><%= profileUser != null ? profileUser.getFullName() : "" %></h4>
                        <div class="text-secondary small mb-2"><%= profileUser != null ? profileUser.getEmail() : "" %></div>
                        <span class="badge badge-custom badge-role-<%= profileUser != null ? profileUser.getRole().name() : "" %>">
                            <i class="bi bi-shield-check"></i> <%= profileUser != null ? profileUser.getRole().getDisplayName() : "" %>
                        </span>
                        <div class="text-muted small mt-3">
                            Account ID: #<%= profileUser != null ? profileUser.getId() : "" %> &bull; Status:
                            <span class="text-success fw-medium">Active</span>
                        </div>
                    </div>
                </div>

                <!-- Update Name & Email Form -->
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-person-gear"></i> Personal Details
                        </h5>
                    </div>
                    <div class="protrack-card-body p-4">
                        <form action="<%= request.getContextPath() %>/profile" method="POST">
                            <input type="hidden" name="action" value="updateProfile">
                            <div class="mb-3">
                                <label for="profName" class="form-label">Full Name</label>
                                <input type="text" class="form-control" id="profName" name="fullName"
                                       value="<%= profileUser != null ? profileUser.getFullName() : "" %>" required>
                            </div>
                            <div class="mb-3">
                                <label for="profEmail" class="form-label">Email Address</label>
                                <input type="email" class="form-control" id="profEmail" name="email"
                                       value="<%= profileUser != null ? profileUser.getEmail() : "" %>" required>
                            </div>
                            <div class="d-flex justify-content-end">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-save me-1"></i> Update Details
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Right Column: Security (Change Password) & Recent History -->
            <div class="col-lg-6">
                <!-- Change Password Form -->
                <div class="protrack-card mb-4">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-shield-lock"></i> Security & Password
                        </h5>
                    </div>
                    <div class="protrack-card-body p-4">
                        <form action="<%= request.getContextPath() %>/profile" method="POST">
                            <input type="hidden" name="action" value="changePassword">
                            <div class="mb-3">
                                <label for="currPass" class="form-label">Current Password</label>
                                <input type="password" class="form-control" id="currPass" name="currentPassword" required>
                            </div>
                            <div class="mb-3">
                                <label for="newPass" class="form-label">New Password</label>
                                <input type="password" class="form-control" id="newPass" name="newPassword" minlength="6" placeholder="Min 6 characters" required>
                            </div>
                            <div class="mb-4">
                                <label for="confPass" class="form-label">Confirm New Password</label>
                                <input type="password" class="form-control" id="confPass" name="confirmPassword" minlength="6" required>
                            </div>
                            <div class="d-flex justify-content-end">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-key me-1"></i> Update Password
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Recent Activity Timeline -->
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-clock-history"></i> My Recent Audit Trail
                        </h5>
                    </div>
                    <div class="protrack-card-body">
                        <% if (myActivities != null && !myActivities.isEmpty()) { %>
                        <div class="timeline">
                            <% for (ActivityLog act : myActivities) { %>
                            <div class="timeline-item">
                                <div class="timeline-marker"></div>
                                <div class="timeline-content">
                                    <div class="timeline-title"><%= act.getDetails() %></div>
                                    <div class="timeline-time">
                                        <i class="bi bi-clock me-1"></i><%= act.getFormattedCreatedAt() %>
                                    </div>
                                </div>
                            </div>
                            <% } %>
                        </div>
                        <% } else { %>
                        <p class="text-muted small text-center my-3">No activity logs recorded yet.</p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

    </div><!-- /.content-body -->

<jsp:include page="common/footer.jsp" />
