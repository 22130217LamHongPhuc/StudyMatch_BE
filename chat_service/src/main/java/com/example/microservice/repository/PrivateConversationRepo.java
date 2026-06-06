package com.example.microservice.repository;


import com.example.microservice.entity.PrivateConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
    select p from PrivateConversation p
    where p.user1Id = :userId or p.user2Id = :userId
""")
    List<PrivateConversation> findByParticipantId(@Param("userId") Long userId);


    @Query("""
    select p from PrivateConversation p
    where (p.user1Id = :user1Id and p.user2Id = :user2Id)
       or (p.user1Id = :user2Id and p.user2Id = :user1Id)
""")
    Optional<PrivateConversation> findPrivateBetweenTwoUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );



    @Query("""
    select p.id from PrivateConversation p
    where (p.user1Id = :user1Id and p.user2Id = :user2Id)
       or (p.user1Id = :user2Id and p.user2Id = :user1Id)
""")
    Long findConverIdByUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );

    @Query(value = """
            select user1_id from private_conversations where conversation_id = :conversationId
            union
            select user2_id from private_conversations where conversation_id = :conversationId
            """, nativeQuery = true)
    List<Long> findParticipantIdsByConversationId(@Param("conversationId") Long conversationId);
}
