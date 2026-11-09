package com.protrack.dao;

import com.protrack.dao.impl.ProjectDAOImpl;
import com.protrack.exception.DatabaseOperationException;
import com.protrack.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectDAORollbackTest {

    @Test
    @DisplayName("Verify transaction rollback in saveWithMembers when member insertion throws SQLException")
    public void testTransactionRollbackOnMemberFailure() throws SQLException {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPsProj = mock(PreparedStatement.class);
        PreparedStatement mockPsMem = mock(PreparedStatement.class);
        ResultSet mockRsKeys = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPsProj);
        when(mockConn.prepareStatement(contains("project_members")))
                .thenReturn(mockPsMem);

        when(mockPsProj.executeUpdate()).thenReturn(1);
        when(mockPsProj.getGeneratedKeys()).thenReturn(mockRsKeys);
        when(mockRsKeys.next()).thenReturn(true);
        when(mockRsKeys.getInt(1)).thenReturn(42);

        // Simulate batch execution failure for members
        when(mockPsMem.executeBatch()).thenThrow(new SQLException("Deadlock / Foreign key constraint violation"));

        Project project = new Project();
        project.setTitle("Mission Critical Deployment");
        project.setDescription("Core platform upgrade");
        project.setStartDate(Date.valueOf(LocalDate.now()));
        project.setEndDate(Date.valueOf(LocalDate.now().plusMonths(1)));
        project.setManagerId(2);

        List<Integer> memberIds = Arrays.asList(3, 4, 5);

        ProjectDAOImpl dao = new ProjectDAOImpl();

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> {
            dao.saveWithMembers(mockConn, project, memberIds);
        });

        assertTrue(exception.getMessage().contains("Transaction rolled back"));

        // Verify transaction management lifecycle:
        // 1. Transaction started
        verify(mockConn).setAutoCommit(false);
        // 2. Rollback executed due to exception
        verify(mockConn).rollback();
        // 3. Commit was NOT executed
        verify(mockConn, never()).commit();
        // 4. Auto-commit restored in finally block
        verify(mockConn).setAutoCommit(true);
    }

    @Test
    @DisplayName("Verify transaction commit in saveWithMembers when both project and members succeed")
    public void testTransactionCommitOnSuccess() throws SQLException {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPsProj = mock(PreparedStatement.class);
        PreparedStatement mockPsMem = mock(PreparedStatement.class);
        ResultSet mockRsKeys = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPsProj);
        when(mockConn.prepareStatement(contains("project_members")))
                .thenReturn(mockPsMem);

        when(mockPsProj.executeUpdate()).thenReturn(1);
        when(mockPsProj.getGeneratedKeys()).thenReturn(mockRsKeys);
        when(mockRsKeys.next()).thenReturn(true);
        when(mockRsKeys.getInt(1)).thenReturn(101);
        when(mockPsMem.executeBatch()).thenReturn(new int[]{1, 1});

        Project project = new Project();
        project.setTitle("FinTech Portal Launch");
        project.setDescription("Secure banking core");
        project.setStartDate(Date.valueOf(LocalDate.now()));
        project.setEndDate(Date.valueOf(LocalDate.now().plusMonths(2)));
        project.setManagerId(2);

        ProjectDAOImpl dao = new ProjectDAOImpl();
        Project created = dao.saveWithMembers(mockConn, project, Arrays.asList(3, 4));

        assertNotNull(created);
        assertEquals(101, created.getId());

        // Verify successful transaction lifecycle:
        verify(mockConn).setAutoCommit(false);
        verify(mockConn).commit();
        verify(mockConn, never()).rollback();
        verify(mockConn).setAutoCommit(true);
    }
}
