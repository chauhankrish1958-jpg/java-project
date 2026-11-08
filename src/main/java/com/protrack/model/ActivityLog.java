package com.protrack.model;

import com.protrack.util.DateUtil;
import java.sql.Timestamp;

/**
 * Activity Log entity for audit trail and recent activity tracking.
 */
public class ActivityLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String action;
    private String details;
    private String entityType;
    private Integer entityId;

    // Joined for display
    private String userName;
    private String userEmail;
    private Role userRole;

    public ActivityLog() {
        super();
    }

    public ActivityLog(Integer userId, String action, String details, String entityType, Integer entityId) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getFormattedCreatedAt() {
        return DateUtil.formatDisplayDateTime(this.createdAt);
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }
}
