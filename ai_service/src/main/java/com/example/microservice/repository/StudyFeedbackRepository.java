package com.example.microservice.repository;

import com.example.microservice.entity.StudyFeedback;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.microservice.enums.StudySessionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyFeedbackRepository extends JpaRepository<StudyFeedback, Long> {

    @Query(value = """
        select f
        from StudyFeedback f
        where (:sessionType is null or f.sessionType = :sessionType)
          and (:reviewerUserId is null or f.reviewerUserId = :reviewerUserId)
          and (:targetUserId is null or f.targetUserId = :targetUserId)
          and (:groupId is null or f.groupId = :groupId)
          and (:minRating is null or f.rating >= :minRating)
          and (:fromDate is null or f.createdAt >= :fromDate)
          and (:toDate is null or f.createdAt < :toDate)
        """,
            countQuery = """
        select count(f)
        from StudyFeedback f
        where (:sessionType is null or f.sessionType = :sessionType)
          and (:reviewerUserId is null or f.reviewerUserId = :reviewerUserId)
          and (:targetUserId is null or f.targetUserId = :targetUserId)
          and (:groupId is null or f.groupId = :groupId)
          and (:minRating is null or f.rating >= :minRating)
          and (:fromDate is null or f.createdAt >= :fromDate)
          and (:toDate is null or f.createdAt < :toDate)
        """)
    Page<StudyFeedback> findAdminPage(
            @Param("sessionType") StudySessionType sessionType,
            @Param("reviewerUserId") Long reviewerUserId,
            @Param("targetUserId") Long targetUserId,
            @Param("groupId") Long groupId,
            @Param("minRating") Integer minRating,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );


    @Query("select count(f) from StudyFeedback f where (:fromDate is null or f.createdAt >= :fromDate) and (:toDate is null or f.createdAt < :toDate)")
    long countFiltered(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );



    @Query("select coalesce(avg(f.rating), 0) from StudyFeedback f where (:fromDate is null or f.createdAt >= :fromDate) and (:toDate is null or f.createdAt < :toDate)")
    Double averageRatingFiltered(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );



    @Query("select count(f) from StudyFeedback f where (:sessionType is null or f.sessionType = :sessionType) and (:fromDate is null or f.createdAt >= :fromDate) and (:toDate is null or f.createdAt < :toDate)")
    long countBySessionTypeFiltered(
            @Param("sessionType") StudySessionType sessionType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("select count(f) from StudyFeedback f where f.rating = :rating and (:fromDate is null or f.createdAt >= :fromDate) and (:toDate is null or f.createdAt < :toDate)")
    long countByRatingFiltered(
            @Param("rating") Integer rating,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    boolean existsBySessionIdAndReviewerUserId(Long sessionId, Long reviewerUserId);

    Optional<StudyFeedback> findBySessionIdAndReviewerUserId(Long sessionId, Long reviewerUserId);
}


