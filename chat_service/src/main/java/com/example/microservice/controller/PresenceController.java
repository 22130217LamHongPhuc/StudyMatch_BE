package com.example.microservice.controller;

import com.example.microservice.socket.WebSocketSessionManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/presence")
public class PresenceController {
    private final WebSocketSessionManager sessionManager;

    public PresenceController(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @GetMapping("/online")
    public Map<Long, Boolean> getOnlineStatuses(@RequestParam(required = false) String userIds) {
        Map<Long, Boolean> statuses = new LinkedHashMap<>();
        if (userIds == null || userIds.isBlank()) {
            return statuses;
        }

        for (String rawUserId : userIds.split(",")) {
            try {
                Long userId = Long.valueOf(rawUserId.trim());
                statuses.put(userId, sessionManager.isOnline(userId));
            } catch (NumberFormatException ignored) {
            }
        }
        return statuses;
    }
}
