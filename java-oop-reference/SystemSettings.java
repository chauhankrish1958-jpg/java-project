package com.protrack.model;

/**
 * System Settings Configuration Entity.
 */
public class SystemSettings {
    private String systemName;
    private String organizationName;
    private boolean maintenanceMode;
    private String defaultTaskPriority;
    private boolean emailNotifications;
    private int sessionTimeout;
    private int maxProjectsPerManager;
    private String securityLevel;

    public SystemSettings() {
        this.systemName = "ProTrack Enterprise Suite";
        this.organizationName = "Apex Global Technology Solutions";
        this.maintenanceMode = false;
        this.defaultTaskPriority = "Medium";
        this.emailNotifications = true;
        this.sessionTimeout = 60;
        this.maxProjectsPerManager = 10;
        this.securityLevel = "High (MFA Enabled)";
    }

    // Getters and Setters
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public boolean isMaintenanceMode() { return maintenanceMode; }
    public void setMaintenanceMode(boolean maintenanceMode) { this.maintenanceMode = maintenanceMode; }

    public String getDefaultTaskPriority() { return defaultTaskPriority; }
    public void setDefaultTaskPriority(String defaultTaskPriority) { this.defaultTaskPriority = defaultTaskPriority; }

    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public int getSessionTimeout() { return sessionTimeout; }
    public void setSessionTimeout(int sessionTimeout) { this.sessionTimeout = sessionTimeout; }

    public int getMaxProjectsPerManager() { return maxProjectsPerManager; }
    public void setMaxProjectsPerManager(int maxProjectsPerManager) { this.maxProjectsPerManager = maxProjectsPerManager; }

    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
}
