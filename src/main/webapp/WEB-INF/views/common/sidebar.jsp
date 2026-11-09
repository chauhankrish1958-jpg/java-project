<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.protrack.model.User" %>
<%@ page import="com.protrack.model.Role" %>
<%
    User user = (User) session.getAttribute("currentUser");
    String activePage = (String) request.getAttribute("activeMenu");
    if (activePage == null) activePage = "";
    String cp = request.getContextPath();
%>
<aside class="sidebar">
    <!-- Brand Header -->
    <a href="<%= cp %><%= user != null ? user.getDashboardUrl() : "/login" %>" class="sidebar-brand">
        <div class="brand-icon">
            <i class="bi bi-kanban-fill"></i>
        </div>
        <span>ProTrack</span>
    </a>

    <!-- Navigation Menu Items -->
    <div class="sidebar-menu">
        <div class="sidebar-heading">Navigation</div>

        <% if (user != null && user.getRole() == Role.ADMIN) { %>
            <!-- Admin Navigation -->
            <a href="<%= cp %>/admin/dashboard" class="nav-link-custom <%= "admin-dashboard".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-speedometer2"></i>
                <span>Dashboard</span>
            </a>
            <a href="<%= cp %>/admin/users" class="nav-link-custom <%= "admin-users".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-people-fill"></i>
                <span>User Management</span>
            </a>
            <a href="<%= cp %>/admin/projects" class="nav-link-custom <%= "admin-projects".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-folder-fill"></i>
                <span>Project Management</span>
            </a>
            <a href="<%= cp %>/admin/settings" class="nav-link-custom <%= "admin-settings".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-gear-fill"></i>
                <span>System Settings</span>
            </a>
        <% } else if (user != null && user.getRole() == Role.PROJECT_MANAGER) { %>
            <!-- Project Manager Navigation -->
            <a href="<%= cp %>/manager/dashboard" class="nav-link-custom <%= "manager-dashboard".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-speedometer2"></i>
                <span>Dashboard</span>
            </a>
            <a href="<%= cp %>/manager/projects" class="nav-link-custom <%= "manager-projects".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-folder-fill"></i>
                <span>Projects Overview</span>
            </a>
            <a href="<%= cp %>/manager/tasks" class="nav-link-custom <%= "manager-tasks".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-check2-square"></i>
                <span>Task Management</span>
            </a>
        <% } else if (user != null) { %>
            <!-- Team Member Navigation -->
            <a href="<%= cp %>/member/dashboard" class="nav-link-custom <%= "member-dashboard".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-speedometer2"></i>
                <span>My Dashboard</span>
            </a>
            <a href="<%= cp %>/member/tasks" class="nav-link-custom <%= "member-tasks".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-list-task"></i>
                <span>My Assigned Tasks</span>
            </a>
            <a href="<%= cp %>/member/projects" class="nav-link-custom <%= "member-projects".equals(activePage) ? "active" : "" %>">
                <i class="bi bi-folder2-open"></i>
                <span>My Projects</span>
            </a>
        <% } %>

        <div class="sidebar-heading mt-4">General</div>
        <a href="<%= cp %>/activity" class="nav-link-custom <%= "activity".equals(activePage) ? "active" : "" %>">
            <i class="bi bi-clock-history"></i>
            <span>Activity Feed</span>
        </a>
        <a href="<%= cp %>/profile" class="nav-link-custom <%= "profile".equals(activePage) ? "active" : "" %>">
            <i class="bi bi-person-circle"></i>
            <span>My Profile</span>
        </a>
        <a href="<%= cp %>/logout" class="nav-link-custom text-danger">
            <i class="bi bi-box-arrow-right text-danger"></i>
            <span>Logout</span>
        </a>
    </div>

    <!-- User Profile Footer -->
    <% if (user != null) { %>
    <div class="sidebar-user">
        <div class="user-avatar">
            <%= user.getFullName() != null && user.getFullName().length() > 0 ? com.protrack.util.ValidationUtil.sanitize(user.getFullName().substring(0, 1).toUpperCase()) : "U" %>
        </div>
        <div class="user-info">
            <div class="user-name" title="<%= com.protrack.util.ValidationUtil.sanitize(user.getFullName()) %>"><%= com.protrack.util.ValidationUtil.sanitize(user.getFullName()) %></div>
            <div class="user-role"><%= user.getRole().getDisplayName() %></div>
        </div>
    </div>
    <% } %>
</aside>
