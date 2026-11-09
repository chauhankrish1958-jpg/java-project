package com.protrack.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginAttemptTrackerTest {

    private LoginAttemptTracker tracker;

    @BeforeEach
    public void setup() {
        tracker = LoginAttemptTracker.getInstance();
        tracker.clear();
    }

    @Test
    @DisplayName("Verify that initial state is not locked")
    public void testInitialStateNotLocked() {
        assertFalse(tracker.isLocked("user@example.com", "127.0.0.1"));
    }

    @Test
    @DisplayName("Verify that 1 to 4 failed attempts do not lock the user")
    public void testFailedAttemptsBelowThresholdNotLocked() {
        String email = "manager@protrack.com";
        String ip = "192.168.1.10";

        for (int i = 1; i <= 4; i++) {
            tracker.recordFailedAttempt(email, ip);
            assertFalse(tracker.isLocked(email, ip), "Should not be locked after " + i + " failed attempts");
        }
    }

    @Test
    @DisplayName("Verify that the 5th failed attempt triggers account lockout")
    public void testFifthFailedAttemptLocksTracker() {
        String email = "admin@protrack.com";
        String ip = "10.0.0.1";

        for (int i = 1; i <= 4; i++) {
            tracker.recordFailedAttempt(email, ip);
        }
        assertFalse(tracker.isLocked(email, ip));

        // 5th attempt
        tracker.recordFailedAttempt(email, ip);
        assertTrue(tracker.isLocked(email, ip), "Should be locked after 5 failed attempts");
    }

    @Test
    @DisplayName("Verify that successful login resets failed attempt counter")
    public void testSuccessfulLoginResetsAttempts() {
        String email = "member@protrack.com";
        String ip = "127.0.0.1";

        tracker.recordFailedAttempt(email, ip);
        tracker.recordFailedAttempt(email, ip);
        tracker.recordFailedAttempt(email, ip);

        tracker.resetAttempts(email, ip);

        // 2 more failed attempts should not lock since counter was reset
        tracker.recordFailedAttempt(email, ip);
        tracker.recordFailedAttempt(email, ip);

        assertFalse(tracker.isLocked(email, ip));
    }

    @Test
    @DisplayName("Verify that tracking is keyed by combination of email and IP independently")
    public void testDifferentEmailOrIpIsIndependent() {
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.2";

        // Lock user1 from ip1
        for (int i = 0; i < 5; i++) {
            tracker.recordFailedAttempt(email1, ip1);
        }
        assertTrue(tracker.isLocked(email1, ip1));

        // user2 from ip1 is NOT locked
        assertFalse(tracker.isLocked(email2, ip1));

        // user1 from ip2 is NOT locked
        assertFalse(tracker.isLocked(email1, ip2));
    }
}
