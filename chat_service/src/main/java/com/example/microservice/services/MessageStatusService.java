package com.example.microservice.services;

import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageStatus;
import com.example.microservice.repository.ConversationRepo;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.MessageStatusRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MessageStatusService {
    private final MessageStatusRepo messageStatusRepo;
    private final ConversationRepo conversationRepo;
    private final MessageRepo messageRepo;

    public MessageStatusService(
            MessageStatusRepo messageStatusRepo,
            ConversationRepo conversationRepo,
            MessageRepo messageRepo
    ) {
        this.messageStatusRepo = messageStatusRepo;
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
    }

    @Transactional
    public MessageStatus ensureStatus(Long conversationId, Long userId) {
        return messageStatusRepo.findByConversationIdAndUserId(conversationId, userId)
                .orElseGet(() -> createStatus(conversationId, userId));
    }

    @Transactional
    public void createInitialStatuses(Long conversationId, Long user1Id, Long user2Id) {
        ensureStatus(conversationId, user1Id);
        ensureStatus(conversationId, user2Id);
    }

    @Transactional
    public MessageStatus markDelivered(Long conversationId, Long userId, Message message) {
        MessageStatus status = ensureStatus(conversationId, userId);
        Message delivered = status.getLastDeliveredMessage();
        if (isAfter(delivered, message)) {
            status.setLastDeliveredMessage(message);
        }
        status.setUpdatedAt(Instant.now());
        return messageStatusRepo.save(status);
    }

    @Transactional
    public MessageStatus markSeen(Long conversationId, Long userId, List<Long> messageIds) {
        Message message = resolveLatestMessage(conversationId, messageIds)
                .orElseThrow(() -> new RuntimeException("Khong tim thay message hop le"));

        MessageStatus status = ensureStatus(conversationId, userId);
        Message seen = status.getLastSeenMessage();
        if (isAfter(seen, message)) {
            status.setLastSeenMessage(message);
        }

        Message delivered = status.getLastDeliveredMessage();
        if (isAfter(delivered, message)) {
            status.setLastDeliveredMessage(message);
        }

        status.setUpdatedAt(Instant.now());
        return messageStatusRepo.save(status);
    }

    @Transactional
    public MessageStatus markSenderSeen(Long conversationId, Long senderId, Message message) {
        MessageStatus status = ensureStatus(conversationId, senderId);
        Message seen = status.getLastSeenMessage();
        if (isAfter(seen, message)) {
            status.setLastSeenMessage(message);
        }
        status.setUpdatedAt(Instant.now());
        return messageStatusRepo.save(status);
    }

    public Optional<MessageStatus> findStatus(Long conversationId, Long userId) {
        return messageStatusRepo.findByConversationIdAndUserId(conversationId, userId);
    }

    public List<MessageStatus> getStatusesByConversation(Long conversationId) {
        return messageStatusRepo.findAllByConversationId(conversationId);
    }

    private MessageStatus createStatus(Long conversationId, Long userId) {
        Conversation conversation = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay conversation"));

        MessageStatus status = new MessageStatus();
        status.setConversation(conversation);
        status.setUserId(userId);
        status.setUpdatedAt(Instant.now());
        return messageStatusRepo.save(status);
    }

    private Optional<Message> resolveLatestMessage(Long conversationId, List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return Optional.empty();
        }

        return messageRepo.findByConversationIdAndIdIn(conversationId, messageIds)
                .stream()
                .max(Comparator.comparing(Message::getId));
    }

    private boolean isAfter(Message current, Message candidate) {
        if (candidate == null || candidate.getId() == null) {
            return false;
        }
        return current == null || current.getId() == null || current.getId() < candidate.getId();
    }
}
