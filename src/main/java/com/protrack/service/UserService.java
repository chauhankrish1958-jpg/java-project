package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.UserDAO;
import com.protrack.dao.impl.ActivityDAOImpl;
import com.protrack.dao.impl.UserDAOImpl;
import com.protrack.exception.AppException;
import com.protrack.exception.UserNotFoundException;
import com.protrack.model.Role;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import com.protrack.util.BCrypt;
import com.protrack.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Service managing user CRUD operations and profile updates.
 */
public class UserService {

    private final UserDAO userDAO;
    private final ActivityDAO activityDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
        this.activityDAO = new ActivityDAOImpl();
    }

    public UserService(UserDAO userDAO, ActivityDAO activityDAO) {
        this.userDAO = userDAO;
        this.activityDAO = activityDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUserById(int id) throws UserNotFoundException {
        return userDAO.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public List<User> getUsersByRole(Role role) {
        return userDAO.findByRole(role);
    }

    public List<User> searchUsers(String query, Role role) {
        return userDAO.searchUsers(query, role);
    }

    public User createUser(String fullName, String email, String password, Role role, String status, Integer actorUserId) throws AppException {
        if (!ValidationUtil.isNotEmpty(fullName)) {
            throw new AppException("Full name is required.");
        }
        String trimmedName = fullName.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 100) {
            throw new AppException("Full name must be between 2 and 100 characters.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new AppException("Please provide a valid email address.");
        }
        if (!ValidationUtil.isStrongPassword(password)) {
            throw new AppException("Password must be at least 8 characters long.");
        }
        if (userDAO.emailExists(email.trim().toLowerCase(), null)) {
            throw new AppException("A user with this email address already exists.");
        }

        Role assignedRole = role != null ? role : Role.TEAM_MEMBER;
        String userStatus = ValidationUtil.isNotEmpty(status) ? status.toUpperCase() : "ACTIVE";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        User user = UserFactory.createUser(0, trimmedName, email.trim().toLowerCase(), hashedPassword, assignedRole, userStatus);
        User saved = userDAO.save(user);

        activityDAO.log(actorUserId, "USER_CREATE", "Created user " + saved.getFullName() + " (" + saved.getEmail() + ") with role " + assignedRole.name(), "USER", saved.getId());
        return saved;
    }

    public void updateUser(int userId, String fullName, String email, Role role, String status, Integer actorUserId) throws AppException {
        User existing = getUserById(userId);

        // Prevent admin from changing their own role away from ADMIN or deactivating themselves
        if (actorUserId != null && actorUserId == userId) {
            if (existing.getRole() == Role.ADMIN && role != null && role != Role.ADMIN) {
                throw new AppException("Admins cannot change their own role away from Administrator.");
            }
            if ("ACTIVE".equalsIgnoreCase(existing.getStatus()) && status != null && !"ACTIVE".equalsIgnoreCase(status.trim())) {
                throw new AppException("Admins cannot deactivate their own account.");
            }
        }

        // Protection for last remaining active administrator
        if (existing.getRole() == Role.ADMIN && "ACTIVE".equalsIgnoreCase(existing.getStatus())) {
            boolean demoting = (role != null && role != Role.ADMIN);
            boolean deactivating = (status != null && !"ACTIVE".equalsIgnoreCase(status.trim()));
            if (demoting || deactivating) {
                if (userDAO.countActiveByRole(Role.ADMIN) <= 1) {
                    throw new AppException("Cannot demote or deactivate the last remaining active administrator.");
                }
            }
        }

        if (!ValidationUtil.isNotEmpty(fullName)) {
            throw new AppException("Full name is required.");
        }
        String trimmedName = fullName.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 100) {
            throw new AppException("Full name must be between 2 and 100 characters.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new AppException("Please provide a valid email address.");
        }
        if (userDAO.emailExists(email.trim().toLowerCase(), userId)) {
            throw new AppException("Another user is already registered with this email address.");
        }

        existing.setFullName(trimmedName);
        existing.setEmail(email.trim().toLowerCase());
        if (role != null) {
            existing.setRole(role);
        }
        if (ValidationUtil.isNotEmpty(status)) {
            existing.setStatus(status.toUpperCase());
        }

        boolean updated = userDAO.update(existing);
        if (!updated) {
            throw new AppException("Failed to update user.");
        }

        activityDAO.log(actorUserId, "USER_UPDATE", "Updated user details for: " + existing.getFullName(), "USER", userId);
    }

    public void updateProfile(int userId, String fullName, String email) throws AppException {
        User existing = getUserById(userId);

        if (!ValidationUtil.isNotEmpty(fullName)) {
            throw new AppException("Full name is required.");
        }
        String trimmedName = fullName.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 100) {
            throw new AppException("Full name must be between 2 and 100 characters.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new AppException("Please provide a valid email address.");
        }
        if (userDAO.emailExists(email.trim().toLowerCase(), userId)) {
            throw new AppException("Another user is already registered with this email address.");
        }

        existing.setFullName(trimmedName);
        existing.setEmail(email.trim().toLowerCase());

        boolean updated = userDAO.update(existing);
        if (!updated) {
            throw new AppException("Failed to update profile.");
        }

        activityDAO.log(userId, "PROFILE_UPDATE", existing.getFullName() + " updated their personal profile.", "USER", userId);
    }

    public void deleteUser(int userId, Integer actorUserId) throws AppException {
        User existing = getUserById(userId);

        // Prevent self deletion
        if (actorUserId != null && actorUserId == userId) {
            throw new AppException("You cannot delete your own account.");
        }

        // Protection for last remaining active administrator
        if (existing.getRole() == Role.ADMIN && "ACTIVE".equalsIgnoreCase(existing.getStatus())) {
            if (userDAO.countActiveByRole(Role.ADMIN) <= 1) {
                throw new AppException("Cannot delete the last remaining active administrator.");
            }
        }

        boolean deleted = userDAO.deleteById(userId);
        if (!deleted) {
            throw new AppException("Could not delete user. The user may be linked to active projects or tasks.");
        }

        activityDAO.log(actorUserId, "USER_DELETE", "Deleted user: " + existing.getFullName() + " (" + existing.getEmail() + ")", "USER", userId);
    }
}
