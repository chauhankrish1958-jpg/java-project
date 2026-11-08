package com.protrack.filter;

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

/**
 * Authentication and Role-Based Access Control (RBAC) Filter.
 * Enforces session verification and prevents URL tampering across roles.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI().substring(contextPath.length());

        // 1. Allow static resources (CSS, JS, images, fonts)
        if (uri.startsWith("/assets/") || uri.endsWith(".css") || uri.endsWith(".js")
                || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".ico")
                || uri.endsWith(".svg")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

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

        // 3. User is logged in: prevent accessing login or register pages again
        if (uri.equals("/login") || uri.equals("/register") || uri.equals("/") || uri.equals("/index.jsp")) {
            response.sendRedirect(contextPath + currentUser.getDashboardUrl());
            return;
        }

        // 4. Role-based URL Authorization
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
