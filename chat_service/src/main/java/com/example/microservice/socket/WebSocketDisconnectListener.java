package com.example.microservice.socket;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketDisconnectListener {
    private final WebSocketSessionManager sessionManager;
    private final ApplicationEventPublisher eventPublisher;

    public WebSocketDisconnectListener(
            WebSocketSessionManager sessionManager,
            ApplicationEventPublisher eventPublisher
    ) {
        this.sessionManager = sessionManager;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = sessionManager.removeSession(accessor.getSessionId());
        if (userId != null && !sessionManager.isOnline(userId)) {
            eventPublisher.publishEvent(new PresenceChangedEvent(userId, false));
        }
    }
}
