package com.protrack.dao;

import com.protrack.model.Task;
import com.protrack.model.TaskPriority;
import com.protrack.model.TaskStatus;
import java.util.List;
import java.util.Map;

/**
 * Task Data Access Object Interface.
 */
public interface TaskDAO extends GenericDAO<Task, Integer> {

    List<Task> findByProjectId(int projectId);

    List<Task> findByAssignedTo(int userId);

    List<Task> findByAssignedToAndFilters(int userId, String status, String priority);

    List<Task> searchTasks(String query, Integer projectId, Integer assignedTo, TaskStatus status, TaskPriority priority);

    List<Task> searchTasks(String query, Integer projectId, Integer assignedTo, TaskStatus status, TaskPriority priority, Integer managerId);

    boolean updateStatus(int taskId, TaskStatus newStatus);

    boolean updatePriority(int taskId, TaskPriority newPriority);

    int countTotalTasks();

    int countTasksByStatus(TaskStatus status);

    int countTasksByPriority(TaskPriority priority);

    int countOverdueTasks();

    int countTasksForMember(int userId, TaskStatus status);

    int countOverdueTasksForMember(int userId);

    List<Task> findOverdueTasks(Integer userId);

    List<Task> findTasksDueToday(Integer userId);

    Map<TaskStatus, Integer> getStatusDistributionByProject(int projectId);

    Map<TaskPriority, Integer> getPriorityDistribution();
}
