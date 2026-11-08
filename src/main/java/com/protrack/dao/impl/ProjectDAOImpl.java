package com.protrack.dao.impl;

import com.protrack.dao.ProjectDAO;
import com.protrack.exception.DatabaseOperationException;
import com.protrack.model.Project;
import com.protrack.model.Role;
import com.protrack.model.User;
import com.protrack.model.UserFactory;
import com.protrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ProjectDAO with aggregation and transaction support.
 */
public class ProjectDAOImpl implements ProjectDAO {

    private Project mapRowToProject(ResultSet rs) throws SQLException {
        Project p = new Project();
        p.setId(rs.getInt("project_id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setStartDate(rs.getDate("start_date"));
        p.setEndDate(rs.getDate("end_date"));
        p.setManagerId(rs.getInt("manager_id"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Joined fields if available
        try {
            p.setManagerName(rs.getString("manager_name"));
            p.setManagerEmail(rs.getString("manager_email"));
        } catch (SQLException ignored) {}

        try {
            p.setTotalTasks(rs.getInt("total_tasks"));
            p.setCompletedTasks(rs.getInt("completed_tasks"));
            p.setPendingTasks(p.getTotalTasks() - p.getCompletedTasks());
        } catch (SQLException ignored) {}

        return p;
    }

    private static final String BASE_SELECT =
            "SELECT p.*, u.full_name AS manager_name, u.email AS manager_email, " +
            "  (SELECT COUNT(*) FROM tasks t WHERE t.project_id = p.project_id) AS total_tasks, " +
            "  (SELECT COUNT(*) FROM tasks t WHERE t.project_id = p.project_id AND t.status = 'COMPLETED') AS completed_tasks " +
            "FROM projects p " +
            "JOIN users u ON p.manager_id = u.user_id ";

    @Override
    public Optional<Project> findById(Integer id) {
        String sql = BASE_SELECT + " WHERE p.project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToProject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to find project by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY p.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToProject(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to fetch all projects", e);
        }
        return list;
    }

    @Override
    public List<Project> findByManagerId(int managerId) {
        List<Project> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE p.manager_id = ? ORDER BY p.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to fetch projects by manager: " + managerId, e);
        }
        return list;
    }

    @Override
    public List<Project> findByMemberId(int memberId) {
        List<Project> list = new ArrayList<>();
        String sql = BASE_SELECT +
                " WHERE p.project_id IN (SELECT project_id FROM project_members WHERE user_id = ?) " +
                "    OR p.project_id IN (SELECT project_id FROM tasks WHERE assigned_to = ?) " +
                " ORDER BY p.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to fetch projects for member: " + memberId, e);
        }
        return list;
    }

    @Override
    public List<Project> searchProjects(String query, String status, Integer managerId) {
        List<Project> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (LOWER(p.title) LIKE ? OR LOWER(p.description) LIKE ?) ");
            String wild = "%" + query.trim().toLowerCase() + "%";
            params.add(wild);
            params.add(wild);
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND p.status = ? ");
            params.add(status.trim().toUpperCase());
        }

        if (managerId != null && managerId > 0) {
            sql.append(" AND p.manager_id = ? ");
            params.add(managerId);
        }

        sql.append(" ORDER BY p.created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProject(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to search projects", e);
        }
        return list;
    }

    @Override
    public Project save(Project project) {
        String sql = "INSERT INTO projects (title, description, start_date, end_date, manager_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getTitle());
            ps.setString(2, project.getDescription());
            ps.setDate(3, project.getStartDate());
            ps.setDate(4, project.getEndDate());
            ps.setInt(5, project.getManagerId());
            ps.setString(6, project.getStatus() != null ? project.getStatus() : "PLANNING");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        project.setId(rs.getInt(1));
                    }
                }
            }
            return project;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to insert project: " + project.getTitle(), e);
        }
    }

    @Override
    public Project saveWithMembers(Project project, List<Integer> memberIds) {
        Connection conn = null;
        PreparedStatement psProj = null;
        PreparedStatement psMem = null;
        try {
            conn = DBConnection.getConnection();
            // Start transaction
            conn.setAutoCommit(false);

            String insertProjSql = "INSERT INTO projects (title, description, start_date, end_date, manager_id, status) VALUES (?, ?, ?, ?, ?, ?)";
            psProj = conn.prepareStatement(insertProjSql, Statement.RETURN_GENERATED_KEYS);
            psProj.setString(1, project.getTitle());
            psProj.setString(2, project.getDescription());
            psProj.setDate(3, project.getStartDate());
            psProj.setDate(4, project.getEndDate());
            psProj.setInt(5, project.getManagerId());
            psProj.setString(6, project.getStatus() != null ? project.getStatus() : "PLANNING");
            psProj.executeUpdate();

            int generatedProjectId;
            try (ResultSet rs = psProj.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedProjectId = rs.getInt(1);
                    project.setId(generatedProjectId);
                } else {
                    throw new SQLException("Failed to retrieve generated project ID");
                }
            }

            if (memberIds != null && !memberIds.isEmpty()) {
                String insertMemSql = "INSERT IGNORE INTO project_members (project_id, user_id) VALUES (?, ?)";
                psMem = conn.prepareStatement(insertMemSql);
                for (Integer uid : memberIds) {
                    if (uid != null && uid > 0) {
                        psMem.setInt(1, generatedProjectId);
                        psMem.setInt(2, uid);
                        psMem.addBatch();
                    }
                }
                psMem.executeBatch();
            }

            // Commit transaction
            conn.commit();
            return project;
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw new DatabaseOperationException("Transaction rolled back while creating project with members", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {}
            }
            DBConnection.close(conn, psProj);
            if (psMem != null) {
                try {
                    psMem.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    @Override
    public boolean update(Project project) {
        String sql = "UPDATE projects SET title = ?, description = ?, start_date = ?, end_date = ?, manager_id = ?, status = ? WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getTitle());
            ps.setString(2, project.getDescription());
            ps.setDate(3, project.getStartDate());
            ps.setDate(4, project.getEndDate());
            ps.setInt(5, project.getManagerId());
            ps.setString(6, project.getStatus());
            ps.setInt(7, project.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to update project: " + project.getId(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM projects WHERE project_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to delete project: " + id, e);
        }
    }

    @Override
    public boolean assignMemberToProject(int projectId, int userId) {
        String sql = "INSERT IGNORE INTO project_members (project_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to assign member to project", e);
        }
    }

    @Override
    public boolean removeMemberFromProject(int projectId, int userId) {
        String sql = "DELETE FROM project_members WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to remove member from project", e);
        }
    }

    @Override
    public List<User> getProjectMembers(int projectId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.* FROM users u " +
                     "JOIN project_members pm ON u.user_id = pm.user_id " +
                     "WHERE pm.project_id = ? ORDER BY u.full_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String fullName = rs.getString("full_name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    Role role = Role.fromString(rs.getString("role"));
                    String status = rs.getString("status");

                    User u = UserFactory.createUser(id, fullName, email, password, role, status);
                    u.setCreatedAt(rs.getTimestamp("created_at"));
                    u.setUpdatedAt(rs.getTimestamp("updated_at"));
                    list.add(u);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to get project members for project: " + projectId, e);
        }
        return list;
    }

    @Override
    public boolean isMemberAssigned(int projectId, int userId) {
        String sql = "SELECT COUNT(*) FROM project_members WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to check if member is assigned", e);
        }
        return false;
    }

    @Override
    public int countProjects() {
        String sql = "SELECT COUNT(*) FROM projects";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to count projects", e);
        }
        return 0;
    }

    @Override
    public int countProjectsByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM projects WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to count projects by status: " + status, e);
        }
        return 0;
    }

    @Override
    public int countProjectsByManager(int managerId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE manager_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to count projects by manager: " + managerId, e);
        }
        return 0;
    }
}
