package com.example.microservice.repository;

import com.example.microservice.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantRepo extends JpaRepository<ConversationParticipant, Long> {
    @Query("""
            select count(cp) > 0
            from ConversationParticipant cp
            where cp.conversation.id = :conversationId
              and cp.userId = :userId
              and cp.leftAt is null
            """)
    boolean existsActiveParticipant(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query("""
            select cp.userId
            from ConversationParticipant cp
            where cp.conversation.id = :conversationId
              and cp.leftAt is null
            """)
    List<Long> findActiveUserIdsByConversationId(@Param("conversationId") Long conversationId);

    @Query("""
            select cp
            from ConversationParticipant cp
            where cp.conversation.id = :conversationId
              and cp.leftAt is null
            """)
    List<ConversationParticipant> findActiveParticipantsByConversationId(@Param("conversationId") Long conversationId);

    @Query("""
            select cp
            from ConversationParticipant cp
            where cp.conversation.id = :conversationId
              and cp.userId = :userId
            """)
    Optional<ConversationParticipant> findByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );
}
