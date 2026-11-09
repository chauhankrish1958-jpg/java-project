<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="com.protrack.model.User" %>
<%
    if (exception != null) {
        application.log("HTTP 500 Internal Server Error encountered: " + exception.getMessage(), exception);
    }
    User currentUser = (User) session.getAttribute("currentUser");
    String cp = request.getContextPath();
    String returnUrl = currentUser != null ? cp + currentUser.getDashboardUrl() : cp + "/login";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 Internal Error | ProTrack</title>
    <!-- Google Fonts: Inter -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom ProTrack SaaS Stylesheet -->
    <link rel="stylesheet" href="<%= cp %>/assets/css/style.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center" style="min-height: 100vh;">

<div class="card border-0 shadow-lg text-center p-5" style="max-width: 540px; border-radius: 16px;">
    <div class="rounded-circle bg-danger bg-opacity-10 text-danger d-inline-flex align-items-center justify-content-center mx-auto mb-3"
         style="width: 80px; height: 80px; font-size: 2.5rem;">
        <i class="bi bi-cpu"></i>
    </div>
    <h1 class="display-5 fw-bold text-dark mb-1">500</h1>
    <h4 class="fw-bold text-danger mb-3">Internal System Error</h4>
    <p class="text-secondary mb-4">
        An unexpected internal server error occurred while processing your request. The system administrator has been notified. Please try again later.
    </p>
    <div>
        <a href="<%= returnUrl %>" class="btn btn-primary px-4 py-2 fw-medium">
            <i class="bi bi-arrow-clockwise me-1"></i> Return to Safety
        </a>
    </div>
</div>

</body>
</html>
