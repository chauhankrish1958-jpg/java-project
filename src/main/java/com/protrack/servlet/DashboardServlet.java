package com.protrack.servlet;

import com.protrack.model.User;
import com.protrack.service.DashboardService;
import com.protrack.service.SettingsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * Dispatches role-specific analytical dashboard data and view templates.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {
        "/admin/dashboard",
        "/manager/dashboard",
        "/member/dashboard"
})
public class DashboardServlet extends HttpServlet {

    private final DashboardService dashboardService = new DashboardService();
    private final SettingsService settingsService = new SettingsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String uri = request.getRequestURI();
        String notificationBanner = settingsService.getSetting("notification_banner", "");
        request.setAttribute("notificationBanner", notificationBanner);

        if (uri.endsWith("/admin/dashboard")) {
            Map<String, Object> adminData = dashboardService.getAdminDashboardData();
            adminData.forEach(request::setAttribute);
            request.setAttribute("schedulerMetrics", com.protrack.scheduler.OverdueTaskScanner.getInstance().getMetricsSnapshot());
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
        } else if (uri.endsWith("/manager/dashboard")) {
            Map<String, Object> managerData = dashboardService.getManagerDashboardData(currentUser.getId());
            managerData.forEach(request::setAttribute);
            request.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(request, response);
        } else {
            // Team Member dashboard
            Map<String, Object> memberData = dashboardService.getMemberDashboardData(currentUser.getId());
            memberData.forEach(request::setAttribute);
            request.getRequestDispatcher("/WEB-INF/views/member/dashboard.jsp").forward(request, response);
        }
    }
}
