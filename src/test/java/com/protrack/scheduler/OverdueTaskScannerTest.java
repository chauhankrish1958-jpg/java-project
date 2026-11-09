package com.protrack.scheduler;

import com.protrack.dao.TaskDAO;
import com.protrack.exception.DatabaseOperationException;
import com.protrack.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OverdueTaskScannerTest {

    private TaskDAO mockTaskDAO;
    private OverdueTaskScanner scanner;

    @BeforeEach
    public void setup() {
        mockTaskDAO = mock(TaskDAO.class);
        scanner = new OverdueTaskScanner(mockTaskDAO);
        scanner.reset();
    }

    @Test
    @DisplayName("Verify initial metrics snapshot before scan execution")
    public void testInitialMetricsSnapshot() {
        OverdueTaskScanner.MetricsSnapshot snapshot = scanner.getMetricsSnapshot();
        assertNotNull(snapshot);
        assertEquals(0, snapshot.totalScans());
        assertEquals(0, snapshot.lastOverdueCount());
        assertEquals("Not yet executed", snapshot.lastScanTime());
    }

    @Test
    @DisplayName("Verify runScan executes taskDAO query and updates metrics under ReentrantLock")
    public void testRunScanUpdatesMetrics() {
        Task mockTask = mock(Task.class);
        when(mockTaskDAO.findOverdueTasks(null)).thenReturn(List.of(mockTask, mockTask));

        scanner.runScan();

        OverdueTaskScanner.MetricsSnapshot snapshot = scanner.getMetricsSnapshot();
        assertEquals(1, snapshot.totalScans());
        assertEquals(2, snapshot.lastOverdueCount());
        assertNotEquals("Not yet executed", snapshot.lastScanTime());
        verify(mockTaskDAO, times(1)).findOverdueTasks(null);
    }

    @Test
    @DisplayName("Verify thread-safe concurrent updates to shared scan counter using ReentrantLock")
    public void testConcurrentUpdatesUnderLock() throws InterruptedException {
        int threadCount = 10;
        int scansPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < scansPerThread; j++) {
                        scanner.recordCustomScan(1, 5, Thread.currentThread().getName());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean finished = latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();
        assertTrue(finished, "All concurrent tasks should complete within timeout");

        OverdueTaskScanner.MetricsSnapshot snapshot = scanner.getMetricsSnapshot();
        assertEquals(threadCount * scansPerThread, snapshot.totalScans(),
                "Shared counter protected by ReentrantLock must match exact concurrent execution total");
    }

    @Test
    @DisplayName("Verify background scanner handles database failure gracefully without crashing")
    public void testGracefulExceptionHandling() {
        when(mockTaskDAO.findOverdueTasks(null)).thenThrow(new DatabaseOperationException("Simulated DB Offline"));

        assertDoesNotThrow(() -> scanner.runScan());

        OverdueTaskScanner.MetricsSnapshot snapshot = scanner.getMetricsSnapshot();
        assertEquals(1, snapshot.totalScans());
        assertEquals(0, snapshot.lastOverdueCount());
    }

    @Test
    @DisplayName("Verify scheduler start and stop lifecycle management")
    public void testSchedulerLifecycle() {
        OverdueTaskScanner singleton = OverdueTaskScanner.getInstance();
        singleton.start();
        assertTrue(singleton.getMetricsSnapshot().active());

        // Idempotent start
        singleton.start();
        assertTrue(singleton.getMetricsSnapshot().active());

        singleton.stop();
        assertFalse(singleton.getMetricsSnapshot().active());
    }
}
