package com.example.microservice.repository;

import com.example.microservice.entity.MatchingItem;
import com.example.microservice.enums.MatchingActionStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingItemRepository extends JpaRepository<MatchingItem, Long> {

        Optional<MatchingItem> findByUserIdAndRecommendedUserId(
                        Long userId,
                        Long recommendedUserId);

        Optional<MatchingItem> findByUserIdAndRecommendedUserIdAndActionStatus(
                        Long userId,
                        Long recommendedUserId,
                        MatchingActionStatus status);

        @Query(value = """
                        select i
                        from MatchingItem i
                        where (:userId is null or i.userId = :userId)
                          and (:recommendedUserId is null or i.recommendedUserId = :recommendedUserId)
                          and (:actionStatus is null or i.actionStatus = :actionStatus)
                          and (:fromDate is null or i.updatedAt >= :fromDate)
                          and (:toDate is null or i.updatedAt < :toDate)
                          and i.isRecommendation = true
                        """, countQuery = """
                        select count(i)
                        from MatchingItem i
                        where (:userId is null or i.userId = :userId)
                          and (:recommendedUserId is null or i.recommendedUserId = :recommendedUserId)
                          and (:actionStatus is null or i.actionStatus = :actionStatus)
                          and (:fromDate is null or i.updatedAt >= :fromDate)
                          and (:toDate is null or i.updatedAt < :toDate)
                          and i.isRecommendation = true
                        """)
        Page<MatchingItem> findActionsPage(
                        @Param("userId") Long userId,
                        @Param("recommendedUserId") Long recommendedUserId,
                        @Param("actionStatus") MatchingActionStatus actionStatus,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate,
                        Pageable pageable);

        long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(LocalDateTime fromDate, LocalDateTime toDate);

        long countByActionStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        MatchingActionStatus actionStatus,
                        LocalDateTime fromDate,
                        LocalDateTime toDate);

        @Query("select count(i) from MatchingItem i where (:fromDate is null or i.createdAt >= :fromDate) and (:toDate is null or i.createdAt < :toDate) and i.isRecommendation = true")
        long countFiltered(
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        @Query("select count(i) from MatchingItem i where i.actionStatus = :actionStatus and (:fromDate is null or i.createdAt >= :fromDate) and (:toDate is null or i.createdAt < :toDate) and i.isRecommendation = true")
        long countByActionStatusFiltered(
                        @Param("actionStatus") MatchingActionStatus actionStatus,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        @Query("select avg(i.finalScore) from MatchingItem i where (:fromDate is null or i.createdAt >= :fromDate) and (:toDate is null or i.createdAt < :toDate) and i.isRecommendation = true")
        Double averageFinalScoreFiltered(
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        @Query("select i.createdAt, i.actionStatus from MatchingItem i where (:fromDate is null or i.createdAt >= :fromDate) and (:toDate is null or i.createdAt < :toDate) and i.isRecommendation = true")
        List<Object[]> findTrendData(
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        List<MatchingItem> findByUserIdAndActionStatusAndIsRecommendationTrueOrderByUpdatedAtDesc(Long userId,
                        MatchingActionStatus actionStatus);

        List<MatchingItem> findByUserIdAndActionStatusAndIsRecommendationTrueOrderByUpdatedAtDesc(Long userId,
                        MatchingActionStatus actionStatus, Pageable pageable);

        List<MatchingItem> findByUserIdAndActionStatusInAndIsRecommendationTrueOrderByUpdatedAtDesc(Long userId,
                        List<MatchingActionStatus> actionStatuses);

        List<MatchingItem> findByUserIdAndActionStatusInAndIsRecommendationTrueOrderByUpdatedAtDesc(Long userId,
                        List<MatchingActionStatus> actionStatuses, Pageable pageable);

        @Query("""
                            select i
                            from MatchingItem i
                            where (i.userId = :userId or i.recommendedUserId = :userId)
                              and i.actionStatus in :statuses
                              and i.isRecommendation = true
                            order by i.updatedAt desc
                        """)
        List<MatchingItem> findRelatedByUserIdAndActionStatusInOrderByUpdatedAtDesc(
                        @Param("userId") Long userId,
                        @Param("statuses") Collection<MatchingActionStatus> statuses,
                        Pageable pageable);

        @Query("""
                            select i
                            from MatchingItem i
                            where (i.userId = :userId or i.recommendedUserId = :userId)
                              and i.actionStatus = :status
                              and i.isRecommendation = true
                            order by i.updatedAt desc
                        """)
        List<MatchingItem> findRelatedByUserIdAndActionStatusOrderByUpdatedAtDesc(
                        @Param("userId") Long userId,
                        @Param("status") MatchingActionStatus status,
                        Pageable pageable);
}
