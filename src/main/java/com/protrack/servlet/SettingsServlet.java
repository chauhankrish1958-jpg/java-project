package com.protrack.servlet;

import com.protrack.exception.AppException;
import com.protrack.model.SystemSetting;
import com.protrack.model.User;
import com.protrack.service.SettingsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Admin System Settings configuration servlet.
 */
@WebServlet(name = "SettingsServlet", urlPatterns = {"/admin/settings"})
public class SettingsServlet extends HttpServlet {

    private final SettingsService settingsService = new SettingsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<SystemSetting> settingsList = settingsService.getSettingsList();
        Map<String, String> settingsMap = settingsService.getAllSettings();

        request.setAttribute("settingsList", settingsList);
        request.setAttribute("settingsMap", settingsMap);

        request.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        Integer actorUserId = (currentUser != null) ? currentUser.getId() : null;

        try {
            String appName = request.getParameter("app_name");
            String companyName = request.getParameter("company_name");
            String dueDays = request.getParameter("task_default_due_days");
            String allowReg = request.getParameter("allow_user_registration");
            String banner = request.getParameter("notification_banner");

            if (appName != null) {
                settingsService.updateSetting("app_name", appName.trim(), "Application branding title", actorUserId);
            }
            if (companyName != null) {
                settingsService.updateSetting("company_name", companyName.trim(), "Organization name", actorUserId);
            }
            if (dueDays != null) {
                settingsService.updateSetting("task_default_due_days", dueDays.trim(), "Default duration for new tasks", actorUserId);
            }
            settingsService.updateSetting("allow_user_registration", "true".equals(allowReg) ? "true" : "false", "Public user registration switch", actorUserId);
            if (banner != null) {
                settingsService.updateSetting("notification_banner", banner.trim(), "Dashboard announcement banner", actorUserId);
            }

            session.setAttribute("flash_success", "System settings saved successfully.");
        } catch (AppException e) {
            session.setAttribute("flash_error", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flash_error", "Failed to save settings: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/settings");
    }
}
