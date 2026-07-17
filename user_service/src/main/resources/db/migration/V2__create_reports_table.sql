-- ============================================================
-- Migration: Tạo bảng reports cho chức năng Báo cáo / Report
-- Service: user_service
-- ============================================================

CREATE TABLE IF NOT EXISTS reports (
    report_id       BIGINT          NOT NULL AUTO_INCREMENT,
    reporter_user_id BIGINT         NOT NULL,
    target_type     VARCHAR(50)     NOT NULL,   -- ReportTargetType: USER | POST | GROUP
    target_id       BIGINT          NOT NULL,
    reason          VARCHAR(100)    NOT NULL,   -- ReportReason: SPAM | HARASSMENT | ...
    description     TEXT,
    status          VARCHAR(50)     NOT NULL DEFAULT 'PENDING', -- ReportStatus
    admin_note      TEXT,
    handled_by      BIGINT,
    created_at      DATETIME        NOT NULL,
    updated_at      DATETIME,

    PRIMARY KEY (report_id),

    -- Chống spam: mỗi user chỉ report cùng target 1 lần
    CONSTRAINT uq_report_reporter_target
        UNIQUE (reporter_user_id, target_type, target_id)
);
