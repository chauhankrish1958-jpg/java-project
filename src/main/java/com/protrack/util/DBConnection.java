package com.protrack.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database Connection Manager for ProTrack.
 * Demonstrates singleton pattern, JDBC connection lifecycle management,
 * configuration loading via db.properties, environment variable overrides,
 * safe resource closure, and loud, explicit failure on MySQL unavailability.
 */
public class DBConnection {

    private static String jdbcUrl;
    private static String jdbcUser;
    private static String jdbcPassword;
    private static String jdbcDriver;
    private static boolean isInitialized = false;
    private static String initializationError = null;

    static {
        init();
    }

    private static synchronized void init() {
        if (isInitialized) return;

        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Notice: Unable to load db.properties: " + e.getMessage());
        }

        // Environment variables override properties file for cloud and container deployments
        jdbcDriver = System.getenv("DB_DRIVER");
        if (jdbcDriver == null || jdbcDriver.trim().isEmpty()) {
            jdbcDriver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }

        jdbcUrl = System.getenv("DB_URL");
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            jdbcUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/project_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
        }

        jdbcUser = System.getenv("DB_USER");
        if (jdbcUser == null || jdbcUser.trim().isEmpty()) {
            jdbcUser = props.getProperty("db.user", "root");
        }

        jdbcPassword = System.getenv("DB_PASSWORD");
        if (jdbcPassword == null) {
            jdbcPassword = props.getProperty("db.password", "root");
        }

        try {
            Class.forName(jdbcDriver);
            isInitialized = true;
            initializationError = null;
            System.out.println("[DBConnection] MySQL JDBC Driver loaded: " + jdbcDriver);
        } catch (ClassNotFoundException e) {
            initializationError = "MySQL JDBC Driver not found in classpath: " + jdbcDriver;
            System.err.println("[DBConnection] Critical: " + initializationError);
        }
    }

    /**
     * Obtains a live JDBC Connection to the configured MySQL database.
     * Fails loudly with an explicit, descriptive SQLException if MySQL is unreachable.
     *
     * @return java.sql.Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            init();
        }

        if (initializationError != null) {
            throw new SQLException(initializationError);
        }

        try {
            return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
        } catch (SQLException e) {
            String errorMsg = String.format(
                "Failed to connect to MySQL database at [%s] with user [%s]. " +
                "Ensure MySQL Server is running on localhost:3306 and schema 'project_management_db' " +
                "is initialized via database.sql. Underlying cause: %s",
                jdbcUrl, jdbcUser, e.getMessage()
            );
            System.err.println("[DBConnection] " + errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }

    /**
     * Safely closes Statement and ResultSet.
     */
    public static void close(Statement stmt, ResultSet rs) {
        close(null, stmt, rs);
    }

    /**
     * Safely closes Connection and Statement.
     */
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    /**
     * Safely closes Connection, Statement and ResultSet.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Helper to safely rollback transactions without throwing exceptions.
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Tests if the MySQL database is currently reachable.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Returns false since silent embedded fallback has been removed.
     */
    public static boolean isUsingFallback() {
        return false;
    }
}
