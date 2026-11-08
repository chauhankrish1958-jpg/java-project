package com.protrack.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility tool to automatically run database.sql and initialize the database schema.
 * Can be executed from CLI or called programmatically during setup.
 */
public class DatabaseInitializer {

    public static boolean initializeDatabase() {
        System.out.println("=================================================");
        System.out.println(" ProTrack - Automated Database Schema Setup");
        System.out.println("=================================================");

        // First test connection
        if (!DBConnection.testConnection()) {
            System.err.println("[DatabaseInitializer] ERROR: Could not connect to MySQL server.");
            System.err.println("Please check db.properties or ensure MySQL is running on localhost:3306.");
            return false;
        }

        InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream("database.sql");
        if (in == null) {
            // Check filesystem if not in classpath
            java.io.File sqlFile = new java.io.File("database.sql");
            if (sqlFile.exists()) {
                try {
                    in = new java.io.FileInputStream(sqlFile);
                } catch (Exception e) {
                    System.err.println("[DatabaseInitializer] Error opening database.sql: " + e.getMessage());
                }
            }
        }

        if (in == null) {
            System.err.println("[DatabaseInitializer] ERROR: database.sql not found.");
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            int executedStatements = 0;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Skip comments and empty lines
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("//") || trimmed.startsWith("#")) {
                    continue;
                }

                sqlBuilder.append(line).append("\n");

                if (trimmed.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();
                    // Remove trailing semicolon
                    sql = sql.substring(0, sql.length() - 1).trim();

                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                            executedStatements++;
                        } catch (Exception e) {
                            System.err.println("[DatabaseInitializer] Warning executing statement: " + e.getMessage());
                        }
                    }
                    sqlBuilder.setLength(0);
                }
            }

            System.out.println("[DatabaseInitializer] SUCCESS: Executed " + executedStatements + " SQL statements from database.sql.");
            System.out.println("[DatabaseInitializer] Database 'project_management_db' is now fully ready!");
            return true;
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Failed to initialize database: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        boolean ok = initializeDatabase();
        System.exit(ok ? 0 : 1);
    }
}
