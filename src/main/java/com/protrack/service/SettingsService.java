package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.SettingsDAO;
import com.protrack.dao.impl.ActivityDAOImpl;
import com.protrack.dao.impl.SettingsDAOImpl;
import com.protrack.exception.AppException;
import com.protrack.model.SystemSetting;

import java.util.List;
import java.util.Map;

/**
 * Service managing global system preferences and application branding.
 */
public class SettingsService {

    private final SettingsDAO settingsDAO;
    private final ActivityDAO activityDAO;

    public SettingsService() {
        this.settingsDAO = new SettingsDAOImpl();
        this.activityDAO = new ActivityDAOImpl();
    }

    public SettingsService(SettingsDAO settingsDAO, ActivityDAO activityDAO) {
        this.settingsDAO = settingsDAO;
        this.activityDAO = activityDAO;
    }

    public String getSetting(String key, String defaultValue) {
        return settingsDAO.getSetting(key, defaultValue);
    }

    public Map<String, String> getAllSettings() {
        return settingsDAO.getAllSettings();
    }

    public List<SystemSetting> getSettingsList() {
        return settingsDAO.findAll();
    }

    public void updateSetting(String key, String value, String description, Integer actorUserId) throws AppException {
        boolean updated = settingsDAO.setSetting(key, value, description);
        if (!updated) {
            throw new AppException("Failed to update system setting: " + key);
        }
        activityDAO.log(actorUserId, "SETTINGS_UPDATE", "Updated system setting [" + key + "] to: " + value, "SETTINGS", null);
    }
}
