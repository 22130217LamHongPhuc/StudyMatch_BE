package com.example.microservice.repository;

import com.example.microservice.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface MessageRepo extends JpaRepository<Message, Long> {
    Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    Message findMessageById(Long id);
    List<Message> findByConversationIdAndIdIn(Long conversationId, List<Long> ids);
    Optional<Message> findFirstByConversationIdAndTypeAndContentContaining(Long conversationId, String type, String content);

    @Query("""
            select m from Message m
            where m.conversation.id = :conversationId
              and m.senderId <> :userId
              and (:lastDeliveredMessageId is null or m.id > :lastDeliveredMessageId)
            order by m.id asc
            """)
    List<Message> findUndeliveredMessagesForUser(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId,
            @Param("lastDeliveredMessageId") Long lastDeliveredMessageId
    );
}
