/*
 Navicat Premium Dump SQL

 Source Server         : ss
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45-google)
 Source Host           : 35.198.241.223:3306
 Source Schema         : chat_service_v4

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45-google)
 File Encoding         : 65001

 Date: 01/08/2026 14:44:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_restrictions
-- ----------------------------
DROP TABLE IF EXISTS `chat_restrictions`;
CREATE TABLE `chat_restrictions`  (
  `restriction_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `conversation_id` bigint NULL DEFAULT NULL,
  `restriction_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `scope` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `start_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `end_at` datetime(6) NULL DEFAULT NULL,
  `active` bit(1) NOT NULL DEFAULT b'1',
  `created_by` bigint NULL DEFAULT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`restriction_id`) USING BTREE,
  INDEX `idx_restriction_user_active`(`user_id` ASC, `active` ASC) USING BTREE,
  INDEX `idx_restriction_conversation_user_active`(`conversation_id` ASC, `user_id` ASC, `active` ASC) USING BTREE,
  INDEX `idx_restriction_end_at`(`end_at` ASC) USING BTREE,
  CONSTRAINT `fk_restriction_conversation` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for conversation_participants
-- ----------------------------
DROP TABLE IF EXISTS `conversation_participants`;
CREATE TABLE `conversation_participants`  (
  `is_muted` bit(1) NOT NULL DEFAULT b'0',
  `is_pinned` bit(1) NOT NULL DEFAULT b'0',
  `conversation_id` bigint NOT NULL,
  `joined_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `left_at` datetime(6) NULL DEFAULT NULL,
  `participant_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`participant_id`) USING BTREE,
  INDEX `FK84npv3fo2vwl7ut63im0p417q`(`conversation_id` ASC) USING BTREE,
  CONSTRAINT `FK84npv3fo2vwl7ut63im0p417q` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for conversations
-- ----------------------------
DROP TABLE IF EXISTS `conversations`;
CREATE TABLE `conversations`  (
  `conversation_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `conversation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `font` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`conversation_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for group_conversations
-- ----------------------------
DROP TABLE IF EXISTS `group_conversations`;
CREATE TABLE `group_conversations`  (
  `conversation_id` bigint NOT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`conversation_id`) USING BTREE,
  CONSTRAINT `FKgyygig5xl5icwvqgouoqt523d` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for message_reactions
-- ----------------------------
DROP TABLE IF EXISTS `message_reactions`;
CREATE TABLE `message_reactions`  (
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `message_id` bigint NOT NULL,
  `reaction_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `emoji` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`reaction_id`) USING BTREE,
  INDEX `FK1o714y33gam6b6741ci4ho041`(`message_id` ASC) USING BTREE,
  CONSTRAINT `FK1o714y33gam6b6741ci4ho041` FOREIGN KEY (`message_id`) REFERENCES `messages` (`message_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for message_read_receipts
-- ----------------------------
DROP TABLE IF EXISTS `message_read_receipts`;
CREATE TABLE `message_read_receipts`  (
  `message_id` bigint NOT NULL,
  `read_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `receipt_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`receipt_id`) USING BTREE,
  INDEX `FK37k9gws80wf2nbm7nmj2y0dab`(`message_id` ASC) USING BTREE,
  CONSTRAINT `FK37k9gws80wf2nbm7nmj2y0dab` FOREIGN KEY (`message_id`) REFERENCES `messages` (`message_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for message_status
-- ----------------------------
DROP TABLE IF EXISTS `message_status`;
CREATE TABLE `message_status`  (
  `status_id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `last_seen_message_id` bigint NULL DEFAULT NULL,
  `last_delivered_message_id` bigint NULL DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`status_id`) USING BTREE,
  UNIQUE INDEX `uq_conversation_user`(`conversation_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_last_seen_message`(`last_seen_message_id` ASC) USING BTREE,
  INDEX `idx_last_delivered_message`(`last_delivered_message_id` ASC) USING BTREE,
  CONSTRAINT `fk_message_status_conversation` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_message_status_last_delivered` FOREIGN KEY (`last_delivered_message_id`) REFERENCES `messages` (`message_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_message_status_last_seen` FOREIGN KEY (`last_seen_message_id`) REFERENCES `messages` (`message_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `is_edited` bit(1) NOT NULL DEFAULT b'0',
  `conversation_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `deleted_at` datetime(6) NULL DEFAULT NULL,
  `edited_at` datetime(6) NULL DEFAULT NULL,
  `moderation_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `file_size` bigint NULL DEFAULT NULL,
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `reply_to_id` bigint NULL DEFAULT NULL,
  `sender_id` bigint NULL DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'text',
  `media_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `pinned` enum('Y','N') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `FKt492th6wsovh1nush5yl5jj8e`(`conversation_id` ASC) USING BTREE,
  INDEX `FKg23x99if9xk265onv7btb0cg9`(`reply_to_id` ASC) USING BTREE,
  CONSTRAINT `FKg23x99if9xk265onv7btb0cg9` FOREIGN KEY (`reply_to_id`) REFERENCES `messages` (`message_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `FKt492th6wsovh1nush5yl5jj8e` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 681 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for private_conversations
-- ----------------------------
DROP TABLE IF EXISTS `private_conversations`;
CREATE TABLE `private_conversations`  (
  `conversation_id` bigint NOT NULL,
  `user1_id` bigint NOT NULL,
  `user2_id` bigint NOT NULL,
  PRIMARY KEY (`conversation_id`) USING BTREE,
  CONSTRAINT `FK9c9c6gjs3kid26b45bx4fcedf` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_call_participants
-- ----------------------------
DROP TABLE IF EXISTS `video_call_participants`;
CREATE TABLE `video_call_participants`  (
  `joined_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `left_at` datetime(6) NULL DEFAULT NULL,
  `session_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `video_call_participant_id` bigint NOT NULL,
  PRIMARY KEY (`video_call_participant_id`) USING BTREE,
  INDEX `FKqdyodx5y8xdb82ta3h5vbsjjb`(`session_id` ASC) USING BTREE,
  CONSTRAINT `FKqdyodx5y8xdb82ta3h5vbsjjb` FOREIGN KEY (`session_id`) REFERENCES `video_call_sessions` (`session_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for video_call_sessions
-- ----------------------------
DROP TABLE IF EXISTS `video_call_sessions`;
CREATE TABLE `video_call_sessions`  (
  `duration_seconds` int NULL DEFAULT NULL,
  `conversation_id` bigint NOT NULL,
  `ended_at` datetime(6) NULL DEFAULT NULL,
  `session_id` bigint NOT NULL,
  `started_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `recording_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `FKdg4u2do1k032r4vgtb8s30lhm`(`conversation_id` ASC) USING BTREE,
  CONSTRAINT `FKdg4u2do1k032r4vgtb8s30lhm` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`conversation_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
