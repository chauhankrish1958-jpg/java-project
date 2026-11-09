package com.protrack.filter;

import com.protrack.dao.UserDAO;
import com.protrack.dao.impl.UserDAOImpl;
import com.protrack.model.Role;
import com.protrack.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Authentication and Role-Based Access Control (RBAC) Filter.
 * Enforces session verification and prevents URL tampering across roles.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    private final UserDAO userDAO;

    public AuthenticationFilter() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthenticationFilter(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String uri = (servletPath != null ? servletPath : "") + (pathInfo != null ? pathInfo : "");
        if (uri.isEmpty()) uri = "/";

        // Neutralize Matrix Parameters (;...) and path confusion bypasses
        int semicolonIndex = uri.indexOf(';');
        if (semicolonIndex >= 0) {
            uri = uri.substring(0, semicolonIndex);
        }

        // 1. Allow static resources strictly from /assets/ directory
        if (uri.startsWith("/assets/")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Ensure CSRF token is available on active session for forms
        if (session != null) {
            com.protrack.util.CSRFTokenUtil.getToken(session);
        }

        // 2. Public auth endpoints
        boolean isPublicPath = uri.equals("/login") || uri.equals("/register") || uri.equals("/index.jsp")
                || uri.equals("/") || uri.startsWith("/error");

        if (currentUser == null) {
            // Unauthenticated user
            if (isPublicPath) {
                chain.doFilter(req, res);
                return;
            }
            // Attempted access to protected resource: redirect to login
            session = request.getSession(true);
            session.setAttribute("flash_error", "Please log in to access this page.");
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // 3. Stale session validation: reload user status & role from database every 30 seconds
        Long lastValidation = (Long) session.getAttribute("lastUserValidationTime");
        long now = System.currentTimeMillis();
        if (lastValidation == null || (now - lastValidation) > 30_000L) {
            Optional<User> freshUserOpt = userDAO.findById(currentUser.getId());
            if (freshUserOpt.isEmpty() || !"ACTIVE".equalsIgnoreCase(freshUserOpt.get().getStatus())) {
                session.invalidate();
                HttpSession newSession = request.getSession(true);
                newSession.setAttribute("flash_error", "Your session has expired or your account is no longer active. Please log in again.");
                response.sendRedirect(contextPath + "/login");
                return;
            }
            User freshUser = freshUserOpt.get();
            session.setAttribute("currentUser", freshUser);
            session.setAttribute("lastUserValidationTime", now);
            currentUser = freshUser;
        }

        // 4. User is logged in: prevent accessing login or register pages again
        if (uri.equals("/login") || uri.equals("/register") || uri.equals("/") || uri.equals("/index.jsp")) {
            response.sendRedirect(contextPath + currentUser.getDashboardUrl());
            return;
        }

        // 5. Validate CSRF token on protected state-changing POST requests
        if ("POST".equalsIgnoreCase(request.getMethod()) && !isPublicPath) {
            if (!com.protrack.util.CSRFTokenUtil.isValid(request)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                session.setAttribute("flash_error", "Security alert: Invalid or missing CSRF token. Operation rejected.");
                request.setAttribute("errorMessage", "Security validation failed: Invalid or missing CSRF token.");
                request.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
                return;
            }
        }

        // 5. Role-based URL Authorization
        Role role = currentUser.getRole();

        // Admin-only endpoints
        if (uri.startsWith("/admin/") || uri.startsWith("/admin")) {
            if (role != Role.ADMIN) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("errorMessage", "Access Denied: You do not have Administrator privileges.");
                request.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
                return;
            }
        }

        // Project Manager endpoints (Admins also allowed)
        if (uri.startsWith("/manager/") || uri.startsWith("/manager")) {
            if (role != Role.ADMIN && role != Role.PROJECT_MANAGER) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                request.setAttribute("errorMessage", "Access Denied: You do not have Project Manager privileges.");
                request.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
                return;
            }
        }

        // Team Member endpoints: accessible to any authenticated user
        // Shared profile and activity pages: accessible to any authenticated user

        // User is authorized for this route
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
