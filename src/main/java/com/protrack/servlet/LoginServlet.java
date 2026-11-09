package com.protrack.servlet;

import com.protrack.exception.AppException;
import com.protrack.model.User;
import com.protrack.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles user authentication, credential validation and session initiation.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to the login JSP view
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = authService.authenticate(email, password);

            // Create session and protect against session fixation attacks
            HttpSession session = request.getSession(true);
            request.changeSessionId(); // Regenerates session ID to prevent session fixation (Servlet 3.1+)
            com.protrack.util.CSRFTokenUtil.getToken(session); // Generate unique session CSRF token
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout

            // Flash welcome message
            session.setAttribute("flash_success", "Welcome back, " + user.getFullName() + "!");

            // Redirect to role-specific dashboard
            response.sendRedirect(request.getContextPath() + user.getDashboardUrl());
        } catch (AppException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An unexpected error occurred during login. Please try again.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
