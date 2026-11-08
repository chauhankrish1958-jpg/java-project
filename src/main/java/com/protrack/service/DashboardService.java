package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.ProjectDAO;
import com.protrack.dao.TaskDAO;
import com.protrack.dao.UserDAO;
import com.protrack.dao.impl.ActivityDAOImpl;
import com.protrack.dao.impl.ProjectDAOImpl;
import com.protrack.dao.impl.TaskDAOImpl;
import com.protrack.dao.impl.UserDAOImpl;
import com.protrack.model.ActivityLog;
import com.protrack.model.Project;
import com.protrack.model.Task;
import com.protrack.model.TaskPriority;
import com.protrack.model.TaskStatus;
import com.protrack.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service calculating live performance metrics and analytics for role-based dashboards.
 */
public class DashboardService {

    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final ActivityDAO activityDAO;

    public DashboardService() {
        this.userDAO = new UserDAOImpl();
        this.projectDAO = new ProjectDAOImpl();
        this.taskDAO = new TaskDAOImpl();
        this.activityDAO = new ActivityDAOImpl();
    }

    public DashboardService(UserDAO userDAO, ProjectDAO projectDAO, TaskDAO taskDAO, ActivityDAO activityDAO) {
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
        this.activityDAO = activityDAO;
    }

    /**
     * Aggregates real-time operational statistics for the Admin Dashboard.
     */
    public Map<String, Object> getAdminDashboardData() {
        Map<String, Object> data = new HashMap<>();

        int totalUsers = userDAO.countUsers();
        int totalProjects = projectDAO.countProjects();
        int activeProjects = projectDAO.countProjectsByStatus("IN_PROGRESS") + projectDAO.countProjectsByStatus("PLANNING");
        int completedProjects = projectDAO.countProjectsByStatus("COMPLETED");
        int totalTasks = taskDAO.countTotalTasks();
        int completedTasks = taskDAO.countTasksByStatus(TaskStatus.COMPLETED);
        int overdueTasks = taskDAO.countOverdueTasks();

        data.put("totalUsers", totalUsers);
        data.put("totalProjects", totalProjects);
        data.put("activeProjects", activeProjects);
        data.put("completedProjects", completedProjects);
        data.put("totalTasks", totalTasks);
        data.put("completedTasks", completedTasks);
        data.put("overdueTasks", overdueTasks);

        // Calculate overall system completion percentage
        int overallCompletion = totalTasks > 0 ? (int) Math.round(((double) completedTasks / totalTasks) * 100) : 0;
        data.put("overallCompletion", overallCompletion);

        // Project Status Distribution
        Map<String, Integer> projectStatusCounts = new HashMap<>();
        projectStatusCounts.put("PLANNING", projectDAO.countProjectsByStatus("PLANNING"));
        projectStatusCounts.put("IN_PROGRESS", projectDAO.countProjectsByStatus("IN_PROGRESS"));
        projectStatusCounts.put("ON_HOLD", projectDAO.countProjectsByStatus("ON_HOLD"));
        projectStatusCounts.put("COMPLETED", projectDAO.countProjectsByStatus("COMPLETED"));
        projectStatusCounts.put("CANCELLED", projectDAO.countProjectsByStatus("CANCELLED"));
        data.put("projectStatusCounts", projectStatusCounts);

        // Task Priority Distribution
        Map<TaskPriority, Integer> priorityDistribution = taskDAO.getPriorityDistribution();
        data.put("priorityDistribution", priorityDistribution);

        // Recent Projects (latest 5)
        List<Project> allProjects = projectDAO.findAll();
        data.put("recentProjects", allProjects.stream().limit(5).toList());

        // Recent Activity Logs (latest 8)
        List<ActivityLog> recentActivities = activityDAO.findRecent(8);
        data.put("recentActivities", recentActivities);

        // Overdue Tasks
        List<Task> overdueList = taskDAO.findOverdueTasks(null);
        data.put("overdueTasksList", overdueList);

        return data;
    }

    /**
     * Aggregates real-time statistics for a Project Manager's Dashboard.
     */
    public Map<String, Object> getManagerDashboardData(int managerId) {
        Map<String, Object> data = new HashMap<>();

        List<Project> managedProjects = projectDAO.findByManagerId(managerId);
        data.put("managedProjects", managedProjects);
        data.put("managedProjectsCount", managedProjects.size());

        int totalTasks = 0;
        int completedTasks = 0;
        int overdueTasks = 0;

        for (Project p : managedProjects) {
            totalTasks += p.getTotalTasks();
            completedTasks += p.getCompletedTasks();
        }

        int pendingTasks = totalTasks - completedTasks;
        int managerCompletionRate = totalTasks > 0 ? (int) Math.round(((double) completedTasks / totalTasks) * 100) : 0;

        data.put("totalTasks", totalTasks);
        data.put("completedTasks", completedTasks);
        data.put("pendingTasks", pendingTasks);
        data.put("managerCompletionRate", managerCompletionRate);

        // All tasks under this manager's projects
        List<Task> allManagerTasks = taskDAO.findAll().stream()
                .filter(t -> managedProjects.stream().anyMatch(p -> p.getId() == t.getProjectId()))
                .toList();

        for (Task t : allManagerTasks) {
            if (t.isOverdue()) {
                overdueTasks++;
            }
        }
        data.put("overdueTasks", overdueTasks);
        data.put("recentTasks", allManagerTasks.stream().limit(6).toList());

        // Urgent alerts: tasks due today or overdue
        List<Task> dueToday = allManagerTasks.stream().filter(Task::isDueToday).toList();
        List<Task> overdueList = allManagerTasks.stream().filter(Task::isOverdue).toList();
        data.put("tasksDueToday", dueToday);
        data.put("overdueTasksList", overdueList);

        // Recent Activities for manager
        List<ActivityLog> activities = activityDAO.findRecent(10);
        data.put("recentActivities", activities);

        return data;
    }

    /**
     * Aggregates real-time statistics for a Team Member's Dashboard.
     */
    public Map<String, Object> getMemberDashboardData(int memberId) {
        Map<String, Object> data = new HashMap<>();

        List<Task> myTasks = taskDAO.findByAssignedTo(memberId);
        data.put("myTasks", myTasks);

        int totalTasks = myTasks.size();
        int completedTasks = 0;
        int inProgressTasks = 0;
        int reviewTasks = 0;
        int todoTasks = 0;
        int overdueTasks = 0;

        for (Task t : myTasks) {
            if (t.getStatus() == TaskStatus.COMPLETED) {
                completedTasks++;
            } else if (t.getStatus() == TaskStatus.IN_PROGRESS) {
                inProgressTasks++;
            } else if (t.getStatus() == TaskStatus.REVIEW) {
                reviewTasks++;
            } else {
                todoTasks++;
            }

            if (t.isOverdue()) {
                overdueTasks++;
            }
        }

        data.put("totalTasks", totalTasks);
        data.put("completedTasks", completedTasks);
        data.put("inProgressTasks", inProgressTasks);
        data.put("reviewTasks", reviewTasks);
        data.put("todoTasks", todoTasks);
        data.put("overdueTasks", overdueTasks);

        int memberCompletionRate = totalTasks > 0 ? (int) Math.round(((double) completedTasks / totalTasks) * 100) : 0;
        data.put("memberCompletionRate", memberCompletionRate);

        // My projects
        List<Project> myProjects = projectDAO.findByMemberId(memberId);
        data.put("myProjects", myProjects);
        data.put("assignedProjectsCount", myProjects.size());

        // Urgent alerts for member
        List<Task> dueToday = taskDAO.findTasksDueToday(memberId);
        List<Task> overdueList = taskDAO.findOverdueTasks(memberId);
        data.put("tasksDueToday", dueToday);
        data.put("overdueTasksList", overdueList);

        // Recent activities
        List<ActivityLog> activities = activityDAO.findRecent(10);
        data.put("recentActivities", activities);

        return data;
    }
}
