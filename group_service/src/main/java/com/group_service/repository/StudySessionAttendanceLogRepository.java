package com.group_service.repository;

import com.group_service.dto.projection.UserStudyDurationProjection;
import com.group_service.entity.StudySessionAttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface StudySessionAttendanceLogRepository extends JpaRepository<StudySessionAttendanceLog, Long> {

    @Query(value = """
            SELECT DATE(l.joined_at) AS study_date,
                   COALESCE(SUM(l.duration_seconds), 0) / 3600.0 AS total_hours,
                   COALESCE(SUM(CASE WHEN s.study_mode IN ('ONLINE', 'HYBRID')
                                     THEN l.duration_seconds ELSE 0 END), 0) / 3600.0 AS online_hours,
                   COALESCE(SUM(CASE WHEN s.study_mode = 'OFFLINE'
                                     THEN l.duration_seconds ELSE 0 END), 0) / 3600.0 AS offline_hours
            FROM study_session_attendance_logs l
            JOIN study_sessions s ON s.id = l.session_id
            WHERE l.joined_at >= :startDate
              AND l.joined_at < :endDate
              AND l.duration_seconds IS NOT NULL
            GROUP BY DATE(l.joined_at)
            ORDER BY study_date
            """, nativeQuery = true)
    List<Object[]> findAdminStudyDurationTimeline(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

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
