package com.protrack.model;

import java.sql.Timestamp;

/**
 * System Setting entity storing key-value system preferences.
 */
public class SystemSetting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String settingKey;
    private String settingValue;
    private String description;

    public SystemSetting() {
        super();
    }

    public SystemSetting(int id, String settingKey, String settingValue, String description) {
        super(id);
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
