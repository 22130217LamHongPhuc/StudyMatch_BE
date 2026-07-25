package com.example.microservice.services.repository;

import com.example.microservice.services.entity.DocumentRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DocumentRatingRepo extends JpaRepository<DocumentRating, Long> {
    Page<DocumentRating> findByDocumentId(Long documentId, Pageable pageable);
    boolean existsByDocumentIdAndUserId(Long documentId, Long userId);
    Optional<DocumentRating> findByDocumentIdAndUserId(Long documentId, Long userId);

    @Query("SELECT COUNT(r) FROM DocumentRating r WHERE r.document.id = :documentId")
    Long countByDocumentId(@Param("documentId") Long documentId);

    @Query("SELECT AVG(r.score) FROM DocumentRating r WHERE r.document.id = :documentId")
    Double averageScoreByDocumentId(@Param("documentId") Long documentId);
}
