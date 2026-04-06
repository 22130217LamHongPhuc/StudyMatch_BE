/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : db_matching_service

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 06/04/2026 13:07:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for interaction_summaries
-- ----------------------------
DROP TABLE IF EXISTS `interaction_summaries`;
CREATE TABLE `interaction_summaries`  (
  `interaction_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `peer_user_id` bigint NULL DEFAULT NULL,
  `group_id` bigint NULL DEFAULT NULL,
  `message_count` int NOT NULL DEFAULT 0,
  `video_call_minutes` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `last_interaction_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`interaction_id`) USING BTREE,
  UNIQUE INDEX `uk_interaction_summaries`(`user_id` ASC, `peer_user_id` ASC, `group_id` ASC) USING BTREE,
  INDEX `idx_interaction_summaries_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_interaction_summaries_peer_user_id`(`peer_user_id` ASC) USING BTREE,
  INDEX `idx_interaction_summaries_group_id`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interaction_summaries
-- ----------------------------

-- ----------------------------
-- Table structure for match_actions
-- ----------------------------
DROP TABLE IF EXISTS `match_actions`;
CREATE TABLE `match_actions`  (
  `action_id` bigint NOT NULL AUTO_INCREMENT,
  `recommendation_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `action_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`action_id`) USING BTREE,
  INDEX `idx_match_actions_recommendation_id`(`recommendation_id` ASC) USING BTREE,
  INDEX `idx_match_actions_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_match_actions_action_type`(`action_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_actions
-- ----------------------------

-- ----------------------------
-- Table structure for match_feedbacks
-- ----------------------------
DROP TABLE IF EXISTS `match_feedbacks`;
CREATE TABLE `match_feedbacks`  (
  `feedback_id` bigint NOT NULL AUTO_INCREMENT,
  `rater_user_id` bigint NOT NULL,
  `target_user_id` bigint NULL DEFAULT NULL,
  `target_group_id` bigint NULL DEFAULT NULL,
  `score` tinyint NOT NULL,
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`) USING BTREE,
  INDEX `idx_match_feedbacks_rater_user_id`(`rater_user_id` ASC) USING BTREE,
  INDEX `idx_match_feedbacks_target_user_id`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_match_feedbacks_target_group_id`(`target_group_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_feedbacks
-- ----------------------------

-- ----------------------------
-- Table structure for match_recommendations
-- ----------------------------
DROP TABLE IF EXISTS `match_recommendations`;
CREATE TABLE `match_recommendations`  (
  `recommendation_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `target_user_id` bigint NULL DEFAULT NULL,
  `target_group_id` bigint NULL DEFAULT NULL,
  `recommendation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `score` decimal(8, 4) NOT NULL,
  `reason_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `model_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'shown',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`recommendation_id`) USING BTREE,
  INDEX `idx_match_recommendations_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_match_recommendations_target_user_id`(`target_user_id` ASC) USING BTREE,
  INDEX `idx_match_recommendations_target_group_id`(`target_group_id` ASC) USING BTREE,
  INDEX `idx_match_recommendations_type_status`(`recommendation_type` ASC, `status` ASC) USING BTREE,
  INDEX `idx_match_recommendations_score`(`score` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_recommendations
-- ----------------------------

-- ----------------------------
-- Table structure for matching_jobs
-- ----------------------------
DROP TABLE IF EXISTS `matching_jobs`;
CREATE TABLE `matching_jobs`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `job_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'generate_recommendations',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'queued',
  `requested_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `started_at` datetime NULL DEFAULT NULL,
  `finished_at` datetime NULL DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`job_id`) USING BTREE,
  INDEX `idx_matching_jobs_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_matching_jobs_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of matching_jobs
-- ----------------------------

-- ----------------------------
-- Table structure for matching_profiles
-- ----------------------------
DROP TABLE IF EXISTS `matching_profiles`;
CREATE TABLE `matching_profiles`  (
  `matching_profile_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `study_goal` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `study_mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `main_subject_id` bigint NOT NULL,
  `avg_score` decimal(4, 2) NOT NULL,
  `studied_credits` int NOT NULL DEFAULT 0,
  `prev_attempts` int NOT NULL DEFAULT 0,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `age_group` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `region` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `free_time_vector` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `semester_flags` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `feature_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `generated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`matching_profile_id`) USING BTREE,
  UNIQUE INDEX `uk_matching_profiles_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_matching_profiles_term_id`(`term_id` ASC) USING BTREE,
  INDEX `idx_matching_profiles_study_goal`(`study_goal` ASC) USING BTREE,
  INDEX `idx_matching_profiles_study_mode`(`study_mode` ASC) USING BTREE,
  INDEX `idx_matching_profiles_main_subject_id`(`main_subject_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of matching_profiles
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
