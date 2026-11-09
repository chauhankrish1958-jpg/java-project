package com.protrack.listener;

import com.protrack.scheduler.OverdueTaskScanner;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Servlet Context Listener responsible for managing the background
 * task scheduler lifecycle. Starts the ScheduledExecutorService daemon on application
 * startup and gracefully terminates all worker threads on application shutdown.
 */
public class TaskSchedulerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().log("[TaskSchedulerListener] Initializing background overdue task scheduler...");
        OverdueTaskScanner.getInstance().start();
        sce.getServletContext().log("[TaskSchedulerListener] Background scheduler initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("[TaskSchedulerListener] Stopping background overdue task scheduler...");
        OverdueTaskScanner.getInstance().stop();
        sce.getServletContext().log("[TaskSchedulerListener] Background scheduler stopped.");
    }
}
