package com.example.microservice.services.repository;

import com.example.microservice.services.entity.DocumentCategory;
import com.example.microservice.services.entity.DocumentStatus;
import com.example.microservice.services.entity.LearningDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LearningDocumentRepo extends JpaRepository<LearningDocument, Long> {

    @Query("""
        SELECT d
        FROM LearningDocument d
        WHERE d.status = 'PUBLISHED'
          AND (:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:subjectId IS NULL OR d.subjectId = :subjectId)
          AND (:category IS NULL OR d.category = :category)
          AND (:fileType IS NULL OR LOWER(d.fileType) = LOWER(:fileType))
          AND (:minRating IS NULL OR d.averageRating >= :minRating)
    """)
    Page<LearningDocument> searchDocuments(
            @Param("search") String search,
            @Param("subjectId") Long subjectId,
            @Param("category") DocumentCategory category,
            @Param("fileType") String fileType,
            @Param("minRating") Double minRating,
            Pageable pageable
    );

    @Query("""
        SELECT d
        FROM LearningDocument d
        WHERE d.status = 'PUBLISHED'
        ORDER BY d.averageRating DESC, d.viewCount DESC, d.downloadCount DESC
    """)
    List<LearningDocument> findFeaturedDocuments(Pageable pageable);

    @Query("""
        SELECT d
        FROM LearningDocument d
        WHERE (:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR d.status = :status)
          AND (:subjectId IS NULL OR d.subjectId = :subjectId)
          AND (:category IS NULL OR d.category = :category)
          AND (:uploaderId IS NULL OR d.uploaderId = :uploaderId)
          AND (:startDate IS NULL OR d.createdAt >= :startDate)
          AND (:endDate IS NULL OR d.createdAt <= :endDate)
    """)
    Page<LearningDocument> searchDocumentsForAdmin(
            @Param("search") String search,
            @Param("status") DocumentStatus status,
            @Param("subjectId") Long subjectId,
            @Param("category") DocumentCategory category,
            @Param("uploaderId") Long uploaderId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
