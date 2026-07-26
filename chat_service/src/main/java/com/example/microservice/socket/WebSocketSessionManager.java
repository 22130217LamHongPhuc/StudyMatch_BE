package com.example.microservice.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionManager {
    public enum CallReservation {
        RESERVED,
        CALLER_OFFLINE,
        TARGET_OFFLINE,
        CALLER_BUSY,
        TARGET_BUSY
    }

    private final Map<Long, UserRuntimeState> userSessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionToUser = new ConcurrentHashMap<>();
    private final Map<Long, Long> activeGroupCallByUser = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> groupCallUsersBySession = new ConcurrentHashMap<>();
    private final Map<Long, Long> groupCallCallerBySession = new ConcurrentHashMap<>();
    private final Object callLock = new Object();

    public boolean addSession(Long userId, String sessionId) {
        if (userId == null || sessionId == null) {
            return false;
        }
        boolean wasOffline = !isOnline(userId);
        userSessions.computeIfAbsent(userId, k -> new UserRuntimeState())
                .getSocketSessionIds()
                .add(sessionId);
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

        userSessions.computeIfPresent(userId, (id, state) -> {
            state.getSocketSessionIds().remove(sessionId);
            return !state.isOnline() && state.getActiveCall() == null ? null : state;
        });
        if (!isOnline(userId)) {
            Long groupSessionId = activeGroupCallByUser.get(userId);
            if (groupSessionId != null && userId.equals(groupCallCallerBySession.get(groupSessionId))) {
                releaseGroupCall(groupSessionId);
            } else if (groupSessionId != null) {
                leaveGroupCall(groupSessionId, userId);
            }
        }

        log.info("[WebSocketSessionManager] 🔴 User DISCONNECTED | User ID: {} | Session ID: {} | Online Count: {} | Remaining User IDs: {}",
                userId, sessionId, userSessions.size(), userSessions.keySet());

        return userId;
    }

    public Set<String> getSessions(Long userId) {
        UserRuntimeState state = userSessions.get(userId);
        return state == null ? Set.of() : Set.copyOf(state.getSocketSessionIds());
    }

    public boolean isOnline(Long userId) {
        UserRuntimeState state = userSessions.get(userId);
        boolean online = state != null && state.isOnline();
        log.info("[WebSocketSessionManager] 🔍 Check isOnline | User ID: {} -> {}", userId, online);
        return online;
    }

    public Set<Long> getOnlineUserIds() {
        Set<Long> onlineIds = ConcurrentHashMap.newKeySet();
        userSessions.forEach((userId, state) -> {
            if (state.isOnline()) {
                onlineIds.add(userId);
            }
        });
        log.info("[WebSocketSessionManager] 📋 Active Online User IDs: {} (Total: {})", onlineIds, onlineIds.size());
        return Set.copyOf(onlineIds);
    }

    public int getOnlineUsersCount() {
        Set<Long> onlineIds = getOnlineUserIds();
        int count = onlineIds.size();
        log.info("[WebSocketSessionManager] 📊 Current Online Users Count: {} | Active User IDs: {}", count, onlineIds);
        return count;
    }

    public CallReservation reserveCall(ActiveCall call) {
        synchronized (callLock) {
            UserRuntimeState caller = userSessions.get(call.getCallerId());
            UserRuntimeState target = userSessions.get(call.getCalleeId());
            if (caller == null || !caller.isOnline()) return CallReservation.CALLER_OFFLINE;
            if (target == null || !target.isOnline()) return CallReservation.TARGET_OFFLINE;
            if (caller.getActiveCall() != null || activeGroupCallByUser.containsKey(call.getCallerId())) {
                return CallReservation.CALLER_BUSY;
            }
            if (target.getActiveCall() != null || activeGroupCallByUser.containsKey(call.getCalleeId())) {
                return CallReservation.TARGET_BUSY;
            }

            caller.setActiveCall(call);
            target.setActiveCall(call);
            return CallReservation.RESERVED;
        }
    }

    public ActiveCall getActiveCall(Long userId, Long sessionId) {
        UserRuntimeState state = userSessions.get(userId);
        ActiveCall call = state == null ? null : state.getActiveCall();
        return call != null && call.getSessionId().equals(sessionId) ? call : null;
    }

    public ActiveCall acceptCall(Long sessionId, Long calleeId, Instant acceptedAt) {
        synchronized (callLock) {
            ActiveCall call = getActiveCall(calleeId, sessionId);
            if (call == null || !call.getCalleeId().equals(calleeId)
                    || call.getState() != ActiveCall.State.RINGING) {
                return null;
            }
            call.accept(acceptedAt);
            return call;
        }
    }

    public ActiveCall releaseCall(Long sessionId) {
        return releaseCall(sessionId, null);
    }

    public ActiveCall releaseCall(Long sessionId, ActiveCall.State requiredState) {
        synchronized (callLock) {
            ActiveCall call = userSessions.values().stream()
                    .map(UserRuntimeState::getActiveCall)
                    .filter(candidate -> candidate != null && candidate.getSessionId().equals(sessionId))
                    .findFirst()
                    .orElse(null);
            if (call == null || requiredState != null && call.getState() != requiredState) return null;

            clearUserCall(call.getCallerId(), sessionId);
            clearUserCall(call.getCalleeId(), sessionId);
            return call;
        }
    }

    public Set<Long> reserveGroupCall(Long sessionId, Long callerId, List<Long> candidateUserIds) {
        synchronized (callLock) {
            UserRuntimeState caller = userSessions.get(callerId);
            if (caller == null || !caller.isOnline()) {
                throw new IllegalStateException("CALLER_OFFLINE");
            }
            if (caller.getActiveCall() != null || activeGroupCallByUser.containsKey(callerId)) {
                throw new IllegalStateException("CALLER_BUSY");
            }

            Set<Long> reservedUsers = ConcurrentHashMap.newKeySet();
            reservedUsers.add(callerId);
            activeGroupCallByUser.put(callerId, sessionId);

            if (candidateUserIds != null) {
                for (Long userId : candidateUserIds) {
                    if (userId == null || callerId.equals(userId)) continue;
                    UserRuntimeState state = userSessions.get(userId);
                    if (state == null || !state.isOnline()
                            || state.getActiveCall() != null
                            || activeGroupCallByUser.containsKey(userId)) {
                        continue;
                    }
                    activeGroupCallByUser.put(userId, sessionId);
                    reservedUsers.add(userId);
                }
            }
            groupCallUsersBySession.put(sessionId, reservedUsers);
            groupCallCallerBySession.put(sessionId, callerId);
            return Set.copyOf(reservedUsers);
        }
    }

    public Set<Long> getGroupCallUsers(Long sessionId) {
        Set<Long> users = groupCallUsersBySession.get(sessionId);
        return users == null ? Set.of() : Set.copyOf(users);
    }

    public boolean joinGroupCall(Long sessionId, Long userId) {
        synchronized (callLock) {
            Long currentSessionId = activeGroupCallByUser.get(userId);
            if (currentSessionId != null && !currentSessionId.equals(sessionId)) {
                return false;
            }
            UserRuntimeState state = userSessions.get(userId);
            if (state == null || !state.isOnline() || state.getActiveCall() != null) {
                return false;
            }
            activeGroupCallByUser.put(userId, sessionId);
            groupCallUsersBySession
                    .computeIfAbsent(sessionId, ignored -> ConcurrentHashMap.newKeySet())
                    .add(userId);
            return true;
        }
    }

    public void leaveGroupCall(Long sessionId, Long userId) {
        synchronized (callLock) {
            activeGroupCallByUser.computeIfPresent(
                    userId,
                    (id, currentSessionId) -> currentSessionId.equals(sessionId) ? null : currentSessionId
            );
            groupCallUsersBySession.computeIfPresent(sessionId, (id, users) -> {
                users.remove(userId);
                return users.isEmpty() ? null : users;
            });
        }
    }

    public void releaseGroupCall(Long sessionId) {
        synchronized (callLock) {
            groupCallCallerBySession.remove(sessionId);
            Set<Long> users = groupCallUsersBySession.remove(sessionId);
            if (users == null) return;
            for (Long userId : users) {
                activeGroupCallByUser.computeIfPresent(
                        userId,
                        (id, currentSessionId) -> currentSessionId.equals(sessionId) ? null : currentSessionId
                );
            }
        }
    }

    private void clearUserCall(Long userId, Long sessionId) {
        userSessions.computeIfPresent(userId, (id, state) -> {
            ActiveCall current = state.getActiveCall();
            if (current != null && current.getSessionId().equals(sessionId)) {
                state.setActiveCall(null);
            }
            return !state.isOnline() && state.getActiveCall() == null ? null : state;
        });
    }
}
