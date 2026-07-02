package com.example.microservice.repository;

import com.example.microservice.entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageStatusRepo extends JpaRepository<MessageStatus, Long> {
    Optional<MessageStatus> findByConversationIdAndUserId(Long conversationId, Long userId);
    java.util.List<MessageStatus> findAllByConversationId(Long conversationId);
}
