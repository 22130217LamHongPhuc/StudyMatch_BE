package com.example.microservice.socket;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.SocketEnvelope;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PresenceEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void onPresenceChanged(PresenceChangedEvent event) {
        SocketEnvelope<Map<String, Object>> payload = new SocketEnvelope<>(
                EnumEvent.USER_PRESENCE.toString(),
                Map.of(
                        "userId", event.getUserId(),
                        "online", event.isOnline()
                )
        );
        messagingTemplate.convertAndSend("/topic/presence", payload);
    }
}
