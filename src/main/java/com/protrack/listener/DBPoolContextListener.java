package com.protrack.listener;

import com.protrack.util.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Servlet Context Listener responsible for managing the lifecycle of the
 * database connection pool (HikariCP). Ensures clean release of database connections
 * when the application is undeployed or Tomcat shuts down.
 */
public class DBPoolContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().log("[DBPoolContextListener] ProTrack application context initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("[DBPoolContextListener] Closing HikariCP connection pool on application shutdown...");
        DBConnection.closePool();
        sce.getServletContext().log("[DBPoolContextListener] HikariCP connection pool closed.");
    }
}
