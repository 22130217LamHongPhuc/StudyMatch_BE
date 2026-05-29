package com.example.microservice.repository;

import com.example.microservice.entity.VideoCallParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VideoCallParticipantRepo extends JpaRepository<VideoCallParticipant, Long> {
    @Query("""
        select p from VideoCallParticipant p
        where p.session.id = :sessionId and p.userId = :userId
    """)
    Optional<VideoCallParticipant> findBySessionIdAndUserId(
            @Param("sessionId") Long sessionId,
            @Param("userId") Long userId
    );
}
