package com.protrack;

import com.protrack.model.Admin;
import com.protrack.model.Project;
import com.protrack.model.ProjectManager;
import com.protrack.model.Role;
import com.protrack.model.Task;
import com.protrack.model.TaskPriority;
import com.protrack.model.TaskStatus;
import com.protrack.model.TeamMember;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import com.protrack.util.BCrypt;
import com.protrack.util.DateUtil;
import com.protrack.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProTrackUnitTest {

    @Test
    @DisplayName("Verify BCrypt password hashing and authentication check")
    public void testBCrypt() {
        String password = "adminSecretPassword123";
        String salt = BCrypt.gensalt(10);
        String hashed = BCrypt.hashpw(password, salt);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$10$"));
        assertTrue(BCrypt.checkpw(password, hashed));
        assertFalse(BCrypt.checkpw("wrongPassword", hashed));
    }

    @Test
    @DisplayName("Verify Object-Oriented Polymorphism and UserFactory")
    public void testOOPPolymorphism() {
        User admin = UserFactory.createUser(1, "Super Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        User manager = UserFactory.createUser(2, "PM Sarah", "pm@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        User member = UserFactory.createUser(3, "Dev Alex", "alex@protrack.com", "hash", Role.TEAM_MEMBER, "ACTIVE");

        // Verify inheritance
        assertTrue(admin instanceof Admin);
        assertTrue(manager instanceof ProjectManager);
        assertTrue(member instanceof TeamMember);

        // Verify polymorphic behavior
        assertTrue(admin.canManageUsers());
        assertFalse(manager.canManageUsers());
        assertFalse(member.canManageUsers());

        assertTrue(admin.canManageProjects());
        assertTrue(manager.canManageProjects());
        assertFalse(member.canManageProjects());

        assertEquals("/admin/dashboard", admin.getDashboardUrl());
        assertEquals("/manager/dashboard", manager.getDashboardUrl());
        assertEquals("/member/dashboard", member.getDashboardUrl());
    }

    @Test
    @DisplayName("Verify Date and Overdue Deadline Calculation")
    public void testDateUtilAndOverdue() {
        LocalDate today = LocalDate.now();
        Date pastDate = Date.valueOf(today.minusDays(3));
        Date futureDate = Date.valueOf(today.plusDays(5));
        Date todayDate = Date.valueOf(today);

        // Overdue tests
        assertTrue(DateUtil.isOverdue(pastDate, "TODO"));
        assertTrue(DateUtil.isOverdue(pastDate, "IN_PROGRESS"));
        // Completed tasks should never be marked overdue
        assertFalse(DateUtil.isOverdue(pastDate, "COMPLETED"));

        assertFalse(DateUtil.isOverdue(futureDate, "TODO"));
        assertTrue(DateUtil.isDueToday(todayDate, "TODO"));

        assertEquals("Due today", DateUtil.getRelativeDueStatus(todayDate, "TODO"));
        assertTrue(DateUtil.getRelativeDueStatus(pastDate, "TODO").contains("Overdue"));
        assertEquals("5 days left", DateUtil.getRelativeDueStatus(futureDate, "TODO"));
        assertEquals("Completed", DateUtil.getRelativeDueStatus(pastDate, "COMPLETED"));
    }

    @Test
    @DisplayName("Verify Input Validation and XSS Sanitization")
    public void testValidationUtil() {
        assertTrue(ValidationUtil.isValidEmail("admin@protrack.com"));
        assertTrue(ValidationUtil.isValidEmail("sarah.pm@sub.domain.org"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("user@.com"));

        assertTrue(ValidationUtil.isStrongPassword("123456"));
        assertFalse(ValidationUtil.isStrongPassword("12345"));

        // XSS sanitization
        String dangerous = "<script>alert('xss');</script>";
        String safe = ValidationUtil.sanitize(dangerous);
        assertFalse(safe.contains("<script>"));
        assertTrue(safe.contains("&lt;script&gt;"));
    }

    @Test
    @DisplayName("Verify Project Progress Percentage Calculation")
    public void testProjectProgress() {
        Project p = new Project();
        p.setTotalTasks(10);
        p.setCompletedTasks(7);
        assertEquals(70, p.getProgressPercentage());

        p.setTotalTasks(0);
        p.setCompletedTasks(0);
        assertEquals(0, p.getProgressPercentage());

        p.setTotalTasks(3);
        p.setCompletedTasks(1);
        assertEquals(33, p.getProgressPercentage());
    }

    @Test
    @DisplayName("Verify Task Entity attributes and status enums")
    public void testTaskEnums() {
        Task t = new Task();
        t.setTaskTitle("Deploy Tomcat App");
        t.setPriority(TaskPriority.CRITICAL);
        t.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals("Critical", t.getPriority().getLabel());
        assertEquals("In Progress", t.getStatus().getLabel());
        assertEquals(TaskPriority.CRITICAL, TaskPriority.fromString("critical"));
        assertEquals(TaskStatus.COMPLETED, TaskStatus.fromString("completed"));
    }
}
