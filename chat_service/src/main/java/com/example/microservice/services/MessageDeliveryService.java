package com.example.microservice.services;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.dto.NewMessageData;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageStatus;
import com.example.microservice.entity.PrivateConversation;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.PrivateConversationRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageDeliveryService {
    private final PrivateConversationRepo privateConversationRepo;
    private final MessageRepo messageRepo;
    private final MessageStatusService messageStatusService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageDeliveryService(
            PrivateConversationRepo privateConversationRepo,
            MessageRepo messageRepo,
            MessageStatusService messageStatusService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.privateConversationRepo = privateConversationRepo;
        this.messageRepo = messageRepo;
        this.messageStatusService = messageStatusService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void sendPendingMessagesToUser(Long userId) {
        List<PrivateConversation> conversations = privateConversationRepo.findByParticipantId(userId);
        for (PrivateConversation privateConversation : conversations) {
            Long conversationId = privateConversation.getId();
            MessageStatus status = messageStatusService.ensureStatus(conversationId, userId);
            Long lastDeliveredMessageId = status.getLastDeliveredMessage() == null
                    ? null
                    : status.getLastDeliveredMessage().getId();

            List<Message> pendingMessages = messageRepo.findUndeliveredMessagesForUser(
                    conversationId,
                    userId,
                    lastDeliveredMessageId
            );

            for (Message pendingMessage : pendingMessages) {
                NewMessageData data = new NewMessageData(conversationId, new MessDTO(pendingMessage));
                SocketEnvelope<NewMessageData> response =
                        new SocketEnvelope<>(EnumEvent.NEW_MESSAGE.toString(), data);
                messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/chat", response);
            }
        }
    }
}
