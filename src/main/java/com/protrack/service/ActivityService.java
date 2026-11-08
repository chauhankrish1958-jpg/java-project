package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.impl.ActivityDAOImpl;
import com.protrack.model.ActivityLog;

import java.util.List;

/**
 * Service managing system activity logs and audit records.
 */
public class ActivityService {

    private final ActivityDAO activityDAO;

    public ActivityService() {
        this.activityDAO = new ActivityDAOImpl();
    }

    public ActivityService(ActivityDAO activityDAO) {
        this.activityDAO = activityDAO;
    }

    public List<ActivityLog> getRecentActivities(int limit) {
        return activityDAO.findRecent(limit);
    }

    public List<ActivityLog> getUserActivities(int userId, int limit) {
        return activityDAO.findByUserId(userId, limit);
    }

    public void log(Integer userId, String action, String details, String entityType, Integer entityId) {
        activityDAO.log(userId, action, details, entityType, entityId);
    }
}
