package com.example.microservice.repository;

import com.example.microservice.entity.GroupConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupConversationRepo extends JpaRepository<GroupConversation, Long> {
    Optional<GroupConversation> findById(Long conversationId);

    @Query("""
            select gc.id
            from GroupConversation gc
            where gc.groupId = :groupId
            """)
    Optional<Long> findConversationIdByGroupId(@Param("groupId") Long groupId);

    @Query("""
            select gc.groupId
            from GroupConversation gc
            where gc.id = :conversationId
            """)
    Optional<Long> findGroupIdByConversationId(@Param("conversationId") Long conversationId);
}
