package com.protrack.servlet;

import com.protrack.model.Role;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import com.protrack.util.CSRFTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class LogoutServletTest {

    private LogoutServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private HttpSession newSession;

    @BeforeEach
    public void setup() {
        servlet = new LogoutServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        newSession = mock(HttpSession.class);

        when(request.getContextPath()).thenReturn("/protrack");
    }

    @Test
    @DisplayName("Verify GET /logout while logged in redirects to dashboard without invalidating session")
    public void testDoGetWhileLoggedInRedirectsToDashboard() throws ServletException, IOException {
        User user = UserFactory.createUser(1, "John Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);

        servlet.doGet(request, response);

        verify(session, never()).invalidate();
        verify(response).sendRedirect("/protrack/admin/dashboard");
    }

    @Test
    @DisplayName("Verify GET /logout while unauthenticated returns HTTP 405 Method Not Allowed")
    public void testDoGetWhileNotLoggedInReturns405() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_METHOD_NOT_ALLOWED), anyString());
    }

    @Test
    @DisplayName("Verify POST /logout with valid CSRF token invalidates session and redirects to /login")
    public void testDoPostWithValidCSRFTokenLogsOut() throws ServletException, IOException {
        String token = "valid-csrf-token-12345";
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn(token);
        when(request.getParameter("csrfToken")).thenReturn(token);
        when(request.getSession(true)).thenReturn(newSession);

        servlet.doPost(request, response);

        verify(session).invalidate();
        verify(newSession).setAttribute(eq("flash_success"), anyString());
        verify(response).sendRedirect("/protrack/login");
    }

    @Test
    @DisplayName("Verify POST /logout with missing or invalid CSRF token returns HTTP 403 Forbidden")
    public void testDoPostWithInvalidCSRFTokenReturns403() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("actual-token");
        when(request.getParameter("csrfToken")).thenReturn("wrong-token");

        servlet.doPost(request, response);

        verify(session, never()).invalidate();
        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
    }
}
