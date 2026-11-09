<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.Project" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    request.setAttribute("pageTitle", "My Assigned Projects");
    request.setAttribute("pageSubtitle", "View projects you are actively contributing to and review timelines");
    request.setAttribute("activeMenu", "member-projects");

    @SuppressWarnings("unchecked")
    List<Project> projectList = (List<Project>) request.getAttribute("projects");
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <div class="row g-4">
            <% if (projectList != null && !projectList.isEmpty()) {
                for (Project p : projectList) { %>
                <div class="col-md-6 col-xl-4">
                    <div class="protrack-card h-100">
                        <div class="protrack-card-body p-4 d-flex flex-column justify-content-between">
                            <div>
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <span class="badge badge-custom <%= p.getStatusBadgeClass() %>"><%= p.getStatus() %></span>
                                    <span class="small text-muted"><i class="bi bi-calendar-event me-1"></i>Due <%= DateUtil.formatDisplayDate(p.getEndDate()) %></span>
                                </div>
                                <h5 class="fw-bold text-dark mb-2"><%= ValidationUtil.escapeXml(p.getTitle()) %></h5>
                                <p class="text-secondary small mb-3"><%= p.getDescription() != null ? ValidationUtil.escapeXml(p.getDescription()) : "No project description provided." %></p>
                            </div>

                            <div class="pt-3 border-top">
                                <div class="d-flex align-items-center mb-3">
                                    <div class="rounded-circle bg-light border text-primary fw-bold d-flex align-items-center justify-content-center me-2"
                                         style="width: 32px; height: 32px; font-size: 0.8rem;">
                                        <%= p.getManagerName() != null && !p.getManagerName().isEmpty() ? ValidationUtil.escapeXml(p.getManagerName().substring(0, 1).toUpperCase()) : "M" %>
                                    </div>
                                    <div class="small">
                                        <div class="fw-semibold text-dark"><%= p.getManagerName() != null ? ValidationUtil.escapeXml(p.getManagerName()) : "Unassigned" %></div>
                                        <div class="text-muted">Project Manager</div>
                                    </div>
                                </div>

                                <div class="d-flex justify-content-between align-items-center small mb-1">
                                    <span class="text-muted"><%= p.getCompletedTasks() %> / <%= p.getTotalTasks() %> Tasks</span>
                                    <span class="fw-bold text-dark"><%= p.getProgressPercentage() %>%</span>
                                </div>
                                <div class="progress progress-custom">
                                    <div class="progress-bar" style="width: <%= p.getProgressPercentage() %>%;"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <%  }
               } else { %>
                <div class="col-12">
                    <div class="protrack-card p-5 text-center text-muted">
                        <i class="bi bi-folder-x fs-1 text-secondary d-block mb-3"></i>
                        <h5>No Projects Assigned</h5>
                        <p class="small text-muted">You are not currently allocated to any project teams. Please contact your Project Manager.</p>
                    </div>
                </div>
            <% } %>
        </div>

    </div><!-- /.content-body -->

<jsp:include page="../common/footer.jsp" />
