package com.protrack.service;

import com.protrack.dao.ActivityDAO;
import com.protrack.dao.ProjectDAO;
import com.protrack.dao.impl.ActivityDAOImpl;
import com.protrack.dao.impl.ProjectDAOImpl;
import com.protrack.exception.AppException;
import com.protrack.exception.ProjectNotFoundException;
import com.protrack.model.Project;
import com.protrack.model.User;
import com.protrack.util.ValidationUtil;

import java.sql.Date;
import java.util.List;

/**
 * Service managing project lifecycle, member assignment, and project progress.
 */
public class ProjectService {

    private final ProjectDAO projectDAO;
    private final ActivityDAO activityDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAOImpl();
        this.activityDAO = new ActivityDAOImpl();
    }

    public ProjectService(ProjectDAO projectDAO, ActivityDAO activityDAO) {
        this.projectDAO = projectDAO;
        this.activityDAO = activityDAO;
    }

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    public Project getProjectById(int id) throws ProjectNotFoundException {
        return projectDAO.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + id));
    }

    public List<Project> getProjectsByManager(int managerId) {
        return projectDAO.findByManagerId(managerId);
    }

    public List<Project> getProjectsByMember(int memberId) {
        return projectDAO.findByMemberId(memberId);
    }

    public List<Project> searchProjects(String query, String status, Integer managerId) {
        return projectDAO.searchProjects(query, status, managerId);
    }

    public Project createProject(String title, String description, Date startDate, Date endDate,
                                 int managerId, String status, List<Integer> memberIds, Integer actorUserId) throws AppException {
        if (!ValidationUtil.isNotEmpty(title)) {
            throw new AppException("Project title is required.");
        }
        String trimmedTitle = title.trim();
        if (trimmedTitle.length() < 2 || trimmedTitle.length() > 150) {
            throw new AppException("Project title must be between 2 and 150 characters.");
        }
        String trimmedDesc = description != null ? description.trim() : "";

        if (startDate == null || endDate == null) {
            throw new AppException("Start date and end date are required.");
        }
        if (endDate.before(startDate)) {
            throw new AppException("End date cannot be earlier than start date.");
        }
        if (managerId <= 0) {
            throw new AppException("A valid project manager must be assigned.");
        }

        String projectStatus = ValidationUtil.isNotEmpty(status) ? status.toUpperCase() : "PLANNING";
        Project project = new Project(0, trimmedTitle, trimmedDesc, startDate, endDate, managerId, projectStatus);

        Project saved;
        if (memberIds != null && !memberIds.isEmpty()) {
            saved = projectDAO.saveWithMembers(project, memberIds);
        } else {
            saved = projectDAO.save(project);
        }

        activityDAO.log(actorUserId, "PROJECT_CREATE", "Created project: \"" + saved.getTitle() + "\"", "PROJECT", saved.getId());
        return saved;
    }

    public void updateProject(int projectId, String title, String description, Date startDate, Date endDate,
                              int managerId, String status, Integer actorUserId) throws AppException {
        Project existing = getProjectById(projectId);

        if (!ValidationUtil.isNotEmpty(title)) {
            throw new AppException("Project title is required.");
        }
        String trimmedTitle = title.trim();
        if (trimmedTitle.length() < 2 || trimmedTitle.length() > 150) {
            throw new AppException("Project title must be between 2 and 150 characters.");
        }
        String trimmedDesc = description != null ? description.trim() : "";

        if (startDate == null || endDate == null) {
            throw new AppException("Start date and end date are required.");
        }
        if (endDate.before(startDate)) {
            throw new AppException("End date cannot be earlier than start date.");
        }
        if (managerId <= 0) {
            throw new AppException("A valid project manager must be assigned.");
        }

        existing.setTitle(trimmedTitle);
        existing.setDescription(trimmedDesc);
        existing.setStartDate(startDate);
        existing.setEndDate(endDate);
        existing.setManagerId(managerId);
        if (ValidationUtil.isNotEmpty(status)) {
            existing.setStatus(status.toUpperCase());
        }

        boolean updated = projectDAO.update(existing);
        if (!updated) {
            throw new AppException("Failed to update project.");
        }

        activityDAO.log(actorUserId, "PROJECT_UPDATE", "Updated project details for: \"" + existing.getTitle() + "\"", "PROJECT", projectId);
    }

    public void deleteProject(int projectId, Integer actorUserId) throws AppException {
        Project existing = getProjectById(projectId);

        boolean deleted = projectDAO.deleteById(projectId);
        if (!deleted) {
            throw new AppException("Failed to delete project.");
        }

        activityDAO.log(actorUserId, "PROJECT_DELETE", "Deleted project: \"" + existing.getTitle() + "\"", "PROJECT", projectId);
    }

    public List<User> getProjectMembers(int projectId) {
        return projectDAO.getProjectMembers(projectId);
    }

    public void assignMember(int projectId, int userId, Integer actorUserId) throws AppException {
        // Validate project exists
        Project project = getProjectById(projectId);

        boolean assigned = projectDAO.assignMemberToProject(projectId, userId);
        if (assigned) {
            activityDAO.log(actorUserId, "PROJECT_ASSIGN_MEMBER", "Assigned team member (ID: " + userId + ") to project \"" + project.getTitle() + "\"", "PROJECT", projectId);
        }
    }

    public void removeMember(int projectId, int userId, Integer actorUserId) throws AppException {
        Project project = getProjectById(projectId);

        boolean removed = projectDAO.removeMemberFromProject(projectId, userId);
        if (removed) {
            activityDAO.log(actorUserId, "PROJECT_REMOVE_MEMBER", "Removed team member (ID: " + userId + ") from project \"" + project.getTitle() + "\"", "PROJECT", projectId);
        }
    }
}
