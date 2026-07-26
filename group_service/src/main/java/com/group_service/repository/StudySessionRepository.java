package com.group_service.repository;

import com.group_service.entity.StudySession;
import com.group_service.entity.enums.GroupStudySessionMode;
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

    List<StudySession> findByRecurrenceIdOrderByStartTimeAsc(String recurrenceId);

    @Query("""
        SELECT DISTINCT s FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND (:sessionType IS NULL OR s.sessionType = :sessionType)
          AND (:participantStatus IS NULL OR p.status = :participantStatus)
          AND (:sessionStatus IS NULL OR s.status = :sessionStatus)
          AND (:startFrom IS NULL OR s.startTime >= :startFrom)
          AND (:startTo IS NULL OR s.startTime <= :startTo)
          AND (:search IS NULL 
               OR LOWER(s.title) LIKE :search 
               OR LOWER(CAST(s.description AS string)) LIKE :search
               OR LOWER(s.subjectName) LIKE :search)
    """)
    Page<StudySession> findSessionsByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("sessionType") StudySessionType sessionType,
            @Param("participantStatus") StudySessionParticipantStatus participantStatus,
            @Param("sessionStatus") GroupStudySessionStatus sessionStatus,
            @Param("startFrom") LocalDateTime startFrom,
            @Param("startTo") LocalDateTime startTo,
            @Param("search") String search,
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

    @Query("""
        SELECT DISTINCT s FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND p.status <> com.group_service.entity.enums.StudySessionParticipantStatus.DECLINED
          AND s.status = com.group_service.entity.enums.GroupStudySessionStatus.SCHEDULED
          AND s.startTime >= :now
        ORDER BY s.startTime ASC, s.id ASC
    """)
    List<StudySession> findTopUpcomingSessionsByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
        SELECT s FROM StudySession s
        LEFT JOIN StudyGroup g ON s.groupId = g.id
        LEFT JOIN StudySessionParticipant p ON p.sessionId = s.id AND p.role = com.group_service.entity.enums.StudySessionParticipantRole.HOST
        WHERE (:keyword IS NULL OR 
               LOWER(s.title) LIKE :keyword OR
               LOWER(g.name) LIKE :keyword OR
               LOWER(p.userName) LIKE :keyword
              )
          AND (:status IS NULL OR s.status = :status)
          AND (:studyMode IS NULL OR s.studyMode = :studyMode)
          AND (:sessionType IS NULL OR s.sessionType = :sessionType)
          AND (:startFrom IS NULL OR s.startTime >= :startFrom)
          AND (:startTo IS NULL OR s.startTime <= :startTo)
    """)
    Page<StudySession> findAdminSessionsWithFilters(
            @Param("keyword") String keyword,
            @Param("status") GroupStudySessionStatus status,
            @Param("studyMode") GroupStudySessionMode studyMode,
            @Param("sessionType") StudySessionType sessionType,
            @Param("startFrom") LocalDateTime startFrom,
            @Param("startTo") LocalDateTime startTo,
            Pageable pageable
    );

    @Query("SELECT p.sessionId, COUNT(p.id) FROM StudySessionParticipant p WHERE p.sessionId IN :sessionIds GROUP BY p.sessionId")
    List<Object[]> countParticipantsBySessionIds(@Param("sessionIds") List<Long> sessionIds);

    @Query("""
        SELECT s FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId = :userId
          AND s.status <> com.group_service.entity.enums.GroupStudySessionStatus.CANCELLED
          AND p.status IN (com.group_service.entity.enums.StudySessionParticipantStatus.ACCEPTED, com.group_service.entity.enums.StudySessionParticipantStatus.JOINED)
          AND s.endTime > :time
    """)
    List<StudySession> findActiveSessionsAfter(
            @Param("userId") Long userId,
            @Param("time") LocalDateTime time
    );

    @Query("""
        SELECT s FROM StudySession s
        JOIN StudySessionParticipant p ON p.sessionId = s.id
        WHERE p.userId IN :userIds
          AND s.status = com.group_service.entity.enums.GroupStudySessionStatus.SCHEDULED
          AND s.startTime < :endTime AND s.endTime > :startTime
    """)
    List<StudySession> findOverlappingSessions(
            @Param("userIds") List<Long> userIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
        SELECT s FROM StudySession s
        WHERE s.status IN (
            com.group_service.entity.enums.GroupStudySessionStatus.SCHEDULED,
            com.group_service.entity.enums.GroupStudySessionStatus.ONGOING
        )
          AND s.endTime <= :now
    """)
    List<StudySession> findEndedSessions(@Param("now") LocalDateTime now);
}
