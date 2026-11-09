package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.TaskDAO;
import com.protrack.exception.AppException;
import com.protrack.exception.UnauthorizedAccessException;
import com.protrack.model.Role;
import com.protrack.model.Task;
import com.protrack.model.TaskStatus;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskAuthorizationTest {

    private TaskDAO mockTaskDAO;
    private ActivityDAO mockActivityDAO;
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        mockTaskDAO = mock(TaskDAO.class);
        mockActivityDAO = mock(ActivityDAO.class);
        taskService = new TaskService(mockTaskDAO, mockActivityDAO);
    }

    @Test
    @DisplayName("Verify TEAM_MEMBER can update the status of a task assigned to them")
    public void testTeamMemberCanUpdateOwnTask() throws AppException {
        User member = UserFactory.createUser(10, "Dev Alex", "alex@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");

        Task task = new Task();
        task.setId(100);
        task.setTaskTitle("Implement OAuth Module");
        task.setAssignedTo(10); // Assigned directly to Alex
        task.setStatus(TaskStatus.TODO);

        when(mockTaskDAO.findById(100)).thenReturn(Optional.of(task));
        when(mockTaskDAO.updateStatus(100, TaskStatus.IN_PROGRESS)).thenReturn(true);

        assertDoesNotThrow(() -> {
            taskService.updateTaskStatusWithRoleCheck(100, TaskStatus.IN_PROGRESS, member);
        });

        verify(mockTaskDAO).updateStatus(100, TaskStatus.IN_PROGRESS);
        verify(mockActivityDAO).log(eq(10), eq("TASK_STATUS_UPDATE"), anyString(), eq("TASK"), eq(100));
    }

    @Test
    @DisplayName("Verify TEAM_MEMBER is denied when attempting to update someone else's task")
    public void testTeamMemberDeniedOnOthersTask() {
        User member = UserFactory.createUser(10, "Dev Alex", "alex@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");

        Task othersTask = new Task();
        othersTask.setId(101);
        othersTask.setTaskTitle("Database Migration");
        othersTask.setAssignedTo(99); // Assigned to another developer (ID 99)
        othersTask.setStatus(TaskStatus.TODO);

        when(mockTaskDAO.findById(101)).thenReturn(Optional.of(othersTask));

        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class, () -> {
            taskService.updateTaskStatusWithRoleCheck(101, TaskStatus.COMPLETED, member);
        });

        assertTrue(ex.getMessage().contains("Access Denied"));
        verify(mockTaskDAO, never()).updateStatus(anyInt(), any(TaskStatus.class));
    }

    @Test
    @DisplayName("Verify TEAM_MEMBER is denied when attempting to update an unassigned task")
    public void testTeamMemberDeniedOnUnassignedTask() {
        User member = UserFactory.createUser(10, "Dev Alex", "alex@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");

        Task unassignedTask = new Task();
        unassignedTask.setId(102);
        unassignedTask.setTaskTitle("Setup CI Pipeline");
        unassignedTask.setAssignedTo(null); // Unassigned
        unassignedTask.setStatus(TaskStatus.TODO);

        when(mockTaskDAO.findById(102)).thenReturn(Optional.of(unassignedTask));

        UnauthorizedAccessException ex = assertThrows(UnauthorizedAccessException.class, () -> {
            taskService.updateTaskStatusWithRoleCheck(102, TaskStatus.IN_PROGRESS, member);
        });

        assertTrue(ex.getMessage().contains("Access Denied"));
        verify(mockTaskDAO, never()).updateStatus(anyInt(), any(TaskStatus.class));
    }

    @Test
    @DisplayName("Verify ADMIN can update status on any task")
    public void testAdminCanUpdateAnyTask() throws AppException {
        User admin = UserFactory.createUser(1, "Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");

        Task anyTask = new Task();
        anyTask.setId(103);
        anyTask.setTaskTitle("Global Architecture Review");
        anyTask.setAssignedTo(77);
        anyTask.setStatus(TaskStatus.REVIEW);

        when(mockTaskDAO.findById(103)).thenReturn(Optional.of(anyTask));
        when(mockTaskDAO.updateStatus(103, TaskStatus.COMPLETED)).thenReturn(true);

        assertDoesNotThrow(() -> {
            taskService.updateTaskStatusWithRoleCheck(103, TaskStatus.COMPLETED, admin);
        });

        verify(mockTaskDAO).updateStatus(103, TaskStatus.COMPLETED);
    }
}
