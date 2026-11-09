package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.SettingsDAO;
import com.protrack.dao.UserDAO;
import com.protrack.dao.impl.ActivityDAOImpl;
import com.protrack.dao.impl.SettingsDAOImpl;
import com.protrack.dao.impl.UserDAOImpl;
import com.protrack.exception.AppException;
import com.protrack.exception.InvalidCredentialsException;
import com.protrack.exception.UserNotFoundException;
import com.protrack.model.Role;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import com.protrack.util.BCrypt;
import com.protrack.util.ValidationUtil;

import java.util.Optional;

/**
 * Authentication and authorization service managing logins, registration and credentials.
 */
public class AuthService {

    private final UserDAO userDAO;
    private final ActivityDAO activityDAO;
    private final SettingsDAO settingsDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
        this.activityDAO = new ActivityDAOImpl();
        this.settingsDAO = new SettingsDAOImpl();
    }

    public AuthService(UserDAO userDAO, ActivityDAO activityDAO, SettingsDAO settingsDAO) {
        this.userDAO = userDAO;
        this.activityDAO = activityDAO;
        this.settingsDAO = settingsDAO;
    }

    /**
     * Authenticates user with email and plaintext password.
     */
    public User authenticate(String email, String password) throws AppException {
        if (!ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password)) {
            throw new InvalidCredentialsException("Email and password are required.");
        }

        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            throw new AppException("Your account has been deactivated. Please contact an administrator.");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        // Record successful login
        activityDAO.log(user.getId(), "LOGIN", user.getFullName() + " logged in successfully.", "AUTH", user.getId());
        return user;
    }

    /**
     * Registers a new user. Default role is TEAM_MEMBER unless specified.
     */
    public User register(String fullName, String email, String password, Role role) throws AppException {
        String allowReg = settingsDAO.getSetting("allow_user_registration", "true");
        if (!"true".equalsIgnoreCase(allowReg)) {
            throw new AppException("Public registration is currently disabled by administrator.");
        }

        String cleanFullName = ValidationUtil.sanitize(fullName);
        if (!ValidationUtil.isNotEmpty(cleanFullName)) {
            throw new AppException("Full name is required.");
        }

        if (!ValidationUtil.isValidEmail(email)) {
            throw new AppException("Please provide a valid email address.");
        }

        if (!ValidationUtil.isStrongPassword(password)) {
            throw new AppException("Password must be at least 8 characters long.");
        }

        if (userDAO.emailExists(email.trim().toLowerCase(), null)) {
            throw new AppException("An account with this email address already exists.");
        }

        Role assignedRole = (role != null) ? role : Role.TEAM_MEMBER;
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        User user = UserFactory.createUser(0, cleanFullName, email.trim().toLowerCase(), hashedPassword, assignedRole, "ACTIVE");
        User savedUser = userDAO.save(user);

        activityDAO.log(savedUser.getId(), "USER_REGISTER", "New user registered: " + savedUser.getFullName() + " (" + assignedRole.getDisplayName() + ")", "USER", savedUser.getId());
        return savedUser;
    }

    /**
     * Changes user password verifying the current password first.
     */
    public void changePassword(int userId, String currentPassword, String newPassword) throws AppException {
        if (!ValidationUtil.isNotEmpty(currentPassword) || !ValidationUtil.isNotEmpty(newPassword)) {
            throw new AppException("Current and new passwords cannot be empty.");
        }

        if (!ValidationUtil.isStrongPassword(newPassword)) {
            throw new AppException("New password must be at least 8 characters long.");
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password does not match.");
        }

        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(10));
        boolean success = userDAO.updatePassword(userId, hashedNewPassword);
        if (!success) {
            throw new AppException("Failed to update password.");
        }

        activityDAO.log(userId, "PASSWORD_CHANGE", user.getFullName() + " updated their account password.", "USER", userId);
    }
}
