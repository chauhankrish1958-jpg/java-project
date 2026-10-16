package com.protrack.service;

import com.protrack.model.Priority;
import com.protrack.model.SystemSettings;

/**
 * Service managing global system configurations.
 * Demonstrates:
 * - Admin Functionality 3: System Settings
 */
public class SystemSettingService {
    private final SystemSettings settings;

    public SystemSettingService() {
        this.settings = new SystemSettings();
    }

    public SystemSettings getSettings() {
        return settings;
    }

    public void updateSettings(String systemName, String orgName, boolean maintenance,
                               Priority defaultPriority, boolean emailAlerts,
                               int sessionTimeout, int maxProjects, String securityLevel) {
        settings.setSystemName(systemName);
        settings.setOrganizationName(orgName);
        settings.setMaintenanceMode(maintenance);
        settings.setDefaultTaskPriority(defaultPriority);
        settings.setEmailNotifications(emailAlerts);
        settings.setSessionTimeoutMinutes(sessionTimeout);
        settings.setMaxProjectsPerManager(maxProjects);
        settings.setSecurityLevel(securityLevel);
    }
}
