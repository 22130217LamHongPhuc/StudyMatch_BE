package com.example.microservice.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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

        log.info("[WebSocketSessionManager] 🟢 User CONNECTED | User ID: {} | Session ID: {} | Online Count: {} | Active User IDs: {}",
                userId, sessionId, userSessions.size(), userSessions.keySet());

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

        log.info("[WebSocketSessionManager] 🔴 User DISCONNECTED | User ID: {} | Session ID: {} | Online Count: {} | Remaining User IDs: {}",
                userId, sessionId, userSessions.size(), userSessions.keySet());

        return userId;
    }

    public Set<String> getSessions(Long userId) {
        return userSessions.getOrDefault(userId, Set.of());
    }

    public boolean isOnline(Long userId) {
        Set<String> sessions = userSessions.get(userId);
        boolean online = sessions != null && !sessions.isEmpty();
        log.info("[WebSocketSessionManager] 🔍 Check isOnline | User ID: {} -> {}", userId, online);
        return online;
    }

    public Set<Long> getOnlineUserIds() {
        log.info("[WebSocketSessionManager] 📋 Active Online User IDs: {} (Total: {})", userSessions.keySet(), userSessions.size());
        return Set.copyOf(userSessions.keySet());
    }

    public int getOnlineUsersCount() {
        int count = userSessions.size();
        log.info("[WebSocketSessionManager] 📊 Current Online Users Count: {} | Active User IDs: {}", count, userSessions.keySet());
        return count;
    }
}
