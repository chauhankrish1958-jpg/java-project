package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.UserDAO;
import com.protrack.exception.AppException;
import com.protrack.model.Role;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceProtectionTest {

    private UserDAO mockUserDAO;
    private ActivityDAO mockActivityDAO;
    private UserService userService;

    @BeforeEach
    public void setup() {
        mockUserDAO = mock(UserDAO.class);
        mockActivityDAO = mock(ActivityDAO.class);
        userService = new UserService(mockUserDAO, mockActivityDAO);
    }

    @Test
    @DisplayName("Verify admin cannot change their own role away from ADMIN")
    public void testAdminCannotChangeOwnRole() {
        User admin = UserFactory.createUser(1, "Super Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(mockUserDAO.findById(1)).thenReturn(Optional.of(admin));

        AppException ex = assertThrows(AppException.class, () -> {
            userService.updateUser(1, "Super Admin", "admin@protrack.com", Role.PROJECT_MANAGER, "ACTIVE", 1);
        });

        assertTrue(ex.getMessage().contains("cannot change their own role away from Administrator"));
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    @DisplayName("Verify admin cannot deactivate their own account")
    public void testAdminCannotDeactivateOwnAccount() {
        User admin = UserFactory.createUser(1, "Super Admin", "admin@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(mockUserDAO.findById(1)).thenReturn(Optional.of(admin));

        AppException ex = assertThrows(AppException.class, () -> {
            userService.updateUser(1, "Super Admin", "admin@protrack.com", Role.ADMIN, "INACTIVE", 1);
        });

        assertTrue(ex.getMessage().contains("cannot deactivate their own account"));
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    @DisplayName("Verify demoting the last active admin is rejected")
    public void testCannotDemoteLastActiveAdmin() {
        User admin2 = UserFactory.createUser(2, "Second Admin", "admin2@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(mockUserDAO.findById(2)).thenReturn(Optional.of(admin2));
        when(mockUserDAO.countActiveByRole(Role.ADMIN)).thenReturn(1);

        AppException ex = assertThrows(AppException.class, () -> {
            userService.updateUser(2, "Second Admin", "admin2@protrack.com", Role.TEAM_MEMBER, "ACTIVE", 1);
        });

        assertTrue(ex.getMessage().contains("last remaining active administrator"));
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    @DisplayName("Verify deactivating the last active admin is rejected")
    public void testCannotDeactivateLastActiveAdmin() {
        User admin2 = UserFactory.createUser(2, "Second Admin", "admin2@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(mockUserDAO.findById(2)).thenReturn(Optional.of(admin2));
        when(mockUserDAO.countActiveByRole(Role.ADMIN)).thenReturn(1);

        AppException ex = assertThrows(AppException.class, () -> {
            userService.updateUser(2, "Second Admin", "admin2@protrack.com", Role.ADMIN, "INACTIVE", 1);
        });

        assertTrue(ex.getMessage().contains("last remaining active administrator"));
        verify(mockUserDAO, never()).update(any());
    }

    @Test
    @DisplayName("Verify deleting the last active admin is rejected")
    public void testCannotDeleteLastActiveAdmin() {
        User admin2 = UserFactory.createUser(2, "Second Admin", "admin2@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(mockUserDAO.findById(2)).thenReturn(Optional.of(admin2));
        when(mockUserDAO.countActiveByRole(Role.ADMIN)).thenReturn(1);

        AppException ex = assertThrows(AppException.class, () -> {
            userService.deleteUser(2, 1);
        });

        assertTrue(ex.getMessage().contains("last remaining active administrator"));
        verify(mockUserDAO, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Verify self deletion is rejected")
    public void testSelfDeleteRejected() {
        User user = UserFactory.createUser(3, "PM User", "pm@protrack.com", "hash", Role.PROJECT_MANAGER, "ACTIVE");
        when(mockUserDAO.findById(3)).thenReturn(Optional.of(user));

        AppException ex = assertThrows(AppException.class, () -> {
            userService.deleteUser(3, 3);
        });

        assertTrue(ex.getMessage().contains("cannot delete your own account"));
        verify(mockUserDAO, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Verify admin demotion succeeds when multiple active admins exist")
    public void testDemoteAdminSucceedsWhenMultipleAdminsExist() throws AppException {
        User admin2 = UserFactory.createUser(2, "Second Admin", "admin2@protrack.com", "hash", Role.ADMIN, "ACTIVE");
        when(mockUserDAO.findById(2)).thenReturn(Optional.of(admin2));
        when(mockUserDAO.countActiveByRole(Role.ADMIN)).thenReturn(2);
        when(mockUserDAO.update(any(User.class))).thenReturn(true);

        assertDoesNotThrow(() -> {
            userService.updateUser(2, "Second Admin", "admin2@protrack.com", Role.PROJECT_MANAGER, "ACTIVE", 1);
        });

        verify(mockUserDAO).update(any(User.class));
    }
}
