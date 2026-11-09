<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register | ProTrack Online Project Management</title>
    <!-- Google Fonts: Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom ProTrack SaaS Stylesheet -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body class="auth-page">

<div class="auth-card row g-0" style="max-width: 800px;">
    <!-- Form Container -->
    <div class="col-12 auth-form-container p-4 p-md-5">
        <div class="text-center mb-4">
            <div class="d-inline-flex align-items-center mb-2">
                <div class="brand-icon me-2" style="width: 38px; height: 38px; background: linear-gradient(135deg, #6366f1, #8b5cf6); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.25rem; color: #fff;">
                    <i class="bi bi-kanban-fill"></i>
                </div>
                <span class="fs-4 fw-bold text-dark">ProTrack</span>
            </div>
            <h3 class="fw-bold text-dark mb-1">Create an Account</h3>
            <p class="text-muted small">Join your team and track project milestones collaboratively</p>
        </div>

        <%
            String reqError = (String) request.getAttribute("errorMessage");
        %>
        <% if (reqError != null && !reqError.trim().isEmpty()) { %>
        <div class="alert alert-danger alert-dismissible fade show small d-flex align-items-center mb-3" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
            <div><%= com.protrack.util.ValidationUtil.sanitize(reqError) %></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/register" method="POST" class="needs-validation">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="regFullName" class="form-label">Full Name</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-muted"><i class="bi bi-person"></i></span>
                        <input type="text" class="form-control" id="regFullName" name="fullName"
                               value="<%= com.protrack.util.ValidationUtil.sanitize((String) request.getAttribute("fullName")) %>"
                               placeholder="e.g. John Doe" required autofocus>
                    </div>
                </div>

                <div class="col-md-6">
                    <label for="regEmail" class="form-label">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-muted"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" id="regEmail" name="email"
                               value="<%= com.protrack.util.ValidationUtil.sanitize((String) request.getAttribute("email")) %>"
                               placeholder="john@example.com" required>
                    </div>
                </div>

                <div class="col-12">
                    <div class="p-3 bg-light rounded-3 border small text-muted d-flex align-items-center">
                        <i class="bi bi-info-circle-fill text-primary me-2 fs-5"></i>
                        <span>New accounts are registered as <strong>Team Member</strong>. Project Manager and Administrator privileges can be assigned by system administrators.</span>
                    </div>
                </div>

                <div class="col-md-6">
                    <label for="regPassword" class="form-label">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-muted"><i class="bi bi-shield-lock"></i></span>
                        <input type="password" class="form-control" id="regPassword" name="password"
                               placeholder="At least 8 characters" minlength="8" required>
                    </div>
                </div>

                <div class="col-md-6">
                    <label for="regConfirmPassword" class="form-label">Confirm Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-muted"><i class="bi bi-shield-check"></i></span>
                        <input type="password" class="form-control" id="regConfirmPassword" name="confirmPassword"
                               placeholder="Re-enter password" minlength="8" required>
                    </div>
                </div>
            </div>

            <div class="mt-4">
                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">
                    <i class="bi bi-person-plus-fill me-1"></i> Register Account
                </button>
            </div>
        </form>

        <div class="text-center mt-4 pt-2 border-top">
            <span class="text-muted small">Already have an account?</span>
            <a href="<%= request.getContextPath() %>/login" class="small fw-semibold text-primary ms-1 text-decoration-none">
                Sign in here
            </a>
        </div>
    </div>
</div>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
