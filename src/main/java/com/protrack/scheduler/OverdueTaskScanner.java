package com.protrack.scheduler;

import com.protrack.dao.TaskDAO;
import com.protrack.dao.impl.TaskDAOImpl;
import com.protrack.model.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Background Overdue Task Scanner demonstrating Core Java Multithreading & Concurrency.
 * Uses a ScheduledExecutorService for asynchronous periodic scanning, with ReentrantLock
 * and synchronized monitors guarding shared audit counters and execution metrics.
 */
public class OverdueTaskScanner {

    private static final OverdueTaskScanner INSTANCE = new OverdueTaskScanner();

    private final TaskDAO taskDAO;
    private ScheduledExecutorService scheduler;
    private final ReentrantLock lock = new ReentrantLock();

    // Shared mutable state protected by ReentrantLock
    private long totalScansCompleted = 0;
    private int lastOverdueDetectedCount = 0;
    private long lastScanDurationMs = 0;
    private String lastScanTime = "Not yet executed";
    private String lastThreadName = "None";
    private boolean isRunning = false;

    public static OverdueTaskScanner getInstance() {
        return INSTANCE;
    }

    public OverdueTaskScanner() {
        this(new TaskDAOImpl());
    }

    public OverdueTaskScanner(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    /**
     * Immutable snapshot record for thread-safe UI consumption.
     */
    public record MetricsSnapshot(
            long totalScans,
            int lastOverdueCount,
            long lastDurationMs,
            String lastScanTime,
            String threadName,
            boolean active
    ) {}

    /**
     * Initializes and starts the ScheduledExecutorService background thread.
     */
    public synchronized void start() {
        if (isRunning && scheduler != null && !scheduler.isShutdown()) {
            return;
        }

        ThreadFactory daemonFactory = new ThreadFactory() {
            private final AtomicInteger threadIndex = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ProTrack-OverdueTaskMonitor-" + threadIndex.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        };

        scheduler = Executors.newSingleThreadScheduledExecutor(daemonFactory);
        isRunning = true;

        // Schedule periodic overdue task checks every 2 minutes with an initial 10-second delay
        scheduler.scheduleWithFixedDelay(this::runScan, 10, 120, TimeUnit.SECONDS);
        System.out.println("[OverdueTaskScanner] Asynchronous daemon task scheduler started (interval: 120s).");
    }

    /**
     * Gracefully stops the executor service on application shutdown.
     */
    public synchronized void stop() {
        isRunning = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("[OverdueTaskScanner] Shutting down ScheduledExecutorService daemon...");
            scheduler.shutdownNow();
            try {
                if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("[OverdueTaskScanner] Daemon scheduler stopped successfully.");
        }
    }

    /**
     * Performs an overdue task scan and updates shared metrics under ReentrantLock.
     */
    public void runScan() {
        long startTime = System.currentTimeMillis();
        String currentThread = Thread.currentThread().getName();
        int overdueFound = 0;

        try {
            List<Task> overdue = taskDAO.findOverdueTasks(null);
            overdueFound = (overdue != null) ? overdue.size() : 0;
        } catch (Exception e) {
            // Gracefully handle database unavailability during tests or startup
            // Do not crash the background scheduler thread
            System.err.println("[OverdueTaskScanner] Notice: Background scan skipped (DB check: " + e.getMessage() + ")");
        }

        long duration = System.currentTimeMillis() - startTime;
        String formattedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"));

        // Explicit synchronization using ReentrantLock to guard shared metrics counter updates
        lock.lock();
        try {
            totalScansCompleted++;
            lastOverdueDetectedCount = overdueFound;
            lastScanDurationMs = duration;
            lastScanTime = formattedTime;
            lastThreadName = currentThread;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Synchronized read method ensuring thread-safe snapshot visibility.
     */
    public synchronized MetricsSnapshot getMetricsSnapshot() {
        lock.lock();
        try {
            return new MetricsSnapshot(
                    totalScansCompleted,
                    lastOverdueDetectedCount,
                    lastScanDurationMs,
                    lastScanTime,
                    lastThreadName,
                    isRunning
            );
        } finally {
            lock.unlock();
        }
    }

    /**
     * Helper to manually increment/test the lock mechanism directly in unit tests.
     */
    public void recordCustomScan(int overdueCount, long durationMs, String threadName) {
        lock.lock();
        try {
            totalScansCompleted++;
            lastOverdueDetectedCount = overdueCount;
            lastScanDurationMs = durationMs;
            lastScanTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"));
            lastThreadName = threadName;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        lock.lock();
        try {
            totalScansCompleted = 0;
            lastOverdueDetectedCount = 0;
            lastScanDurationMs = 0;
            lastScanTime = "Not yet executed";
            lastThreadName = "None";
        } finally {
            lock.unlock();
        }
    }
}
