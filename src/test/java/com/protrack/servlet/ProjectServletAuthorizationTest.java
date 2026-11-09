package com.protrack.servlet;

import com.protrack.exception.AppException;
import com.protrack.model.Project;
import com.protrack.model.Role;
import com.protrack.model.Task;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import com.protrack.service.ProjectService;
import com.protrack.service.TaskService;
import com.protrack.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectServletAuthorizationTest {

    private ProjectService mockProjectService;
    private UserService mockUserService;
    private TaskService mockTaskService;
    private ProjectServlet projectServlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setup() {
        mockProjectService = mock(ProjectService.class);
        mockUserService = mock(UserService.class);
        mockTaskService = mock(TaskService.class);
        projectServlet = new ProjectServlet(mockProjectService, mockUserService, mockTaskService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/protrack");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    @DisplayName("Verify PROJECT_MANAGER viewing another manager's project is blocked with HTTP 403")
    public void testManagerBlockedFromOtherManagerProject() throws ServletException, IOException, AppException {
        when(request.getRequestURI()).thenReturn("/protrack/manager/projects");
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("10");

        User pm = UserFactory.createUser(5, "PM Alex", "alex@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(pm);

        Project otherProject = new Project(10, "Secret Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 99, "IN_PROGRESS"); // managerId is 99 != 5
        when(mockProjectService.getProjectById(10)).thenReturn(otherProject);

        projectServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(request).setAttribute(eq("errorMessage"), contains("You can only view projects that you manage"));
        verify(request).getRequestDispatcher("/WEB-INF/views/error/403.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify PROJECT_MANAGER viewing their own project succeeds")
    public void testManagerCanViewOwnProject() throws ServletException, IOException, AppException {
        when(request.getRequestURI()).thenReturn("/protrack/manager/projects");
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("10");

        User pm = UserFactory.createUser(5, "PM Alex", "alex@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(pm);

        Project ownProject = new Project(10, "My Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 5, "IN_PROGRESS"); // managerId is 5 == pm.getId()
        when(mockProjectService.getProjectById(10)).thenReturn(ownProject);
        when(mockProjectService.getProjectMembers(10)).thenReturn(Collections.emptyList());
        when(mockTaskService.getTasksByProject(10)).thenReturn(Collections.emptyList());

        projectServlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/views/manager/project-detail.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify ADMIN can view any project")
    public void testAdminCanViewAnyProject() throws ServletException, IOException, AppException {
        when(request.getRequestURI()).thenReturn("/protrack/admin/projects");
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("10");

        User admin = UserFactory.createUser(1, "Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        Project project = new Project(10, "Any Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 5, "IN_PROGRESS");
        when(mockProjectService.getProjectById(10)).thenReturn(project);
        when(mockProjectService.getProjectMembers(10)).thenReturn(Collections.emptyList());
        when(mockTaskService.getTasksByProject(10)).thenReturn(Collections.emptyList());

        projectServlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/views/admin/project-detail.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify TEAM_MEMBER not assigned to project is blocked with HTTP 403")
    public void testTeamMemberNotAssignedBlockedWith403() throws ServletException, IOException, AppException {
        when(request.getRequestURI()).thenReturn("/protrack/member/projects");
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("10");

        User member = UserFactory.createUser(20, "Dev John", "john@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(member);

        Project project = new Project(10, "Unassigned Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 5, "IN_PROGRESS");
        when(mockProjectService.getProjectById(10)).thenReturn(project);
        when(mockProjectService.isMemberAssigned(10, 20)).thenReturn(false);
        when(mockTaskService.getTasksByProject(10)).thenReturn(Collections.emptyList());

        projectServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(request).setAttribute(eq("errorMessage"), contains("You are not assigned to this project"));
        verify(request).getRequestDispatcher("/WEB-INF/views/error/403.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify TEAM_MEMBER assigned to project sees only their own tasks")
    public void testTeamMemberAssignedSeesOnlyOwnTasks() throws ServletException, IOException, AppException {
        when(request.getRequestURI()).thenReturn("/protrack/member/projects");
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("10");

        User member = UserFactory.createUser(20, "Dev John", "john@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(member);

        Project project = new Project(10, "Assigned Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 5, "IN_PROGRESS");
        when(mockProjectService.getProjectById(10)).thenReturn(project);
        when(mockProjectService.isMemberAssigned(10, 20)).thenReturn(true);

        Task myTask = new Task(1, "My Task", "Desc", 10, 20, null, null, null, null, null);
        Task otherTask = new Task(2, "Other Task", "Desc", 10, 99, null, null, null, null, null);
        when(mockTaskService.getTasksByProject(10)).thenReturn(List.of(myTask, otherTask));

        projectServlet.doGet(request, response);

        verify(request).setAttribute(eq("tasks"), eq(List.of(myTask)));
        verify(request).getRequestDispatcher("/WEB-INF/views/member/project-detail.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify ProjectServlet POST with malformed date sets flash_error with expected date format")
    public void testProjectCreateWithMalformedDateSetsFlashError() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protrack/admin/projects");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("title")).thenReturn("Valid Title");
        when(request.getParameter("startDate")).thenReturn("invalid-date-format");
        when(request.getParameter("endDate")).thenReturn("2026-12-31");

        User admin = UserFactory.createUser(1, "Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        projectServlet.doPost(request, response);

        verify(session).setAttribute(eq("flash_error"), contains("Invalid date format (expected yyyy-MM-dd)"));
    }
}
