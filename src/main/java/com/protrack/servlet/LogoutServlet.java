package com.protrack.servlet;

import com.protrack.model.User;
import com.protrack.util.CSRFTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles secure session termination and logout redirection.
 * Enforces POST-only invocation with CSRF verification to prevent cross-site logout attacks.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser != null) {
            // Do not log out on GET - safely redirect back to dashboard
            response.sendRedirect(request.getContextPath() + currentUser.getDashboardUrl());
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method GET is not supported by this URL. Logout requires a POST request.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!CSRFTokenUtil.isValid(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token.");
            return;
        }
        processLogout(request, response);
    }

    private void processLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Create new session just to flash the logout notice
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("flash_success", "You have been logged out securely.");

        response.sendRedirect(request.getContextPath() + "/login");
    }
}
