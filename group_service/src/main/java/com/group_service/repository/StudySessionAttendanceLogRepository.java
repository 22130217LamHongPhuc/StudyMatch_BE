package com.group_service.repository;

import com.group_service.dto.projection.UserStudyDurationProjection;
import com.group_service.entity.StudySessionAttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudySessionAttendanceLogRepository extends JpaRepository<StudySessionAttendanceLog, Long> {

    Optional<StudySessionAttendanceLog> findFirstBySessionIdAndUserIdAndLeftAtIsNullOrderByJoinedAtDesc(
            Long sessionId,
            Long userId
    );

    List<StudySessionAttendanceLog> findBySessionIdAndUserIdOrderByJoinedAtAsc(
            Long sessionId,
            Long userId
    );

    List<StudySessionAttendanceLog> findBySessionIdAndLeftAtIsNull(Long sessionId);

    @Query("""
            SELECT COALESCE(SUM(l.durationSeconds), 0)
            FROM StudySessionAttendanceLog l
            WHERE l.sessionId = :sessionId
              AND l.userId = :userId
              AND l.durationSeconds IS NOT NULL
            """)
    Long sumDurationSecondsBySessionIdAndUserId(
            @Param("sessionId") Long sessionId,
            @Param("userId") Long userId
    );

    @Query("""
            SELECT l.userId AS userId, COALESCE(SUM(l.durationSeconds), 0) AS totalDurationSeconds
            FROM StudySessionAttendanceLog l
            WHERE l.durationSeconds IS NOT NULL
            GROUP BY l.userId
            """)
    List<UserStudyDurationProjection> getStudyDurationPerUser();
}