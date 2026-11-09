<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.protrack.model.SystemSetting" %>
<%@ page import="com.protrack.util.ValidationUtil" %>
<%
    request.setAttribute("pageTitle", "System Settings");
    request.setAttribute("pageSubtitle", "Configure application preferences, registration policies and announcement banners");
    request.setAttribute("activeMenu", "admin-settings");

    @SuppressWarnings("unchecked")
    Map<String, String> settingsMap = (Map<String, String>) request.getAttribute("settingsMap");
    @SuppressWarnings("unchecked")
    List<SystemSetting> settingsList = (List<SystemSetting>) request.getAttribute("settingsList");

    String appName = settingsMap != null && settingsMap.get("app_name") != null ? settingsMap.get("app_name") : "ProTrack Enterprise";
    String companyName = settingsMap != null && settingsMap.get("company_name") != null ? settingsMap.get("company_name") : "ProTrack Software Labs";
    String dueDays = settingsMap != null && settingsMap.get("task_default_due_days") != null ? settingsMap.get("task_default_due_days") : "7";
    String allowReg = settingsMap != null && settingsMap.get("allow_user_registration") != null ? settingsMap.get("allow_user_registration") : "true";
    String banner = settingsMap != null && settingsMap.get("notification_banner") != null ? settingsMap.get("notification_banner") : "";
%>

<jsp:include page="../common/header.jsp" />
<jsp:include page="../common/sidebar.jsp" />

<div class="main-wrapper">
    <jsp:include page="../common/navbar.jsp" />

    <div class="content-body">
        <jsp:include page="../common/alerts.jsp" />

        <div class="row g-4">
            <!-- Settings Form -->
            <div class="col-lg-8">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-sliders"></i> Global Application Configuration
                        </h5>
                        <span class="small text-muted">Persisted in system_settings table</span>
                    </div>
                    <div class="protrack-card-body p-4">
                        <form action="<%= request.getContextPath() %>/admin/settings" method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <div class="row g-3 mb-4">
                                <div class="col-md-6">
                                    <label for="appName" class="form-label">Application Branding Name</label>
                                    <input type="text" class="form-control" id="appName" name="app_name" value="<%= ValidationUtil.escapeXml(appName) %>" required>
                                    <div class="form-text small text-muted">Appears in header, browser titles, and emails.</div>
                                </div>
                                <div class="col-md-6">
                                    <label for="companyName" class="form-label">Organization / Institution Name</label>
                                    <input type="text" class="form-control" id="companyName" name="company_name" value="<%= ValidationUtil.escapeXml(companyName) %>" required>
                                    <div class="form-text small text-muted">Host organization name.</div>
                                </div>
                            </div>

                            <div class="row g-3 mb-4">
                                <div class="col-md-6">
                                    <label for="dueDays" class="form-label">Default Task Duration (Days)</label>
                                    <input type="number" class="form-control" id="dueDays" name="task_default_due_days" value="<%= ValidationUtil.escapeXml(dueDays) %>" min="1" max="365" required>
                                    <div class="form-text small text-muted">Default days added to start date when planning tasks.</div>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label d-block">Public User Registration</label>
                                    <div class="form-check form-switch mt-2">
                                        <input class="form-check-input" type="checkbox" role="switch" id="allowReg" name="allow_user_registration" value="true" <%= "true".equalsIgnoreCase(allowReg) ? "checked" : "" %>>
                                        <label class="form-check-label fw-medium" for="allowReg">Enable Self-Registration</label>
                                    </div>
                                    <div class="form-text small text-muted">When disabled, only Admins can provision new users.</div>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label for="banner" class="form-label">Global Notification / Announcement Banner</label>
                                <textarea class="form-control" id="banner" name="notification_banner" rows="2" placeholder="e.g. Scheduled maintenance this Saturday at 10 PM UTC"><%= ValidationUtil.escapeXml(banner) %></textarea>
                                <div class="form-text small text-muted">Broadcasted in the top navigation bar across all logged-in user dashboards.</div>
                            </div>

                            <hr class="my-4">

                            <div class="d-flex justify-content-end">
                                <button type="submit" class="btn btn-primary px-4">
                                    <i class="bi bi-save me-1"></i> Save Configuration
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Settings Summary Card -->
            <div class="col-lg-4">
                <div class="protrack-card">
                    <div class="protrack-card-header">
                        <h5 class="protrack-card-title">
                            <i class="bi bi-database-check"></i> Active Parameters
                        </h5>
                    </div>
                    <div class="protrack-card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-sm table-modern">
                                <thead>
                                    <tr>
                                        <th>Key</th>
                                        <th>Current Value</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (settingsList != null) {
                                        for (SystemSetting s : settingsList) { %>
                                        <tr>
                                            <td class="font-monospace small text-primary"><%= ValidationUtil.escapeXml(s.getSettingKey()) %></td>
                                            <td class="small text-truncate" style="max-width: 160px;" title="<%= ValidationUtil.escapeXml(s.getSettingValue()) %>">
                                                <%= ValidationUtil.escapeXml(s.getSettingValue()) %>
                                            </td>
                                        </tr>
                                    <%  }
                                       } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div><!-- /.content-body -->

<jsp:include page="../common/footer.jsp" />
