package com.protrack.filter;

import com.protrack.model.Role;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import com.protrack.util.CSRFTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import com.protrack.dao.UserDAO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SecurityRBACAndAuthTest {

    private AuthenticationFilter filter;
    private UserDAO mockUserDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private FilterChain chain;

    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setup() {
        mockUserDAO = mock(UserDAO.class);
        filter = new AuthenticationFilter(mockUserDAO);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        chain = mock(FilterChain.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getContextPath()).thenReturn("/protrack");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(session);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        when(mockUserDAO.findById(anyInt())).thenAnswer(inv -> {
            User u = (User) session.getAttribute("currentUser");
            return Optional.ofNullable(u);
        });
    }

    @Test
    @DisplayName("Verify unauthenticated access to /admin/users is redirected to /login")
    public void testUnauthenticatedAccessRedirectsToLogin() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/admin/users");
        when(session.getAttribute("currentUser")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/protrack/login");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify semicolon matrix parameter bypass (/manager/tasks;x=.css) does NOT bypass authentication")
    public void testMatrixParameterBypassAttemptBlocked() throws IOException, ServletException {
        // Attack scenario: attacker attaches ;x=.css suffix trying to trigger static .css whitelist
        when(request.getServletPath()).thenReturn("/manager/tasks;x=.css");
        when(session.getAttribute("currentUser")).thenReturn(null);

        filter.doFilter(request, response, chain);

        // Must redirect to /login and NOT pass through the filter chain
        verify(response).sendRedirect("/protrack/login");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify TEAM_MEMBER accessing /admin/* is denied with 403 Forbidden")
    public void testTeamMemberBlockedFromAdminRole() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/admin/users");
        User member = UserFactory.createUser(10, "Dev", "dev@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(member);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify TEAM_MEMBER accessing /manager/* is denied with 403 Forbidden")
    public void testTeamMemberBlockedFromManagerRole() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/manager/projects");
        User member = UserFactory.createUser(10, "Dev", "dev@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(member);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify PROJECT_MANAGER accessing /admin/* is denied with 403 Forbidden")
    public void testManagerBlockedFromAdminSettings() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/admin/settings");
        User manager = UserFactory.createUser(5, "PM", "pm@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(manager);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify public static resources under /assets/ pass through without authentication")
    public void testStaticAssetsAllowedWithoutAuth() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/assets/css/style.css");
        when(session.getAttribute("currentUser")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Verify POST request without CSRF token is rejected with HTTP 403 Forbidden")
    public void testPostRequestWithoutCSRFTokenRejected() throws IOException, ServletException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/admin/users");
        User admin = UserFactory.createUser(1, "Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        // Valid session has token "secret-csrf-token", but request contains null or wrong token
        when(session.getAttribute(CSRFTokenUtil.SESSION_ATTR)).thenReturn("secret-csrf-token");
        when(request.getParameter("csrfToken")).thenReturn("tampered-or-missing-token");

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify stale session: deactivated user has session invalidated and redirected to /login")
    public void testStaleSessionDeactivatedUserInvalidated() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/member/tasks");
        User cachedUser = UserFactory.createUser(10, "Dev", "dev@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(cachedUser);

        // Database now has the user marked as INACTIVE
        User deactivated = UserFactory.createUser(10, "Dev", "dev@protrack.com", "hash", Role.TEAM_MEMBER, "INACTIVE");
        when(mockUserDAO.findById(10)).thenReturn(Optional.of(deactivated));

        HttpSession freshSession = mock(HttpSession.class);
        when(request.getSession(true)).thenReturn(freshSession);

        filter.doFilter(request, response, chain);

        verify(session).invalidate();
        verify(freshSession).setAttribute(eq("flash_error"), contains("no longer active"));
        verify(response).sendRedirect("/protrack/login");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Verify stale session: user demoted from ADMIN to TEAM_MEMBER has role refreshed and blocked from /admin")
    public void testUserRoleDemotedRefreshesRoleAndBlocksAdminAccess() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/admin/settings");
        // Cached in session as ADMIN:
        User cachedAdmin = UserFactory.createUser(5, "FormerAdmin", "fa@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(cachedAdmin);

        // In database, role was changed to TEAM_MEMBER:
        User freshMember = UserFactory.createUser(5, "FormerAdmin", "fa@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");
        when(mockUserDAO.findById(5)).thenReturn(Optional.of(freshMember));

        filter.doFilter(request, response, chain);

        // Session updated with fresh user
        verify(session).setAttribute("currentUser", freshMember);
        // And since fresh role is TEAM_MEMBER, access to /admin/settings is rejected with 403
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }
}
