package com.example.microservice.services;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.dto.ModerationMessageRequest;
import com.example.microservice.dto.ModerationMessageResponse;
import com.example.microservice.dto.NewMessageData;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.entity.Message;
import com.example.microservice.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageModerationService {
    private final MessageRepo messageRepo;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${moderation.api-url:http://localhost:8001/moderate/messages}")
    private String moderationApiUrl;

    @Async
    public void moderateMessageAsync(Long messageId) {
        try {
            moderateMessage(messageId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void moderateMessage(Long messageId) {
        Message message = messageRepo.findMessageById(messageId);
        if (message == null || message.getContent() == null || message.getContent().isBlank()) {
            return;
        }

        List<ModerationMessageResponse> responses = requestModeration(message);
        if (responses == null || responses.isEmpty()) {
            return;
        }

        ModerationMessageResponse result = responses.stream()
                .filter(item -> messageId.equals(item.getId()))
                .findFirst()
                .orElse(responses.get(0));

        String label = normalizeLabel(result.getLabel());
        if (label == null || label.isBlank()) {
            return;
        }

        Message latestMessage = messageRepo.findMessageById(messageId);
        if (latestMessage == null) {
            return;
        }

        latestMessage.setModerationStatus(label);
        Message savedMessage = messageRepo.save(latestMessage);

        if (!"NONE".equals(label)) {
            broadcastModeratedMessage(savedMessage);
        }
    }

    private List<ModerationMessageResponse> requestModeration(Message message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<ModerationMessageRequest>> entity = new HttpEntity<>(
                List.of(new ModerationMessageRequest(message.getId(), message.getContent())),
                headers
        );

        ResponseEntity<List<ModerationMessageResponse>> response = restTemplate.exchange(
                moderationApiUrl,
                org.springframework.http.HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    private String normalizeLabel(String label) {
        if (label == null) {
            return null;
        }
        return label.trim().toUpperCase(Locale.ROOT);
    }

    private void broadcastModeratedMessage(Message message) {
        if (message.getConversation() == null || message.getConversation().getId() == null) {
            return;
        }

        Long conversationId = message.getConversation().getId();
        NewMessageData data = new NewMessageData(conversationId, new MessDTO(message));
        SocketEnvelope<NewMessageData> response =
                new SocketEnvelope<>(EnumEvent.MESSAGE_MODERATED.toString(), data);

        List<Long> participants = chatService.findConversationParticipants(conversationId);
        for (Long participantId : participants) {
            if (participantId == null) {
                continue;
            }
            messagingTemplate.convertAndSendToUser(String.valueOf(participantId), "/queue/chat", response);
        }
    }
}
