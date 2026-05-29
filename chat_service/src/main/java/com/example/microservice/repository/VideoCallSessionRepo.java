package com.example.microservice.repository;

import com.example.microservice.entity.VideoCallSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VideoCallSessionRepo extends JpaRepository<VideoCallSession, Long> {
    @Query("""
        select s from VideoCallSession s
        where s.conversation.id = :conversationId and s.endedAt is null
        order by s.startedAt desc
    """)
    Optional<VideoCallSession> findActiveByConversationId(@Param("conversationId") Long conversationId);
}
