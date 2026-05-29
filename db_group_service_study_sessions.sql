SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `study_sessions`;
CREATE TABLE `study_sessions` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `group_id` bigint NULL DEFAULT NULL,
    `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    `start_time` datetime NOT NULL,
    `end_time` datetime NOT NULL,
    `subject_id` bigint NULL DEFAULT NULL,
    `subject_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `study_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `session_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `meeting_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `created_by_user_id` bigint NOT NULL,
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SCHEDULED',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_study_sessions_group_id`(`group_id` ASC) USING BTREE,
    INDEX `idx_study_sessions_created_by`(`created_by_user_id` ASC) USING BTREE,
    INDEX `idx_study_sessions_status`(`status` ASC) USING BTREE,
    INDEX `idx_study_sessions_session_type`(`session_type` ASC) USING BTREE,
    INDEX `idx_study_sessions_start_time`(`start_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `study_session_participants`;
CREATE TABLE `study_session_participants` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `session_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PARTICIPANT',
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
    `responded_at` datetime NULL DEFAULT NULL,
    `joined_at` datetime NULL DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_session_user`(`session_id` ASC, `user_id` ASC) USING BTREE,
    INDEX `idx_participant_session`(`session_id` ASC) USING BTREE,
    INDEX `idx_participant_user`(`user_id` ASC) USING BTREE,
    INDEX `idx_participant_status`(`status` ASC) USING BTREE,
    INDEX `idx_participant_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `study_session_feedbacks`;
CREATE TABLE `study_session_feedbacks` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `session_id` bigint NOT NULL,
    `reviewer_user_id` bigint NOT NULL,
    `target_user_id` bigint NULL DEFAULT NULL,
    `group_id` bigint NULL DEFAULT NULL,
    `rating` tinyint NOT NULL,
    `compatibility_score` tinyint NULL DEFAULT NULL,
    `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_session_reviewer_target`(`session_id` ASC, `reviewer_user_id` ASC, `target_user_id` ASC) USING BTREE,
    INDEX `idx_feedback_session`(`session_id` ASC) USING BTREE,
    INDEX `idx_feedback_reviewer`(`reviewer_user_id` ASC) USING BTREE,
    INDEX `idx_feedback_target_user`(`target_user_id` ASC) USING BTREE,
    INDEX `idx_feedback_group`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `study_session_reminders`;
CREATE TABLE `study_session_reminders` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `session_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `remind_before_minutes` int NOT NULL,
    `is_sent` tinyint(1) NOT NULL DEFAULT 0,
    `sent_at` datetime NULL DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_session_user_reminder`(`session_id` ASC, `user_id` ASC, `remind_before_minutes` ASC) USING BTREE,
    INDEX `idx_reminder_session`(`session_id` ASC) USING BTREE,
    INDEX `idx_reminder_user`(`user_id` ASC) USING BTREE,
    INDEX `idx_reminder_sent`(`is_sent` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
