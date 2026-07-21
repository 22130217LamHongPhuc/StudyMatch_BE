package com.example.microservice.socket;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionToUser = new ConcurrentHashMap<>();

    public boolean addSession(Long userId, String sessionId) {
        if (userId == null || sessionId == null) {
            return false;
        }
        boolean wasOffline = !isOnline(userId);
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionToUser.put(sessionId, userId);
        return wasOffline;
    }

    public Long removeSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        Long userId = sessionToUser.remove(sessionId);
        if (userId == null) {
            return null;
        }

        Set<String> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        return userId;
    }

    public Set<String> getSessions(Long userId) {
        return userSessions.getOrDefault(userId, Set.of());
    }

    public boolean isOnline(Long userId) {
        Set<String> sessions = userSessions.get(userId);
        return sessions != null && !sessions.isEmpty();
    }

    public Set<Long> getOnlineUserIds() {
        return Set.copyOf(userSessions.keySet());
    }

    public int getOnlineUsersCount() {
        return userSessions.size();
    }
}
