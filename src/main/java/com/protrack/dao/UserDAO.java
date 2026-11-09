package com.protrack.dao;

import com.protrack.model.Role;
import com.protrack.model.User;
import java.util.List;
import java.util.Optional;

/**
 * User Data Access Object Interface.
 */
public interface UserDAO extends GenericDAO<User, Integer> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> searchUsers(String query, Role role);

    boolean updatePassword(int userId, String hashedPassword);

    int countUsers();

    int countActiveByRole(Role role);

    boolean emailExists(String email, Integer excludeUserId);
}

