package com.protrack.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ActivityLog Entity Class representing audit log events.
 */
public class ActivityLog {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private long id;
    private String timestamp;
    private String userName;
    private String userRole;
    private String action;
    private String type; // "task", "project", "system", "general"

    public ActivityLog(long id, String userName, String userRole, String action, String type) {
        this.id = id;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
        this.userName = userName;
        this.userRole = userRole;
        this.action = action;
        this.type = type != null ? type : "general";
    }

    public long getId() { return id; }
    public String getTimestamp() { return timestamp; }
    public String getUserName() { return userName; }
    public String getUserRole() { return userRole; }
    public String getAction() { return action; }
    public String getType() { return type; }

    @Override
    public String toString() {
        return String.format("[%s] [%s - %s] %s", timestamp, userName, userRole, action);
    }
}
