<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String flashSuccess = (String) session.getAttribute("flash_success");
    String flashError = (String) session.getAttribute("flash_error");
    String reqError = (String) request.getAttribute("errorMessage");
    String reqSuccess = (String) request.getAttribute("successMessage");

    // Clear session flash attributes
    if (flashSuccess != null) session.removeAttribute("flash_success");
    if (flashError != null) session.removeAttribute("flash_error");

    String successMsg = flashSuccess != null ? flashSuccess : reqSuccess;
    String errorMsg = flashError != null ? flashError : reqError;
%>

<% if (successMsg != null && !successMsg.trim().isEmpty()) { %>
<div class="alert alert-success alert-dismissible fade show d-flex align-items-center mb-4 shadow-sm border-0" role="alert">
    <i class="bi bi-check-circle-fill me-2 fs-5"></i>
    <div class="fw-medium"><%= successMsg %></div>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<% } %>

<% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
<div class="alert alert-danger alert-dismissible fade show d-flex align-items-center mb-4 shadow-sm border-0" role="alert">
    <i class="bi bi-exclamation-triangle-fill me-2 fs-5"></i>
    <div class="fw-medium"><%= errorMsg %></div>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
</div>
<% } %>
