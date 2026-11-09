<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.protrack.model.User" %>
<%
    User navUser = (User) session.getAttribute("currentUser");
    String pageHeading = (String) request.getAttribute("pageTitle");
    if (pageHeading == null) pageHeading = "Dashboard";
    String pageSubheading = (String) request.getAttribute("pageSubtitle");
    if (pageSubheading == null) pageSubheading = "Manage and monitor your projects efficiently";
    String notifBanner = (String) request.getAttribute("notificationBanner");
    String navCp = request.getContextPath();
%>
<header class="top-navbar">
    <div class="d-flex align-items-center">
        <button type="button" class="btn btn-sm btn-outline-secondary d-lg-none me-3" id="sidebarToggle" aria-label="Toggle Navigation">
            <i class="bi bi-list fs-5"></i>
        </button>
        <div>
            <h1 class="page-title"><%= com.protrack.util.ValidationUtil.escapeXml(pageHeading) %></h1>
            <p class="page-subtitle"><%= com.protrack.util.ValidationUtil.escapeXml(pageSubheading) %></p>
        </div>
    </div>

    <!-- Right Header Actions -->
    <div class="d-flex align-items-center gap-3">
        <% if (notifBanner != null && !notifBanner.trim().isEmpty()) { %>
        <div class="d-none d-xl-flex align-items-center bg-light px-3 py-1 rounded-pill border">
            <i class="bi bi-megaphone-fill text-primary me-2"></i>
            <span class="small text-secondary fw-medium"><%= com.protrack.util.ValidationUtil.escapeXml(notifBanner) %></span>
        </div>
        <% } %>

        <!-- User Role Pill -->
        <% if (navUser != null) { %>
        <span class="badge badge-custom badge-role-<%= navUser.getRole().name() %> d-none d-sm-inline-flex">
            <i class="bi bi-shield-check"></i> <%= navUser.getRole().getDisplayName() %>
        </span>

        <!-- User Profile Dropdown -->
        <div class="dropdown">
            <button class="btn btn-link text-decoration-none p-0 d-flex align-items-center dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                <div class="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold" style="width: 38px; height: 38px; font-size: 0.9rem;">
                    <%= navUser.getFullName() != null && !navUser.getFullName().isEmpty() ? com.protrack.util.ValidationUtil.escapeXml(navUser.getFullName().substring(0, 1).toUpperCase()) : "U" %>
                </div>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow-sm border-light">
                <li class="px-3 py-2 border-bottom">
                    <div class="fw-semibold text-dark"><%= com.protrack.util.ValidationUtil.escapeXml(navUser.getFullName()) %></div>
                    <div class="small text-muted"><%= com.protrack.util.ValidationUtil.escapeXml(navUser.getEmail()) %></div>
                </li>
                <li><a class="dropdown-item py-2" href="<%= navCp %>/profile"><i class="bi bi-person me-2 text-primary"></i> My Profile</a></li>
                <li><a class="dropdown-item py-2" href="<%= navCp %>/activity"><i class="bi bi-clock-history me-2 text-primary"></i> Activity Log</a></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item py-2 text-danger" href="<%= navCp %>/logout"><i class="bi bi-box-arrow-right me-2 text-danger"></i> Logout</a></li>
            </ul>
        </div>
        <% } %>
    </div>
</header>
