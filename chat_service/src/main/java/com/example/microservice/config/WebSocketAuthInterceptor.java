package com.example.microservice.config;

import com.example.microservice.dto.TokenValidateResponse;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.services.MessageDeliveryService;
import com.example.microservice.socket.WebSocketSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    @Autowired
    UserClient client;
    @Autowired
    WebSocketSessionManager sessionManager;
    @Autowired
    @Lazy
    MessageDeliveryService messageDeliveryService;
    @Autowired
    @Lazy
    SimpMessagingTemplate messagingTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Long userId = sessionManager.removeSession(accessor.getSessionId());
            if (userId != null && !sessionManager.isOnline(userId)) {
                broadcastPresence(userId, false);
            }
        }
        return message;
    }

    public void handleConnect(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing token");
        }
        TokenValidateResponse response = client.validateToken(authorization);
        if (response == null || !response.isValid()) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = response.getUserId();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        String.valueOf(userId),
                        null,
                        List.of()
                );
        accessor.setUser(authentication);
        boolean wasOffline = sessionManager.addSession(userId, accessor.getSessionId());
        messageDeliveryService.markPendingDeliveredForUser(userId);
        if (wasOffline) {
            broadcastPresence(userId, true);
        }
    }

    private void broadcastPresence(Long userId, boolean online) {
        messagingTemplate.convertAndSend(
                "/topic/presence",
                java.util.Map.of(
                        "event", "USER_PRESENCE",
                        "data", java.util.Map.of(
                                "userId", userId,
                                "online", online
                        )
                )
        );
    }
}
