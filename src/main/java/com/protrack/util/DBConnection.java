package com.protrack.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database Connection Manager for ProTrack using HikariCP Connection Pooling.
 * Demonstrates singleton DataSource lifecycle management, configuration loading
 * via db.properties and environment variables, safe resource closure,
 * connection pooling sizing, and loud, fail-fast SQLException on MySQL unavailability.
 */
public class DBConnection {

    private static HikariDataSource dataSource;
    private static String jdbcUrl;
    private static String jdbcUser;
    private static String jdbcPassword;
    private static String jdbcDriver;
    private static int initialSize = 5;
    private static int maxTotal = 20;
    private static boolean isInitialized = false;
    private static String initializationError = null;

    private static synchronized void initConfig() {
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
            initialSize = Integer.parseInt(props.getProperty("db.initialSize", "5"));
        } catch (NumberFormatException ignored) {
            initialSize = 5;
        }

        try {
            maxTotal = Integer.parseInt(props.getProperty("db.maxTotal", "20"));
        } catch (NumberFormatException ignored) {
            maxTotal = 20;
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

    private static synchronized void ensureDataSourceInitialized() throws SQLException {
        if (!isInitialized) {
            initConfig();
        }

        if (initializationError != null) {
            throw new SQLException(initializationError);
        }

        if (dataSource == null || dataSource.isClosed()) {
            try {
                HikariConfig config = new HikariConfig();
                config.setDriverClassName(jdbcDriver);
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(jdbcUser);
                config.setPassword(jdbcPassword);
                config.setMinimumIdle(initialSize);
                config.setMaximumPoolSize(maxTotal);
                config.setPoolName("ProTrackHikariPool");

                // Fail-fast timeout configuration: 3 seconds connection timeout
                config.setConnectionTimeout(3000L);
                config.setValidationTimeout(2000L);
                config.setIdleTimeout(600000L);
                config.setMaxLifetime(1800000L);
                config.setInitializationFailTimeout(1L);

                dataSource = new HikariDataSource(config);
                System.out.println("[DBConnection] HikariCP Connection Pool initialized (minIdle=" + initialSize + ", maxPool=" + maxTotal + ")");
            } catch (Exception e) {
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
    }

    /**
     * Obtains a live JDBC Connection from the HikariCP connection pool.
     * Fails loudly with an explicit, descriptive SQLException if MySQL is unreachable.
     *
     * @return java.sql.Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        ensureDataSourceInitialized();
        try {
            return dataSource.getConnection();
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
     * Safely closes the HikariCP connection pool upon application shutdown.
     */
    public static synchronized void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
            isInitialized = false;
            System.out.println("[DBConnection] HikariCP Connection Pool closed successfully.");
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
