/*
 Navicat Premium Dump SQL

 Source Server         : ss
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45-google)
 Source Host           : 35.198.241.223:3306
 Source Schema         : db_matching_service

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45-google)
 File Encoding         : 65001

 Date: 01/08/2026 14:44:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for matching_items
-- ----------------------------
DROP TABLE IF EXISTS `matching_items`;
CREATE TABLE `matching_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `recommended_user_id` bigint NOT NULL,
  `final_score` double NULL DEFAULT 0,
  `reason_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `action_status` enum('FRIEND_REQUEST_SENT','NONE','REJECTED','VIEWED','ACCEPTED','SKIPPED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `viewed_at` timestamp NULL DEFAULT NULL,
  `request_sent_at` timestamp NULL DEFAULT NULL,
  `responded_at` timestamp NULL DEFAULT NULL,
  `reject_type` enum('RECOMMENDATION_REJECTED','REQUEST_REJECTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `is_recommendation` tinyint(1) NOT NULL DEFAULT 1,
  `count` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_matching_items_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_matching_items_recommended_user_id`(`recommended_user_id` ASC) USING BTREE,
  INDEX `idx_matching_items_action_status`(`action_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for study_feedbacks
-- ----------------------------
DROP TABLE IF EXISTS `study_feedbacks`;
CREATE TABLE `study_feedbacks`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `reviewer_user_id` bigint NOT NULL,
  `session_type` enum('GROUP','USER_PAIR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_user_id` bigint NULL DEFAULT NULL,
  `group_id` bigint NULL DEFAULT NULL,
  `rating` int NOT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `feedback_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SESSION_FEEDBACK',
  `eligible_for_model` tinyint(1) NULL DEFAULT 0,
  `matched_quality_score` int NULL DEFAULT NULL,
  `communication_score` int NULL DEFAULT NULL,
  `study_effectiveness_score` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_study_feedbacks_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_study_feedbacks_reviewer_user_id`(`reviewer_user_id` ASC) USING BTREE,
  INDEX `idx_study_feedbacks_target_user_id`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_study_feedbacks_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_study_feedbacks_type`(`feedback_type` ASC) USING BTREE,
  INDEX `idx_study_feedbacks_model`(`eligible_for_model` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
