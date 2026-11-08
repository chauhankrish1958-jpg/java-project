package com.protrack.dao;

import com.protrack.model.Project;
import com.protrack.model.User;
import java.util.List;

/**
 * Project Data Access Object Interface.
 */
public interface ProjectDAO extends GenericDAO<Project, Integer> {

    List<Project> findByManagerId(int managerId);

    List<Project> findByMemberId(int memberId);

    List<Project> searchProjects(String query, String status, Integer managerId);

    boolean assignMemberToProject(int projectId, int userId);

    boolean removeMemberFromProject(int projectId, int userId);

    List<User> getProjectMembers(int projectId);

    boolean isMemberAssigned(int projectId, int userId);

    int countProjects();

    int countProjectsByStatus(String status);

    int countProjectsByManager(int managerId);

    /**
     * Demonstrates JDBC Transactions: atomic creation of a project and its initial member mappings.
     */
    Project saveWithMembers(Project project, List<Integer> memberIds);
}
