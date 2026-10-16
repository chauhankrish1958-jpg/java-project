package com.protrack.service;

import com.protrack.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing User accounts, credentials, and role authorizations.
 * Demonstrates:
 * - Business Logic Layer
 * - Method Overloading (Compile-Time Polymorphism: searchUsers)
 */
public class UserService {
    private final Map<Integer, User> userMap;
    private int idCounter;

    public UserService() {
        this.userMap = new LinkedHashMap<>();
        this.idCounter = 1;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(userMap.get(id));
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null) return Optional.empty();
        return userMap.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    public List<User> getUsersByRole(Role role) {
        return userMap.values().stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }

    // Functionality 1 for Admin: User Creation
    public User createUser(String name, String email, Role role, String department, String title) {
        int newId = idCounter++;
        User user;
        if (role == Role.ADMIN) {
            user = new Admin(newId, name, email, department, title);
        } else if (role == Role.PROJECT_MANAGER) {
            user = new ProjectManager(newId, name, email, department, title);
        } else {
            user = new TeamMember(newId, name, email, department, title);
        }
        userMap.put(newId, user);
        return user;
    }

    // Functionality 1 for Admin: User Update
    public boolean updateUser(int id, String name, String email, Role role, String department, String title) {
        User user = userMap.get(id);
        if (user == null) return false;

        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        user.setDepartment(department);
        user.setTitle(title);
        return true;
    }

    // Functionality 3 for Team Member: Profile Update
    public boolean updateProfile(int id, String name, String email, String phone, String bio, String newPassword) {
        User user = userMap.get(id);
        if (user == null) return false;

        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setBio(bio);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(newPassword);
        }
        return true;
    }

    // Functionality 1 for Admin: User Deletion
    public boolean deleteUser(int id) {
        return userMap.remove(id) != null;
    }

    // --- Compile-Time Polymorphism (Method Overloading) ---
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) return getAllUsers();
        String q = query.toLowerCase().trim();
        return userMap.values().stream()
                .filter(u -> u.getName().toLowerCase().contains(q) ||
                             u.getEmail().toLowerCase().contains(q) ||
                             u.getDepartment().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<User> searchUsers(Role role) {
        return getUsersByRole(role);
    }

    public List<User> searchUsers(String query, Role role) {
        return searchUsers(query).stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }
}
