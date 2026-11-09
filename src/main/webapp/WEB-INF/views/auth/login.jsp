<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | ProTrack Online Project Management</title>
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

<div class="auth-card row g-0">
    <!-- Left Hero Feature Banner -->
    <div class="col-lg-5 auth-hero-banner d-none d-lg-flex">
        <div>
            <div class="d-flex align-items-center mb-4">
                <div class="brand-icon me-2" style="width: 42px; height: 42px; background: linear-gradient(135deg, #6366f1, #8b5cf6); border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 1.4rem; color: #fff;">
                    <i class="bi bi-kanban-fill"></i>
                </div>
                <span class="fs-3 fw-bold text-white tracking-tight">ProTrack</span>
            </div>
            <h2 class="fw-bold mb-3 text-white fs-3">Enterprise Project Command Center</h2>
            <p class="text-light opacity-75 mb-4">
                A robust, role-driven platform engineered with Java Servlets, JDBC, and MySQL for comprehensive project lifecycle governance.
            </p>

            <div class="auth-feature-item">
                <div class="auth-feature-icon"><i class="bi bi-shield-lock-fill"></i></div>
                <div>
                    <h6 class="fw-semibold text-white mb-1">Role-Based Access Control</h6>
                    <small class="text-light opacity-75">Dedicated dashboards for Admins, Project Managers & Team Members.</small>
                </div>
            </div>

            <div class="auth-feature-item">
                <div class="auth-feature-icon"><i class="bi bi-graph-up-arrow"></i></div>
                <div>
                    <h6 class="fw-semibold text-white mb-1">Live Progress Analytics</h6>
                    <small class="text-light opacity-75">Automated task completion metrics, deadline alerts, and progress bars.</small>
                </div>
            </div>

            <div class="auth-feature-item">
                <div class="auth-feature-icon"><i class="bi bi-clock-history"></i></div>
                <div>
                    <h6 class="fw-semibold text-white mb-1">Comprehensive Audit Logs</h6>
                    <small class="text-light opacity-75">Real-time activity tracking for status shifts, creations, and updates.</small>
                </div>
            </div>
        </div>

        <div class="pt-4 border-top border-secondary border-opacity-25 small text-light opacity-50">
            Academic B.Tech Project &bull; MVC Architecture &bull; Tomcat 10
        </div>
    </div>

    <!-- Right Login Form -->
    <div class="col-lg-7 auth-form-container">
        <div class="mb-4">
            <h3 class="fw-bold text-dark mb-1">Sign In to ProTrack</h3>
            <p class="text-muted small">Enter your credentials to access your project dashboard</p>
        </div>

        <!-- Flash & Error Alerts -->
        <%
            String flashSuccess = (String) session.getAttribute("flash_success");
            String flashError = (String) session.getAttribute("flash_error");
            String reqError = (String) request.getAttribute("errorMessage");
            if (flashSuccess != null) session.removeAttribute("flash_success");
            if (flashError != null) session.removeAttribute("flash_error");
            String displaySuccess = flashSuccess;
            String displayError = flashError != null ? flashError : reqError;
        %>
        <% if (displaySuccess != null) { %>
        <div class="alert alert-success alert-dismissible fade show small d-flex align-items-center mb-3" role="alert">
            <i class="bi bi-check-circle-fill me-2 fs-5"></i>
            <div><%= com.protrack.util.ValidationUtil.sanitize(displaySuccess) %></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>
        <% if (displayError != null) { %>
        <div class="alert alert-danger alert-dismissible fade show small d-flex align-items-center mb-3" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
            <div><%= com.protrack.util.ValidationUtil.sanitize(displayError) %></div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/login" method="POST" class="needs-validation" novalidate>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="mb-3">
                <label for="loginEmail" class="form-label">Email Address</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-envelope"></i></span>
                    <input type="email" class="form-control border-start-0 ps-0" id="loginEmail" name="email"
                           value="<%= com.protrack.util.ValidationUtil.sanitize((String) request.getAttribute("email")) %>"
                           placeholder="name@company.com" required autofocus>
                </div>
            </div>

            <div class="mb-4">
                <div class="d-flex justify-content-between align-items-center">
                    <label for="loginPassword" class="form-label">Password</label>
                </div>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-key"></i></span>
                    <input type="password" class="form-control border-start-0 ps-0" id="loginPassword" name="password"
                           placeholder="Enter your password" required>
                </div>
            </div>

            <button type="submit" class="btn btn-primary w-100 py-2 mb-3 fw-semibold">
                <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
            </button>
        </form>

        <!-- Viva Demonstration Demo Accounts -->
        <div class="mt-4 pt-3 border-top">
            <div class="small fw-semibold text-secondary mb-2 d-flex align-items-center">
                <i class="bi bi-lightning-charge-fill text-warning me-1"></i> Quick Demo Accounts (Click to Fill):
            </div>
            <div class="d-flex flex-wrap">
                <button type="button" class="demo-account-chip" onclick="fillCredentials('admin@protrack.com', 'admin123')">
                    <i class="bi bi-shield-fill-check text-purple me-1"></i> <strong>Admin</strong> (admin@protrack.com)
                </button>
                <button type="button" class="demo-account-chip" onclick="fillCredentials('sarah.pm@protrack.com', 'manager123')">
                    <i class="bi bi-briefcase-fill text-primary me-1"></i> <strong>PM</strong> (sarah.pm@protrack.com)
                </button>
                <button type="button" class="demo-account-chip" onclick="fillCredentials('alex.dev@protrack.com', 'member123')">
                    <i class="bi bi-person-badge-fill text-success me-1"></i> <strong>Member</strong> (alex.dev@protrack.com)
                </button>
            </div>
        </div>

        <div class="text-center mt-4">
            <span class="text-muted small">Don't have an account?</span>
            <a href="<%= request.getContextPath() %>/register" class="small fw-semibold text-primary ms-1 text-decoration-none">
                Create an account
            </a>
        </div>
    </div>
</div>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- ProTrack Main Script -->
<script src="<%= request.getContextPath() %>/assets/js/main.js"></script>
</body>
</html>
