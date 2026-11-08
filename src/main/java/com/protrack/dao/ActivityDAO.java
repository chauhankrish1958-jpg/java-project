package com.protrack.dao;

import com.protrack.model.ActivityLog;
import java.util.List;

/**
 * Activity Log Data Access Object Interface.
 */
public interface ActivityDAO extends GenericDAO<ActivityLog, Integer> {

    List<ActivityLog> findRecent(int limit);

    List<ActivityLog> findByUserId(int userId, int limit);

    List<ActivityLog> findByEntityType(String entityType, int entityId);

    void log(Integer userId, String action, String details, String entityType, Integer entityId);
}
