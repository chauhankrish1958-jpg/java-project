package com.protrack.servlet;

import com.protrack.exception.AppException;
import com.protrack.model.ActivityLog;
import com.protrack.model.User;
import com.protrack.service.ActivityService;
import com.protrack.service.AuthService;
import com.protrack.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Handles personal profile inspection, name/email modifications and password changes.
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final AuthService authService = new AuthService();
    private final ActivityService activityService = new ActivityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Fresh state from DB
            User freshUser = userService.getUserById(currentUser.getId());
            session.setAttribute("currentUser", freshUser);

            List<ActivityLog> myActivities = activityService.getUserActivities(freshUser.getId(), 10);
            request.setAttribute("myActivities", myActivities);

            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
        } catch (AppException e) {
            session.setAttribute("flash_error", e.getMessage());
            response.sendRedirect(request.getContextPath() + currentUser.getDashboardUrl());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "updateProfile";

        try {
            if ("updateProfile".equalsIgnoreCase(action)) {
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");

                userService.updateProfile(currentUser.getId(), fullName, email);

                // Update session object
                currentUser.setFullName(fullName);
                currentUser.setEmail(email);
                session.setAttribute("currentUser", currentUser);
                session.setAttribute("flash_success", "Profile details updated successfully.");
            } else if ("changePassword".equalsIgnoreCase(action)) {
                String currentPassword = request.getParameter("currentPassword");
                String newPassword = request.getParameter("newPassword");
                String confirmPassword = request.getParameter("confirmPassword");

                if (newPassword == null || !newPassword.equals(confirmPassword)) {
                    throw new AppException("New passwords do not match.");
                }

                authService.changePassword(currentUser.getId(), currentPassword, newPassword);
                session.setAttribute("flash_success", "Password updated successfully.");
            }
        } catch (AppException e) {
            session.setAttribute("flash_error", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("flash_error", "Operation failed: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
