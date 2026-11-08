package com.protrack.dao.impl;

import com.protrack.dao.SettingsDAO;
import com.protrack.exception.DatabaseOperationException;
import com.protrack.model.SystemSetting;
import com.protrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of SettingsDAO.
 */
public class SettingsDAOImpl implements SettingsDAO {

    @Override
    public String getSetting(String key, String defaultValue) {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        } catch (SQLException e) {
            System.err.println("[SettingsDAOImpl] Error getting setting " + key + ": " + e.getMessage());
        }
        return defaultValue;
    }

    @Override
    public boolean setSetting(String key, String value, String description) {
        String sql = "INSERT INTO system_settings (setting_key, setting_value, description) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), " +
                     "description = COALESCE(VALUES(description), description)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.setString(3, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to save setting: " + key, e);
        }
    }

    @Override
    public Map<String, String> getAllSettings() {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT setting_key, setting_value FROM system_settings";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            System.err.println("[SettingsDAOImpl] Error fetching all settings: " + e.getMessage());
        }
        return map;
    }

    @Override
    public List<SystemSetting> findAll() {
        List<SystemSetting> list = new ArrayList<>();
        String sql = "SELECT * FROM system_settings ORDER BY setting_key ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SystemSetting s = new SystemSetting(
                        rs.getInt("setting_id"),
                        rs.getString("setting_key"),
                        rs.getString("setting_value"),
                        rs.getString("description")
                );
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(s);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to fetch settings list", e);
        }
        return list;
    }
}
