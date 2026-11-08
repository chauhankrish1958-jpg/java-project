package com.protrack.dao;

import com.protrack.model.SystemSetting;
import java.util.List;
import java.util.Map;

/**
 * Settings Data Access Object Interface.
 */
public interface SettingsDAO {

    String getSetting(String key, String defaultValue);

    boolean setSetting(String key, String value, String description);

    Map<String, String> getAllSettings();

    List<SystemSetting> findAll();
}
