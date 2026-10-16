package com.protrack.model;

/**
 * System Settings Configuration Entity.
 * Demonstrates Singleton / Encapsulated Configuration pattern.
 */
public class SystemSettings {
    private String systemName;
    private String organizationName;
    private boolean maintenanceMode;
    private Priority defaultTaskPriority;
    private boolean emailNotifications;
    private int sessionTimeoutMinutes;
    private int maxProjectsPerManager;
    private String securityLevel;

    public SystemSettings() {
        this.systemName = "ProTrack Enterprise Suite";
        this.organizationName = "Apex Global Technology Solutions";
        this.maintenanceMode = false;
        this.defaultTaskPriority = Priority.MEDIUM;
        this.emailNotifications = true;
        this.sessionTimeoutMinutes = 60;
        this.maxProjectsPerManager = 10;
        this.securityLevel = "High (MFA Enabled)";
    }

    // Getters and Setters
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) {
        if (systemName != null && !systemName.trim().isEmpty()) {
            this.systemName = systemName.trim();
        }
    }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) {
        if (organizationName != null && !organizationName.trim().isEmpty()) {
            this.organizationName = organizationName.trim();
        }
    }

    public boolean isMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public Priority getDefaultTaskPriority() { return defaultTaskPriority; }
    public void setDefaultTaskPriority(Priority defaultTaskPriority) {
        if (defaultTaskPriority != null) this.defaultTaskPriority = defaultTaskPriority;
    }

    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public int getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    public void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = Math.max(5, sessionTimeoutMinutes);
    }

    public int getMaxProjectsPerManager() { return maxProjectsPerManager; }
    public void setMaxProjectsPerManager(int maxProjectsPerManager) {
        this.maxProjectsPerManager = Math.max(1, maxProjectsPerManager);
    }

    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) {
        if (securityLevel != null) this.securityLevel = securityLevel;
    }

    @Override
    public String toString() {
        return String.format("Settings: %s (%s) | Maintenance: %s | Timeout: %d mins | Security: %s",
                systemName, organizationName, maintenanceMode ? "ENABLED" : "DISABLED",
                sessionTimeoutMinutes, securityLevel);
    }
}
