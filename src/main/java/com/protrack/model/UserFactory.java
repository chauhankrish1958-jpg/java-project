package com.protrack.model;

/**
 * Factory pattern implementation for polymorphic User instantiation based on Role.
 */
public class UserFactory {

    public static User createUser(Role role) {
        if (role == null) {
            return new TeamMember();
        }
        return switch (role) {
            case ADMIN -> new Admin();
            case PROJECT_MANAGER -> new ProjectManager();
            case TEAM_MEMBER -> new TeamMember();
        };
    }

    public static User createUser(int id, String fullName, String email, String password, Role role, String status) {
        if (role == null) {
            return new TeamMember(id, fullName, email, password, status);
        }
        return switch (role) {
            case ADMIN -> new Admin(id, fullName, email, password, status);
            case PROJECT_MANAGER -> new ProjectManager(id, fullName, email, password, status);
            case TEAM_MEMBER -> new TeamMember(id, fullName, email, password, status);
        };
    }
}
