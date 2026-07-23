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

    /**
     * Kiểm tra báo cáo trùng lặp (chống spam):
     * cùng reporter, cùng targetType, cùng targetId.
     */
    boolean existsByReporterUserIdAndTargetTypeAndTargetId(
            Long reporterUserId,
            ReportTargetType targetType,
            Long targetId
    );

    /**
     * Lấy danh sách báo cáo do một user gửi.
     */
    List<Report> findByReporterUserIdOrderByCreatedAtDesc(Long reporterUserId);

    Page<Report> findByReporterUserIdOrderByCreatedAtDesc(Long reporterUserId, Pageable pageable);

    /**
     * Admin filter: lọc báo cáo theo status, targetType, reason (nullable).
     */
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
}
