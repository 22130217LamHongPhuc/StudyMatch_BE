package com.group_service.repository;

import com.group_service.entity.StudySession;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import com.group_service.entity.enums.StudySessionType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StudySession s WHERE s.id = :id")
    java.util.Optional<StudySession> findByIdForUpdate(@Param("id") Long id);

    List<StudySession> findByGroupIdOrderByStartTimeAsc(Long groupId);

    @Query("""
        SELECT DISTINCT s FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND (:sessionType IS NULL OR s.sessionType = :sessionType)
          AND (:participantStatus IS NULL OR p.status = :participantStatus)
          AND (:sessionStatus IS NULL OR s.status = :sessionStatus)
          AND (:startFrom IS NULL OR s.startTime >= :startFrom)
          AND (:startTo IS NULL OR s.startTime <= :startTo)
    """)
    Page<StudySession> findSessionsByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("sessionType") StudySessionType sessionType,
            @Param("participantStatus") StudySessionParticipantStatus participantStatus,
            @Param("sessionStatus") GroupStudySessionStatus sessionStatus,
            @Param("startFrom") LocalDateTime startFrom,
            @Param("startTo") LocalDateTime startTo,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(DISTINCT s.id) FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND s.startTime >= :dayStart AND s.startTime < :dayEnd
          AND s.status <> com.group_service.entity.enums.GroupStudySessionStatus.CANCELLED
    """)
    long countTodaySessions(
            @Param("userId") Long userId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );

    @Query("""
        SELECT COUNT(DISTINCT s.id) FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND s.startTime >= :weekStart AND s.startTime < :weekEnd
          AND s.status <> com.group_service.entity.enums.GroupStudySessionStatus.CANCELLED
    """)
    long countWeekSessions(
            @Param("userId") Long userId,
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd
    );

    @Query("""
        SELECT COUNT(DISTINCT s.id) FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND p.status = com.group_service.entity.enums.StudySessionParticipantStatus.PENDING
    """)
    long countPendingSessions(@Param("userId") Long userId);

    @Query("""
        SELECT COUNT(DISTINCT s.id) FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND s.sessionType = com.group_service.entity.enums.StudySessionType.GROUP
          AND s.status <> com.group_service.entity.enums.GroupStudySessionStatus.CANCELLED
    """)
    long countGroupSessions(@Param("userId") Long userId);

    @Query("""
        SELECT s FROM StudySession s
        WHERE s.status = com.group_service.entity.enums.GroupStudySessionStatus.SCHEDULED
          AND s.reminderSent = false
          AND s.startTime BETWEEN :now AND :fiveMinLater
    """)
    List<StudySession> findUpcomingSessions(
            @Param("now") LocalDateTime now,
            @Param("fiveMinLater") LocalDateTime fiveMinLater
    );
}
