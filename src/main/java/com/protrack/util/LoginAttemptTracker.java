package com.protrack.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-memory brute-force protection tracking failed login attempts per email and remote IP address.
 * Locks the target key for 5 minutes after 5 failed attempts.
 */
public class LoginAttemptTracker {

    public static final int MAX_ATTEMPTS = 5;
    public static final long LOCK_DURATION_MS = 5 * 60 * 1000L; // 5 minutes

    private static final LoginAttemptTracker INSTANCE = new LoginAttemptTracker();

    private final ConcurrentMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    public static LoginAttemptTracker getInstance() {
        return INSTANCE;
    }

    public static class AttemptRecord {
        private int failedCount;
        private long lockExpiry;

        public AttemptRecord(int failedCount, long lockExpiry) {
            this.failedCount = failedCount;
            this.lockExpiry = lockExpiry;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public long getLockExpiry() {
            return lockExpiry;
        }
    }

    private String buildKey(String email, String ip) {
        String cleanEmail = (email != null) ? email.trim().toLowerCase() : "";
        String cleanIp = (ip != null) ? ip.trim() : "";
        return cleanEmail + ":" + cleanIp;
    }

    /**
     * Checks if the email + IP combination is currently locked due to too many failed attempts.
     */
    public boolean isLocked(String email, String ip) {
        String key = buildKey(email, ip);
        AttemptRecord record = attempts.get(key);
        if (record == null) {
            return false;
        }
        if (record.lockExpiry > System.currentTimeMillis()) {
            return true;
        }
        // Lock expired
        if (record.lockExpiry > 0) {
            attempts.remove(key);
        }
        return false;
    }

    /**
     * Records a failed attempt for the email + IP combination.
     * If failed attempts reach MAX_ATTEMPTS, locks the key for LOCK_DURATION_MS.
     */
    public void recordFailedAttempt(String email, String ip) {
        String key = buildKey(email, ip);
        long now = System.currentTimeMillis();

        attempts.compute(key, (k, record) -> {
            if (record == null || (record.lockExpiry > 0 && record.lockExpiry <= now)) {
                return new AttemptRecord(1, 0L);
            }
            if (record.lockExpiry > now) {
                // Already locked
                return record;
            }
            int newCount = record.failedCount + 1;
            long newExpiry = (newCount >= MAX_ATTEMPTS) ? (now + LOCK_DURATION_MS) : 0L;
            return new AttemptRecord(newCount, newExpiry);
        });
    }

    /**
     * Resets failed attempts upon a successful login.
     */
    public void resetAttempts(String email, String ip) {
        String key = buildKey(email, ip);
        attempts.remove(key);
    }

    /**
     * Clears all attempt tracking records (used for test setup).
     */
    public void clear() {
        attempts.clear();
    }
}
