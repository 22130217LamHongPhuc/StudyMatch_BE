-- Schema for Learning Document Library Module

CREATE TABLE IF NOT EXISTS learning_documents (
  document_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT NULL,
  subject_id BIGINT NOT NULL,
  category VARCHAR(50) NOT NULL,
  file_url VARCHAR(1000) NOT NULL,
  storage_key VARCHAR(255) NULL,
  original_file_name VARCHAR(255) NOT NULL,
  file_type VARCHAR(50) NOT NULL,
  mime_type VARCHAR(100) NOT NULL,
  file_size BIGINT NOT NULL,
  uploader_id BIGINT NOT NULL,
  source_name VARCHAR(255) NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  rejection_reason VARCHAR(1000) NULL,
  view_count BIGINT NOT NULL DEFAULT 0,
  download_count BIGINT NOT NULL DEFAULT 0,
  average_rating DOUBLE NOT NULL DEFAULT 0.0,
  rating_count BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  published_at DATETIME NULL,
  PRIMARY KEY (document_id),
  INDEX idx_docs_status (status),
  INDEX idx_docs_subject (subject_id),
  INDEX idx_docs_category (category),
  INDEX idx_docs_uploader (uploader_id),
  INDEX idx_docs_created_at (created_at),
  INDEX idx_docs_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS document_bookmarks (
  bookmark_id BIGINT NOT NULL AUTO_INCREMENT,
  document_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (bookmark_id),
  UNIQUE KEY uk_document_bookmark_user (document_id, user_id),
  INDEX idx_bookmarks_user (user_id),
  CONSTRAINT fk_bookmarks_document
    FOREIGN KEY (document_id) REFERENCES learning_documents (document_id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS document_ratings (
  rating_id BIGINT NOT NULL AUTO_INCREMENT,
  document_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  score INT NOT NULL,
  review TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (rating_id),
  UNIQUE KEY uk_document_rating_user (document_id, user_id),
  INDEX idx_ratings_user (user_id),
  CONSTRAINT fk_ratings_document
    FOREIGN KEY (document_id) REFERENCES learning_documents (document_id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS document_reports (
  report_id BIGINT NOT NULL AUTO_INCREMENT,
  document_id BIGINT NOT NULL,
  reporter_id BIGINT NOT NULL,
  reason VARCHAR(50) NOT NULL,
  description TEXT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  resolved_at DATETIME NULL,
  resolved_by BIGINT NULL,
  resolution_note TEXT NULL,
  PRIMARY KEY (report_id),
  UNIQUE KEY uk_document_report_reporter (document_id, reporter_id),
  INDEX idx_reports_status (status),
  INDEX idx_reports_reporter (reporter_id),
  CONSTRAINT fk_reports_document
    FOREIGN KEY (document_id) REFERENCES learning_documents (document_id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
