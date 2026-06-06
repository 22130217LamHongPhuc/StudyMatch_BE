package com.example.microservice.repository;

import com.example.microservice.entity.MatchingItem;
import com.example.microservice.enums.MatchingActionStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchingItemRepository extends JpaRepository<MatchingItem, Long> {

    List<MatchingItem> findByBatchIdOrderByCreatedAtAscIdAsc(Long batchId);

    @Query(value = """
            select i
            from MatchingItem i
            where (:userId is null or i.userId = :userId)
              and (:recommendedUserId is null or i.recommendedUserId = :recommendedUserId)
              and (:batchId is null or i.batch.id = :batchId)
              and (:actionStatus is null or i.actionStatus = :actionStatus)
              and (:fromDate is null or i.createdAt >= :fromDate)
              and (:toDate is null or i.createdAt < :toDate)
            """,
            countQuery = """
            select count(i)
            from MatchingItem i
            where (:userId is null or i.userId = :userId)
              and (:recommendedUserId is null or i.recommendedUserId = :recommendedUserId)
              and (:batchId is null or i.batch.id = :batchId)
              and (:actionStatus is null or i.actionStatus = :actionStatus)
              and (:fromDate is null or i.createdAt >= :fromDate)
              and (:toDate is null or i.createdAt < :toDate)
            """)
    @EntityGraph(attributePaths = "batch")
    Page<MatchingItem> findAdminPage(
            @Param("userId") Long userId,
            @Param("recommendedUserId") Long recommendedUserId,
            @Param("batchId") Long batchId,
            @Param("actionStatus") MatchingActionStatus actionStatus,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query(value = """
            select i
            from MatchingItem i
            where (:userId is null or i.userId = :userId)
              and (:recommendedUserId is null or i.recommendedUserId = :recommendedUserId)
              and (:actionStatus is null or i.actionStatus = :actionStatus)
              and (:fromDate is null or i.updatedAt >= :fromDate)
              and (:toDate is null or i.updatedAt < :toDate)
            """,
            countQuery = """
            select count(i)
            from MatchingItem i
            where (:userId is null or i.userId = :userId)
              and (:recommendedUserId is null or i.recommendedUserId = :recommendedUserId)
              and (:actionStatus is null or i.actionStatus = :actionStatus)
              and (:fromDate is null or i.updatedAt >= :fromDate)
              and (:toDate is null or i.updatedAt < :toDate)
            """)
    Page<MatchingItem> findActionsPage(
            @Param("userId") Long userId,
            @Param("recommendedUserId") Long recommendedUserId,
            @Param("actionStatus") MatchingActionStatus actionStatus,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    long countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(LocalDateTime fromDate, LocalDateTime toDate);

    long countByActionStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            MatchingActionStatus actionStatus,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    @Query("select count(i) from MatchingItem i where (:fromDate is null or i.createdAt >= :fromDate) and (:toDate is null or i.createdAt < :toDate)")
    long countFiltered(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("select count(i) from MatchingItem i where i.actionStatus = :actionStatus and (:fromDate is null or i.createdAt >= :fromDate) and (:toDate is null or i.createdAt < :toDate)")
    long countByActionStatusFiltered(
            @Param("actionStatus") MatchingActionStatus actionStatus,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}


