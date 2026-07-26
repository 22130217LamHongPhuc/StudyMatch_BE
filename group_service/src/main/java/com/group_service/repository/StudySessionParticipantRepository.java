package com.group_service.repository;

import com.group_service.entity.StudySessionParticipant;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudySessionParticipantRepository extends JpaRepository<StudySessionParticipant, Long> {

    Optional<StudySessionParticipant> findBySessionIdAndUserId(Long sessionId, Long userId);

    Optional<StudySessionParticipant> findFirstBySessionIdAndUserIdNot(Long sessionId, Long userId);

    List<StudySessionParticipant> findBySessionId(Long sessionId);

    long countBySessionId(Long sessionId);

    long countBySessionIdAndStatus(Long sessionId, StudySessionParticipantStatus status);

    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);

    List<StudySessionParticipant> findByUserIdAndStatus(Long userId, StudySessionParticipantStatus status);

    @org.springframework.data.jpa.repository.Query("""
        SELECT p FROM StudySessionParticipant p
        JOIN FETCH p.studySession s
        WHERE p.userId = :userId
          AND s.status = com.group_service.entity.enums.GroupStudySessionStatus.COMPLETED
    """)
    List<StudySessionParticipant> findAllCompletedParticipations(@org.springframework.data.repository.query.Param("userId") Long userId);
}
