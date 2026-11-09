<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.ActivityLog" %>
<%@ page import="com.protrack.util.DateUtil" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    request.setAttribute("pageTitle", "System Activity Feed");
    request.setAttribute("pageSubtitle", "Chronological audit trail of project governance, task transitions and user events");
    request.setAttribute("activeMenu", "activity");

    @SuppressWarnings("unchecked")
    List<ActivityLog> activities = (List<ActivityLog>) request.getAttribute("activities");
%>

<jsp:include page="common/header.jsp" />
<jsp:include page="common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="common/alerts.jsp" />

        <div class="protrack-card">
            <div class="protrack-card-header">
                <h5 class="protrack-card-title">
                    <i class="bi bi-clock-history"></i> Audit Trail History (<%= activities != null ? activities.size() : 0 %> Events)
                </h5>
                <span class="small text-muted">Persisted in activity_logs database table</span>
            </div>
            <div class="protrack-card-body p-4">
                <% if (activities != null && !activities.isEmpty()) { %>
                <div class="timeline">
                    <% for (ActivityLog act : activities) { %>
                    <div class="timeline-item">
                        <div class="timeline-marker"></div>
                        <div class="timeline-content">
                            <div class="d-flex justify-content-between align-items-center mb-1">
                                <span class="badge bg-light text-dark border font-monospace small"><%= ValidationUtil.escapeXml(act.getAction()) %></span>
                                <span class="small text-muted"><i class="bi bi-calendar3 me-1"></i><%= act.getFormattedCreatedAt() %></span>
                            </div>
                            <div class="timeline-title text-dark fs-6 mb-1"><%= ValidationUtil.escapeXml(act.getDetails()) %></div>
                            <div class="timeline-time">
                                <i class="bi bi-person-circle me-1 text-primary"></i>
                                Triggered by <strong><%= act.getUserName() != null ? ValidationUtil.escapeXml(act.getUserName()) : "System" %></strong>
                                <% if (act.getUserRole() != null) { %>
                                    (<span class="text-secondary"><%= act.getUserRole().getDisplayName() %></span>)
                                <% } %>
                                <% if (act.getEntityType() != null) { %>
                                    &bull; Target Entity: <span class="badge bg-light text-secondary border"><%= ValidationUtil.escapeXml(act.getEntityType()) %> #<%= act.getEntityId() != null ? act.getEntityId() : "" %></span>
                                <% } %>
                            </div>
                        </div>
                    </div>
                    <% } %>
                </div>
                <% } else { %>
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-clock fs-1 text-secondary d-block mb-2"></i>
                    No activities recorded yet in system logs.
                </div>
                <% } %>
            </div>
        </div>

    </div><!-- /.content-body -->

<jsp:include page="common/footer.jsp" />
