package com.example.microservice.services.repository;

import com.example.microservice.services.entity.DocumentBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DocumentBookmarkRepo extends JpaRepository<DocumentBookmark, Long> {
    boolean existsByDocumentIdAndUserId(Long documentId, Long userId);
    Optional<DocumentBookmark> findByDocumentIdAndUserId(Long documentId, Long userId);
    Page<DocumentBookmark> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
