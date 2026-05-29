package com.example.microservice.services;

import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.MessageStatusData;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageStatus;
import com.example.microservice.entity.PrivateConversation;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.PrivateConversationRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void markPendingDeliveredForUser(Long userId) {
        List<PrivateConversation> conversations = privateConversationRepo.findByParticipantId(userId);
        for (PrivateConversation privateConversation : conversations) {
            Long conversationId = privateConversation.getId();
            MessageStatus status = messageStatusService.ensureStatus(conversationId, userId);
            Long lastDeliveredMessageId = status.getLastDeliveredMessage() == null
                    ? null
                    : status.getLastDeliveredMessage().getId();

            List<Message> deliveredMessages = messageRepo.findUndeliveredMessagesForUser(
                    conversationId,
                    userId,
                    lastDeliveredMessageId
            );
            if (deliveredMessages.isEmpty()) {
                continue;
            }

            Message latestMessage = deliveredMessages.get(deliveredMessages.size() - 1);
            messageStatusService.markDelivered(conversationId, userId, latestMessage);

            Map<Long, List<Long>> messageIdsBySender = deliveredMessages.stream()
                    .collect(Collectors.groupingBy(
                            Message::getSenderId,
                            Collectors.mapping(Message::getId, Collectors.toList())
                    ));

            for (Map.Entry<Long, List<Long>> entry : messageIdsBySender.entrySet()) {
                MessageStatusData data = new MessageStatusData(
                        conversationId,
                        userId,
                        "DELIVERED",
                        entry.getValue(),
                        Instant.now()
                );
                SocketEnvelope<MessageStatusData> response =
                        new SocketEnvelope<>(EnumEvent.MESSAGE_DELIVERED.toString(), data);
                messagingTemplate.convertAndSendToUser(String.valueOf(entry.getKey()), "/queue/chat", response);
            }
        }
    }
}
