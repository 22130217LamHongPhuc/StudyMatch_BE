/*
 Navicat Premium Dump SQL

 Source Server         : ss
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45-google)
 Source Host           : 35.198.241.223:3306
 Source Schema         : db_profile_service

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45-google)
 File Encoding         : 65001

 Date: 01/08/2026 14:43:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for academic_terms
-- ----------------------------
DROP TABLE IF EXISTS `academic_terms`;
CREATE TABLE `academic_terms`  (
  `term_id` bigint NOT NULL AUTO_INCREMENT,
  `academic_year_start` smallint NULL DEFAULT NULL,
  `academic_year_end` smallint NULL DEFAULT NULL,
  `semester_no` tinyint NULL DEFAULT NULL,
  `full_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`term_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cohorts
-- ----------------------------
DROP TABLE IF EXISTS `cohorts`;
CREATE TABLE `cohorts`  (
  `cohort_id` bigint NOT NULL AUTO_INCREMENT,
  `cohort_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `curriculum_id` bigint NULL DEFAULT NULL,
  `start_academic_year` int NULL DEFAULT NULL,
  `total_study_years` tinyint NOT NULL DEFAULT 4 COMMENT 'Số năm đào tạo',
  PRIMARY KEY (`cohort_id`) USING BTREE,
  INDEX `idx_cohort_code`(`cohort_code` ASC) USING BTREE,
  INDEX `fk_cohorts_curriculum`(`curriculum_id` ASC) USING BTREE,
  CONSTRAINT `fk_cohorts_curriculum` FOREIGN KEY (`curriculum_id`) REFERENCES `curriculums` (`curriculum_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for curriculum_term_subjects
-- ----------------------------
DROP TABLE IF EXISTS `curriculum_term_subjects`;
CREATE TABLE `curriculum_term_subjects`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `curriculum_id` bigint NOT NULL,
  `study_year_no` tinyint NOT NULL,
  `semester_no` tinyint NOT NULL,
  `subject_id` bigint NOT NULL,
  `is_required` tinyint(1) NULL DEFAULT 1,
  `recommended_order` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cts`(`curriculum_id` ASC, `study_year_no` ASC, `semester_no` ASC, `subject_id` ASC) USING BTREE,
  INDEX `fk_cts_subject`(`subject_id` ASC) USING BTREE,
  CONSTRAINT `fk_cts_curriculum` FOREIGN KEY (`curriculum_id`) REFERENCES `curriculums` (`curriculum_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cts_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 283 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for curriculums
-- ----------------------------
DROP TABLE IF EXISTS `curriculums`;
CREATE TABLE `curriculums`  (
  `curriculum_id` bigint NOT NULL AUTO_INCREMENT,
  `curriculum_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `curriculum_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`curriculum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student_free_time_slots
-- ----------------------------
DROP TABLE IF EXISTS `student_free_time_slots`;
CREATE TABLE `student_free_time_slots`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `day_of_week` tinyint NOT NULL COMMENT '0=Mon ... 6=Sun',
  `slot_code` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_available` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term_day_slot`(`user_id` ASC, `term_id` ASC, `day_of_week` ASC, `slot_code` ASC) USING BTREE,
  INDEX `fk_free_slots_term`(`term_id` ASC) USING BTREE,
  CONSTRAINT `fk_free_slots_term` FOREIGN KEY (`term_id`) REFERENCES `academic_terms` (`term_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_free_slots_user` FOREIGN KEY (`user_id`) REFERENCES `student_profiles` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 562 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student_profiles
-- ----------------------------
DROP TABLE IF EXISTS `student_profiles`;
CREATE TABLE `student_profiles`  (
  `profile_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `student_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `age_group` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `region` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `cohort_id` bigint NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`profile_id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  UNIQUE INDEX `uk_student_code`(`student_code` ASC) USING BTREE,
  INDEX `fk_student_cohort`(`cohort_id` ASC) USING BTREE,
  CONSTRAINT `fk_student_cohort` FOREIGN KEY (`cohort_id`) REFERENCES `cohorts` (`cohort_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student_subject_enrollments
-- ----------------------------
DROP TABLE IF EXISTS `student_subject_enrollments`;
CREATE TABLE `student_subject_enrollments`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_enroll`(`user_id` ASC, `term_id` ASC, `subject_id` ASC) USING BTREE,
  INDEX `fk_enroll_term`(`term_id` ASC) USING BTREE,
  INDEX `fk_enroll_subject`(`subject_id` ASC) USING BTREE,
  CONSTRAINT `fk_enroll_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_enroll_term` FOREIGN KEY (`term_id`) REFERENCES `academic_terms` (`term_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_enroll_user` FOREIGN KEY (`user_id`) REFERENCES `student_profiles` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 246 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student_subject_schedule_slots
-- ----------------------------
DROP TABLE IF EXISTS `student_subject_schedule_slots`;
CREATE TABLE `student_subject_schedule_slots`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `day_of_week` tinyint NOT NULL COMMENT '0=Mon ... 6=Sun',
  `slot_code` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `schedule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `location` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term_subject_day_slot`(`user_id` ASC, `term_id` ASC, `subject_id` ASC, `day_of_week` ASC, `slot_code` ASC) USING BTREE,
  INDEX `idx_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `idx_subject`(`subject_id` ASC) USING BTREE,
  INDEX `fk_schedule_term`(`term_id` ASC) USING BTREE,
  CONSTRAINT `fk_schedule_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_schedule_term` FOREIGN KEY (`term_id`) REFERENCES `academic_terms` (`term_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_schedule_user` FOREIGN KEY (`user_id`) REFERENCES `student_profiles` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 463 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for student_term_profiles
-- ----------------------------
DROP TABLE IF EXISTS `student_term_profiles`;
CREATE TABLE `student_term_profiles`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `study_year_no` tinyint NOT NULL,
  `semester_no` tinyint NOT NULL,
  `avg_score` decimal(4, 2) NULL DEFAULT NULL,
  `studied_credits` int NULL DEFAULT NULL,
  `study_goal` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `study_mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `main_subject_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term`(`user_id` ASC, `term_id` ASC) USING BTREE,
  INDEX `fk_term_profile_term`(`term_id` ASC) USING BTREE,
  CONSTRAINT `fk_term_profile_term` FOREIGN KEY (`term_id`) REFERENCES `academic_terms` (`term_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_term_profile_user` FOREIGN KEY (`user_id`) REFERENCES `student_profiles` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for subjects
-- ----------------------------
DROP TABLE IF EXISTS `subjects`;
CREATE TABLE `subjects`  (
  `subject_id` bigint NOT NULL AUTO_INCREMENT,
  `subject_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `subject_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`subject_id`) USING BTREE,
  UNIQUE INDEX `uk_subject_code`(`subject_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 77 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
