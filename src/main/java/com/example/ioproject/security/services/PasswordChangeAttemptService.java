package com.example.ioproject.security.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordChangeAttemptService {
    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    private static class AttemptInfo {
        int failedCount;
        long blockUntil = 0L;
    }

    public boolean isBlocked(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null) return false;
        return info.blockUntil > System.currentTimeMillis();
    }

    public long getBlockSeconds(String username) {
        AttemptInfo info = attempts.get(username);
        if (info == null) return 0;
        if (info.blockUntil > System.currentTimeMillis()) {
            return (info.blockUntil - System.currentTimeMillis()) / 1000;
        }
        return 0;
    }

    public void recordFailed(String username) {
        AttemptInfo info = attempts.computeIfAbsent(username, k -> new AttemptInfo());
        info.failedCount++;
        if (info.failedCount >= 5) {
            info.blockUntil = System.currentTimeMillis() + 15 * 60 * 1000; // 15 min
            info.failedCount = 0;
        }
    }

    public void recordSuccess(String username) {
        attempts.remove(username);
    }
}

