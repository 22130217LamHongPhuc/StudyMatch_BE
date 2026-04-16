package com.example.microservice.repository;


import com.example.microservice.entity.PrivateConversation;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateConversationRepo extends JpaRepository<PrivateConversation, Long> {
    @Query("""
    SELECT p FROM PrivateConversation p
    WHERE p.id = :conversationId
      AND (p.user1Id = :userId OR p.user2Id = :userId)
""")
    Optional<PrivateConversation> findByConversationIdAndUserId(
            @Param("conversationId") Long conversationId,
            @Param("userId") Long userId
    );

    @Query(value = """
    SELECT CASE
        WHEN user1_id = :currentUserId THEN user2_id
        WHEN user2_id = :currentUserId THEN user1_id
    END AS other_user_id
    FROM private_conversations
    WHERE conversation_id = :conversationId
      AND (user1_id = :currentUserId OR user2_id = :currentUserId)
    """, nativeQuery = true)
    Optional<Long> findOtherUserId(@Param("conversationId") Long conversationId,
                                   @Param("currentUserId") Long currentUserId);
}