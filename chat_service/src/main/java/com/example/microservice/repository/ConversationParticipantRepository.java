package com.example.microservice.repository;

import com.example.microservice.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    List<ConversationParticipant> findByConversationConversationId(Long conversationId);

    boolean existsByConversationConversationIdAndUserUserId(Long conversationId, Long userId);

    @Query("""
        select cp.conversation.conversationId
        from ConversationParticipant cp
        where cp.user.userId in (:user1, :user2)
        group by cp.conversation.conversationId
        having count(distinct cp.user.userId) = 2
    """)
    List<Long> findCommonConversationIds(Long user1, Long user2);

    @Query("""
        select cp
        from ConversationParticipant cp
        where cp.conversation.conversationId = :conversationId
          and cp.user.userId = :userId
    """)
    Optional<ConversationParticipant> findParticipant(Long conversationId, Long userId);
}
