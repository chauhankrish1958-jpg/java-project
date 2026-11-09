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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TaskServletAuthorizationTest {

    private TaskService mockTaskService;
    private ProjectService mockProjectService;
    private UserService mockUserService;
    private TaskServlet taskServlet;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setup() {
        mockTaskService = mock(TaskService.class);
        mockProjectService = mock(ProjectService.class);
        mockUserService = mock(UserService.class);
        taskServlet = new TaskServlet(mockTaskService, mockProjectService, mockUserService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/protrack");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    @DisplayName("Verify PM cannot update task to move it to a project they do not manage")
    public void testPMCannotMoveTaskToUnmanagedProject() throws ServletException, IOException, AppException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("taskId")).thenReturn("100");
        when(request.getParameter("projectId")).thenReturn("20"); // Target project
        when(request.getParameter("title")).thenReturn("Task Title");

        User pm = UserFactory.createUser(5, "PM Alex", "alex@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(pm);

        // Task currently belongs to project 10 (managed by PM Alex)
        Task currentTask = new Task();
        currentTask.setId(100);
        currentTask.setProjectId(10);
        when(mockTaskService.getTaskById(100)).thenReturn(currentTask);

        Project currentProject = new Project(10, "Current Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 5, "IN_PROGRESS");
        when(mockProjectService.getProjectById(10)).thenReturn(currentProject);

        // Target project 20 belongs to someone else (managerId 99)
        Project targetProject = new Project(20, "Target Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 99, "IN_PROGRESS");
        when(mockProjectService.getProjectById(20)).thenReturn(targetProject);

        taskServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(request).setAttribute(eq("errorMessage"), contains("cannot move a task to a project you do not manage"));
        verify(request).getRequestDispatcher("/WEB-INF/views/error/403.jsp");
        verify(dispatcher).forward(request, response);
        verify(mockTaskService, never()).updateTask(anyInt(), anyString(), anyString(), anyInt(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Verify PM cannot update task belonging to an unmanaged project")
    public void testPMCannotUpdateTaskInUnmanagedProject() throws ServletException, IOException, AppException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("taskId")).thenReturn("100");
        when(request.getParameter("projectId")).thenReturn("10");

        User pm = UserFactory.createUser(5, "PM Alex", "alex@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(pm);

        Task currentTask = new Task();
        currentTask.setId(100);
        currentTask.setProjectId(10);
        when(mockTaskService.getTaskById(100)).thenReturn(currentTask);

        // Current project 10 is managed by someone else (managerId 99)
        Project currentProject = new Project(10, "Unmanaged Project", "Desc", Date.valueOf("2026-01-01"),
                Date.valueOf("2026-12-31"), 99, "IN_PROGRESS");
        when(mockProjectService.getProjectById(10)).thenReturn(currentProject);

        taskServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(request).setAttribute(eq("errorMessage"), contains("You can only edit tasks for projects assigned to you"));
        verify(dispatcher).forward(request, response);
        verify(mockTaskService, never()).updateTask(anyInt(), anyString(), anyString(), anyInt(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Verify PM GET /manager/tasks queries tasks restricted to their managerId")
    public void testPMGetTasksRestrictedToManagerId() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protrack/manager/tasks");
        User pm = UserFactory.createUser(5, "PM Alex", "alex@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(pm);

        when(mockTaskService.searchTasks(any(), any(), any(), any(), any(), eq(5))).thenReturn(Collections.emptyList());

        taskServlet.doGet(request, response);

        verify(mockTaskService).searchTasks(any(), any(), any(), any(), any(), eq(5));
        verify(request).getRequestDispatcher("/WEB-INF/views/manager/tasks.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify PM GET /manager/tasks with unowned projectId returns empty list")
    public void testPMGetTasksWithUnownedProjectIdReturnsEmpty() throws ServletException, IOException, AppException {
        when(request.getRequestURI()).thenReturn("/protrack/manager/tasks");
        when(request.getParameter("projectId")).thenReturn("99");
        User pm = UserFactory.createUser(5, "PM Alex", "alex@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(pm);

        // Project 99 is managed by manager 77, not PM 5
        Project p = new Project(99, "Other Proj", "Desc", null, null, 77, "IN_PROGRESS");
        when(mockProjectService.getProjectById(99)).thenReturn(p);

        taskServlet.doGet(request, response);

        verify(request).setAttribute(eq("tasks"), eq(Collections.emptyList()));
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify ADMIN GET /manager/tasks sees all tasks (managerId is null)")
    public void testAdminGetTasksSeesAll() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/protrack/manager/tasks");
        User admin = UserFactory.createUser(1, "Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(admin);

        when(mockTaskService.searchTasks(any(), any(), any(), any(), any(), isNull())).thenReturn(Collections.emptyList());

        taskServlet.doGet(request, response);

        verify(mockTaskService).searchTasks(any(), any(), any(), any(), any(), isNull());
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Verify TaskServlet POST with malformed date sets flash_error with expected date format")
    public void testTaskCreateWithMalformedDateSetsFlashError() throws ServletException, IOException, AppException {
        when(request.getRequestURI()).thenReturn("/protrack/manager/tasks");
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("projectId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Valid Task");
        when(request.getParameter("startDate")).thenReturn("2026-01-01");
        when(request.getParameter("dueDate")).thenReturn("invalid-due-date");

        User manager = UserFactory.createUser(5, "Manager", "pm@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(session.getAttribute("currentUser")).thenReturn(manager);

        Project project = new Project(1, "Project 1", "Desc", null, null, 5, "PLANNING");
        when(mockProjectService.getProjectById(1)).thenReturn(project);

        taskServlet.doPost(request, response);

        verify(session).setAttribute(eq("flash_error"), contains("Invalid date format (expected yyyy-MM-dd)"));
    }
}
