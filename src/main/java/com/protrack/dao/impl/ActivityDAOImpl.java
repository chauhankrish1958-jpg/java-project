package com.protrack.dao.impl;

import com.protrack.dao.ActivityDAO;
import com.protrack.exception.DatabaseOperationException;
import com.protrack.model.ActivityLog;
import com.protrack.model.Role;
import com.protrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ActivityDAO.
 */
public class ActivityDAOImpl implements ActivityDAO {

    private static final String BASE_SELECT =
            "SELECT l.*, u.full_name AS user_name, u.email AS user_email, u.role AS user_role " +
            "FROM activity_logs l " +
            "LEFT JOIN users u ON l.user_id = u.user_id ";

    private ActivityLog mapRow(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getInt("log_id"));

        int uid = rs.getInt("user_id");
        if (rs.wasNull()) {
            log.setUserId(null);
        } else {
            log.setUserId(uid);
        }

        log.setAction(rs.getString("action"));
        log.setDetails(rs.getString("details"));
        log.setEntityType(rs.getString("entity_type"));

        int eid = rs.getInt("entity_id");
        if (rs.wasNull()) {
            log.setEntityId(null);
        } else {
            log.setEntityId(eid);
        }

        log.setCreatedAt(rs.getTimestamp("created_at"));
        log.setUserName(rs.getString("user_name"));
        log.setUserEmail(rs.getString("user_email"));
        String roleStr = rs.getString("user_role");
        if (roleStr != null) {
            log.setUserRole(Role.fromString(roleStr));
        }

        return log;
    }

    @Override
    public Optional<ActivityLog> findById(Integer id) {
        String sql = BASE_SELECT + " WHERE l.log_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to find activity log by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<ActivityLog> findAll() {
        return findRecent(100);
    }

    @Override
    public List<ActivityLog> findRecent(int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY l.created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to fetch recent activity logs", e);
        }
        return list;
    }

    @Override
    public List<ActivityLog> findByUserId(int userId, int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE l.user_id = ? ORDER BY l.created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to fetch activity logs for user: " + userId, e);
        }
        return list;
    }

    @Override
    public List<ActivityLog> findByEntityType(String entityType, int entityId) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE l.entity_type = ? AND l.entity_id = ? ORDER BY l.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entityType);
            ps.setInt(2, entityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to fetch activity logs for entity", e);
        }
        return list;
    }

    @Override
    public ActivityLog save(ActivityLog log) {
        String sql = "INSERT INTO activity_logs (user_id, action, details, entity_type, entity_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null && log.getUserId() > 0) {
                ps.setInt(1, log.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, log.getAction());
            ps.setString(3, log.getDetails());
            ps.setString(4, log.getEntityType());
            if (log.getEntityId() != null && log.getEntityId() > 0) {
                ps.setInt(5, log.getEntityId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setId(rs.getInt(1));
                    }
                }
            }
            return log;
        } catch (SQLException e) {
            // Activity log failure should not necessarily crash user workflow, but log error
            System.err.println("[ActivityDAOImpl] Failed to insert activity log: " + e.getMessage());
            return log;
        }
    }

    @Override
    public void log(Integer userId, String action, String details, String entityType, Integer entityId) {
        save(new ActivityLog(userId, action, details, entityType, entityId));
    }

    @Override
    public boolean update(ActivityLog entity) {
        // Activity logs are append-only audit trails
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM activity_logs WHERE log_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to delete activity log: " + id, e);
        }
    }
}
