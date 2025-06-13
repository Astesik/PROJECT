package com.example.ioproject.security.services;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service that tracks failed password change attempts and temporarily blocks users
 * who exceed the allowed number of failures.
 * <p>
 * It prevents brute-force or abuse of the password change functionality
 * by introducing a temporary blocking mechanism.
 */
@Service
public class PasswordChangeAttemptService {

    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    /**
     * Inner class holding failed attempt count and block timestamp.
     */
    private static class AttemptInfo {
        int failedCount;
        long blockUntil = 0L;
    }

    /**
     * Checks whether the given username is currently blocked from changing their password.
     *
     * @param username the username to check
     * @return {@code true} if the user is blocked, {@code false} otherwise
     */
    public boolean isBlocked(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null) return false;
        return info.blockUntil > System.currentTimeMillis();
    }

    /**
     * Returns the remaining block time in seconds for the given user.
     *
     * @param username the username to check
     * @return the number of seconds remaining in the block period; {@code 0} if not blocked
     */
    public long getBlockSeconds(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null) return 0;
        if (info.blockUntil > System.currentTimeMillis()) {
            return (info.blockUntil - System.currentTimeMillis()) / 1000;
        }
        return 0;
    }

    /**
     * Records a failed password change attempt for the given user.
     * <p>
     * If the user reaches 5 failed attempts, they are blocked for 15 minutes.
     *
     * @param username the username to record the failure for
     */
    public void recordFailed(String username) {
        AttemptInfo info = attempts.computeIfAbsent(username, k -> new AttemptInfo());
        info.failedCount++;
        if (info.failedCount >= 5) {
            info.blockUntil = System.currentTimeMillis() + 15 * 60 * 1000; // 15 min
            info.failedCount = 0;
        }
    }

    /**
     * Records a successful password change for the given user.
     * <p>
     * This clears any recorded failures and removes any block.
     *
     * @param username the username to reset the attempt tracking for
     */
    public void recordSuccess(String username) {
        attempts.remove(username);
    }
}

