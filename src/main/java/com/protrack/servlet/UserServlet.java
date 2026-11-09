package com.protrack.servlet;

import com.protrack.exception.AppException;
import com.protrack.model.Role;
import com.protrack.model.User;
import com.protrack.service.UserService;
import com.protrack.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Admin User Management Servlet handling User CRUD, search, and filtering.
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/admin/users"})
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("q");
        String roleParam = request.getParameter("role");

        Role roleFilter = null;
        if (roleParam != null && !roleParam.trim().isEmpty() && !"ALL".equalsIgnoreCase(roleParam)) {
            roleFilter = Role.fromString(roleParam);
        }

        List<User> userList = userService.searchUsers(query, roleFilter);

        request.setAttribute("users", userList);
        request.setAttribute("searchQuery", query != null ? query.trim() : "");
        request.setAttribute("selectedRole", roleParam != null ? roleParam.trim() : "ALL");
        request.setAttribute("allRoles", Role.values());

        request.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        Integer actorUserId = (currentUser != null) ? currentUser.getId() : null;

        String action = request.getParameter("action");
        if (action == null) action = "create";

        try {
            switch (action) {
                case "create" -> {
                    String fullName = request.getParameter("fullName");
                    String email = request.getParameter("email");
                    String password = request.getParameter("password");
                    String roleStr = request.getParameter("role");
                    String status = request.getParameter("status");

                    Role role = Role.fromString(roleStr);
                    userService.createUser(fullName, email, password, role, status, actorUserId);
                    session.setAttribute("flash_success", "User \"" + fullName + "\" created successfully.");
                }
                case "update" -> {
                    int userId = ValidationUtil.parsePositiveInt(request.getParameter("userId"), 0);
                    String fullName = request.getParameter("fullName");
                    String email = request.getParameter("email");
                    String roleStr = request.getParameter("role");
                    String status = request.getParameter("status");

                    Role role = Role.fromString(roleStr);
                    userService.updateUser(userId, fullName, email, role, status, actorUserId);
                    session.setAttribute("flash_success", "User details updated successfully.");
                }
                case "delete" -> {
                    int userId = ValidationUtil.parsePositiveInt(request.getParameter("userId"), 0);
                    userService.deleteUser(userId, actorUserId);
                    session.setAttribute("flash_success", "User deleted successfully.");
                }
                default -> session.setAttribute("flash_error", "Unknown action: " + action);
            }
        } catch (AppException e) {
            session.setAttribute("flash_error", e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Unexpected error in UserServlet", e);
            session.setAttribute("flash_error", "An unexpected system error occurred. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
