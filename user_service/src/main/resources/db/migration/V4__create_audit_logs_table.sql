-- ============================================================
-- Migration: Tạo bảng audit_logs cho chức năng Ghi nhật ký Admin
-- Service: user_service
-- ============================================================

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    target_id VARCHAR(100) DEFAULT NULL,
    target_type VARCHAR(100) DEFAULT NULL,
    details TEXT DEFAULT NULL,
    ip_address VARCHAR(45) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
