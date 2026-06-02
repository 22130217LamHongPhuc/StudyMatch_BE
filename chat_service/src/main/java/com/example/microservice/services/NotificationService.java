package com.example.microservice.services;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.SessionReminderRequest;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.socket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final WebSocketSessionManager sessionManager;
    private final SimpMessagingTemplate messagingTemplate;

    public List<Long> getOnlineUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return userIds.stream()
                .filter(sessionManager::isOnline)
                .collect(Collectors.toList());
    }

    public Map<String, Boolean> getOnlineStatusMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        Map<String, Boolean> statusMap = new HashMap<>();
        for (Long userId : userIds) {
            if (userId == null) {
                continue;
            }
            statusMap.put(String.valueOf(userId), sessionManager.isOnline(userId));
        }
        return statusMap;
    }

    public void sendSessionReminder(SessionReminderRequest request) {
        Long userId = request.getUserId();
        if (userId == null || !sessionManager.isOnline(userId)) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("sessionTitle", request.getSessionTitle());
        data.put("startTime", request.getStartTime());
        data.put("groupName", request.getGroupName());
        data.put("sessionId", request.getSessionId());
        data.put("meetingUrl",request.getSessionId());


        SocketEnvelope<Map<String, Object>> envelope = new SocketEnvelope<>(
                EnumEvent.STUDY_SESSION_REMINDER.toString(),
                data
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/chat",
                envelope
        );
    }
}
