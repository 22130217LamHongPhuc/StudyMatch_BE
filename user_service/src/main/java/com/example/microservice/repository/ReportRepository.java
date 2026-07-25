package com.example.microservice.repository;

import com.example.microservice.entity.Report;
import com.example.microservice.enums.ReportReason;
import com.example.microservice.enums.ReportStatus;
import com.example.microservice.enums.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

public interface ReportRepository extends JpaRepository<Report, Long> {


    List<Report> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            LocalDateTime start, LocalDateTime end);

  

    boolean existsByReporterUserIdAndTargetTypeAndTargetId(
            Long reporterUserId,
            ReportTargetType targetType,
            Long targetId
    );

    List<Report> findByReporterUserIdOrderByCreatedAtDesc(Long reporterUserId);

    Page<Report> findByReporterUserIdOrderByCreatedAtDesc(Long reporterUserId, Pageable pageable);

    @Query("""
            SELECT r FROM Report r
            WHERE
                (:status IS NULL OR r.status = :status)
                AND (:targetType IS NULL OR r.targetType = :targetType)
                AND (:reason IS NULL OR r.reason = :reason)
            ORDER BY r.createdAt DESC
            """)
    Page<Report> findForAdmin(
            @Param("status") ReportStatus status,
            @Param("targetType") ReportTargetType targetType,
            @Param("reason") ReportReason reason,
            Pageable pageable
    );

    @Query("""
            SELECT r.targetId, COUNT(r.id)
            FROM Report r
            WHERE r.targetType = :targetType
                AND r.targetId IN :targetIds
                AND r.status = com.example.microservice.enums.ReportStatus.PENDING
            GROUP BY r.targetId
            """)
    List<Object[]> countUnresolvedReportsByTargetIds(
            @Param("targetType") ReportTargetType targetType,
            @Param("targetIds") List<Long> targetIds
    );
}
