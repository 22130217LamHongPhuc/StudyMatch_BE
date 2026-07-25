package com.example.microservice.services.repository;

import com.example.microservice.services.entity.DocumentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DocumentReportRepo extends JpaRepository<DocumentReport, Long> {
    boolean existsByDocumentIdAndReporterId(Long documentId, Long reporterId);
    Optional<DocumentReport> findByDocumentIdAndReporterId(Long documentId, Long reporterId);
}
