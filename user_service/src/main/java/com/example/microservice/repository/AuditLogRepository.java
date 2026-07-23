package com.example.microservice.repository;

import com.example.microservice.entity.AuditLog;
import com.example.microservice.dto.respone.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT DISTINCT log.action FROM AuditLog log")
    List<String> findDistinctActions();

    @Query("SELECT DISTINCT log.targetType FROM AuditLog log")
    List<String> findDistinctTargetTypes();

    @Query("""
        SELECT new com.example.microservice.dto.respone.AuditLogResponse(
            log.id,
            log.adminId,
            u.fullName,
            u.email,
            log.action,
            log.targetId,
            log.targetType,
            log.details,
            log.ipAddress,
            log.createdAt
        )
        FROM AuditLog log
        LEFT JOIN User u ON log.adminId = u.userId
        WHERE (:keyword IS NULL OR :keyword = '' 
               OR LOWER(log.action) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(log.details) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
          AND (:action IS NULL OR :action = '' OR log.action = :action)
          AND (:targetType IS NULL OR :targetType = '' OR log.targetType = :targetType)
    """)
    Page<AuditLogResponse> findAuditLogs(
            @Param("keyword") String keyword,
            @Param("action") String action,
            @Param("targetType") String targetType,
            Pageable pageable
    );
}
