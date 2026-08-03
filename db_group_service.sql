/*
 Navicat Premium Dump SQL

 Source Server         : ss
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45-google)
 Source Host           : 35.198.241.223:3306
 Source Schema         : db_group_service

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45-google)
 File Encoding         : 65001

 Date: 01/08/2026 14:43:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for group_free_time_slots
-- ----------------------------
DROP TABLE IF EXISTS `group_free_time_slots`;
CREATE TABLE `group_free_time_slots`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `day_of_week` tinyint NOT NULL COMMENT '0=Mon ... 6=Sun',
  `slot_code` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_term_day_slot`(`group_id` ASC, `term_id` ASC, `day_of_week` ASC, `slot_code` ASC) USING BTREE,
  INDEX `fk_free_slots_term`(`term_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 77 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for group_invitations
-- ----------------------------
DROP TABLE IF EXISTS `group_invitations`;
CREATE TABLE `group_invitations`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `inviter_user_id` bigint NOT NULL,
  `invitee_user_id` bigint NOT NULL,
  `status` enum('ACCEPTED','CANCELLED','EXPIRED','PENDING','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `responded_at` datetime NULL DEFAULT NULL,
  `expired_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_group_invitations_group`(`group_id` ASC) USING BTREE,
  INDEX `idx_group_invitee`(`invitee_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for group_join_requests
-- ----------------------------
DROP TABLE IF EXISTS `group_join_requests`;
CREATE TABLE `group_join_requests`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `requester_user_id` bigint NOT NULL,
  `status` enum('APPROVED','CANCELLED','PENDING','REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `responded_at` datetime NULL DEFAULT NULL,
  `responded_by_user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_group_join_requests_group`(`group_id` ASC) USING BTREE,
  INDEX `idx_group_request_user`(`requester_user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for group_members
-- ----------------------------
DROP TABLE IF EXISTS `group_members`;
CREATE TABLE `group_members`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role` enum('ADMIN','MEMBER','OWNER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('ACTIVE','LEFT','REMOVED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `joined_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_member`(`group_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_group_member_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_session_participants
-- ----------------------------
DROP TABLE IF EXISTS `group_session_participants`;
CREATE TABLE `group_session_participants`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `status` enum('PENDING','JOINED','DECLINED','ABSENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `responded_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_participant`(`session_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_study_sessions
-- ----------------------------
DROP TABLE IF EXISTS `group_study_sessions`;
CREATE TABLE `group_study_sessions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `study_mode` enum('ONLINE','OFFLINE','HYBRID') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ONLINE',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_by_user_id` bigint NOT NULL,
  `status` enum('SCHEDULED','COMPLETED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SCHEDULED',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_group_sessions_group`(`group_id` ASC) USING BTREE,
  INDEX `idx_group_session_time`(`start_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for study_groups
-- ----------------------------
DROP TABLE IF EXISTS `study_groups`;
CREATE TABLE `study_groups`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_type` enum('COMMUNITY','STUDY') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_by_user_id` bigint NOT NULL COMMENT 'Người tạo nhóm, có thể là ADMIN hoặc STUDENT',
  `owner_user_id` bigint NULL DEFAULT NULL COMMENT 'Trưởng nhóm, chỉ áp dụng cho nhóm STUDY',
  `term_id` bigint NULL DEFAULT NULL,
  `main_subject_id` bigint NULL DEFAULT NULL,
  `subject_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `max_members` int NULL DEFAULT NULL COMMENT 'Chỉ áp dụng cho nhóm STUDY',
  `visibility` enum('COMMUNITY','PRIVATE','PUBLIC') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('ACTIVE','ARCHIVED','DELETED','INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_type`(`group_type` ASC) USING BTREE,
  INDEX `idx_group_created_by`(`created_by_user_id` ASC) USING BTREE,
  INDEX `idx_group_owner`(`owner_user_id` ASC) USING BTREE,
  INDEX `idx_group_term`(`term_id` ASC) USING BTREE,
  INDEX `idx_group_subject`(`main_subject_id` ASC) USING BTREE,
  INDEX `idx_group_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for study_session_attendance_logs
-- ----------------------------
DROP TABLE IF EXISTS `study_session_attendance_logs`;
CREATE TABLE `study_session_attendance_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `participant_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `joined_at` datetime NOT NULL,
  `left_at` datetime NULL DEFAULT NULL,
  `duration_seconds` bigint NULL DEFAULT NULL,
  `leave_reason` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attendance_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_attendance_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_attendance_participant`(`participant_id` ASC) USING BTREE,
  INDEX `idx_attendance_open_log`(`session_id` ASC, `user_id` ASC, `left_at` ASC) USING BTREE,
  CONSTRAINT `FK2tp1mckioil2oxmq5j8bkcpjq` FOREIGN KEY (`participant_id`) REFERENCES `study_session_participants` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKa8gj9ftlwvn2pwurlaig0s4wn` FOREIGN KEY (`session_id`) REFERENCES `study_sessions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for study_session_feedbacks
-- ----------------------------
DROP TABLE IF EXISTS `study_session_feedbacks`;
CREATE TABLE `study_session_feedbacks`  (
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
  INDEX `idx_feedback_group`(`group_id` ASC) USING BTREE,
  CONSTRAINT `FK4lonhj2u127ff37s88t50w1dx` FOREIGN KEY (`session_id`) REFERENCES `study_sessions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for study_session_participants
-- ----------------------------
DROP TABLE IF EXISTS `study_session_participants`;
CREATE TABLE `study_session_participants`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PARTICIPANT',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `responded_at` datetime NULL DEFAULT NULL,
  `joined_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `first_joined_at` datetime NULL DEFAULT NULL,
  `last_left_at` datetime NULL DEFAULT NULL,
  `total_duration_seconds` bigint NULL DEFAULT 0,
  `join_count` int NULL DEFAULT 0,
  `attendance_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_session_user`(`session_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_participant_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_participant_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_participant_status`(`status` ASC) USING BTREE,
  INDEX `idx_participant_role`(`role` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for study_session_reminders
-- ----------------------------
DROP TABLE IF EXISTS `study_session_reminders`;
CREATE TABLE `study_session_reminders`  (
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
  INDEX `idx_reminder_sent`(`is_sent` ASC) USING BTREE,
  CONSTRAINT `FK7g828r0975d84w517sh8hunfw` FOREIGN KEY (`session_id`) REFERENCES `study_sessions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for study_sessions
-- ----------------------------
DROP TABLE IF EXISTS `study_sessions`;
CREATE TABLE `study_sessions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NULL DEFAULT NULL,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `subject_id` bigint NULL DEFAULT NULL,
  `subject_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `study_mode` enum('HYBRID','OFFLINE','ONLINE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `session_type` enum('GROUP','USER_PAIR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `meeting_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_by_user_id` bigint NOT NULL,
  `status` enum('CANCELLED','COMPLETED','ONGOING','SCHEDULED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reminder_sent` bit(1) NULL DEFAULT NULL,
  `room_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `recurrence_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `recurrence_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_study_sessions_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_study_sessions_created_by`(`created_by_user_id` ASC) USING BTREE,
  INDEX `idx_study_sessions_status`(`status` ASC) USING BTREE,
  INDEX `idx_study_sessions_session_type`(`session_type` ASC) USING BTREE,
  INDEX `idx_study_sessions_start_time`(`start_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_free_time_slots
-- ----------------------------
DROP TABLE IF EXISTS `user_free_time_slots`;
CREATE TABLE `user_free_time_slots`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NULL DEFAULT NULL COMMENT 'Nếu thời gian rảnh thay đổi theo học kỳ',
  `day_of_week` tinyint NOT NULL COMMENT '0=Monday, 1=Tuesday, ..., 6=Sunday',
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT 1,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term_day_time`(`user_id` ASC, `term_id` ASC, `day_of_week` ASC, `start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_user_free_time_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_free_time_day`(`day_of_week` ASC) USING BTREE,
  INDEX `idx_user_free_time_term`(`term_id` ASC) USING BTREE,
  CONSTRAINT `chk_user_free_time_day` CHECK (`day_of_week` between 0 and 6),
  CONSTRAINT `chk_user_free_time_range` CHECK (`end_time` > `start_time`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
