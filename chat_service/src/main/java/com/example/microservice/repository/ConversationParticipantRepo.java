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

    @Query(value = """
            select cp.conversation_id, other_cp.user_id
            from conversation_participants cp
            join conversations c on c.conversation_id = cp.conversation_id
            join conversation_participants other_cp on other_cp.conversation_id = cp.conversation_id
            where cp.user_id = :userId
              and cp.left_at is null
              and other_cp.user_id <> :userId
              and other_cp.left_at is null
              and lower(c.conversation_type) in ('1', 'private')
            """, nativeQuery = true)
    List<Object[]> findPrivateConversationPairsByParticipantId(@Param("userId") Long userId);
}
