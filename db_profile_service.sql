/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : db_profile_service

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 06/04/2026 13:07:37
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
) ENGINE = MyISAM AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of academic_terms
-- ----------------------------
INSERT INTO `academic_terms` VALUES (1, 2022, 2023, 1, 'Học kỳ 1 - Năm học 2022 - 2023', 'completed');
INSERT INTO `academic_terms` VALUES (2, 2022, 2023, 2, 'Học kỳ 2 - Năm học 2022 - 2023', 'completed');
INSERT INTO `academic_terms` VALUES (3, 2023, 2024, 1, 'Học kỳ 1 - Năm học 2023 - 2024', 'completed');
INSERT INTO `academic_terms` VALUES (4, 2023, 2024, 2, 'Học kỳ 2 - Năm học 2023 - 2024', 'completed');
INSERT INTO `academic_terms` VALUES (5, 2024, 2025, 1, 'Học kỳ 1 - Năm học 2024 - 2025', 'completed');
INSERT INTO `academic_terms` VALUES (6, 2024, 2025, 2, 'Học kỳ 2 - Năm học 2024 - 2025', 'completed');
INSERT INTO `academic_terms` VALUES (7, 2025, 2026, 1, 'Học kỳ 1 - Năm học 2025 - 2026', 'completed');
INSERT INTO `academic_terms` VALUES (8, 2025, 2026, 2, 'Học kỳ 2 - Năm học 2025 - 2026', 'active');
INSERT INTO `academic_terms` VALUES (9, 2026, 2027, 1, 'Học kỳ 1 - Năm học 2026 - 2027', 'planned');
INSERT INTO `academic_terms` VALUES (10, 2026, 2027, 2, 'Học kỳ 2 - Năm học 2026 - 2027', 'planned');

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
  INDEX `idx_cohort_code`(`cohort_code`) USING BTREE,
  INDEX `FKljcl4fvg9f7g43v5dd6gf5dn2`(`curriculum_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cohorts
-- ----------------------------
INSERT INTO `cohorts` VALUES (1, '48', 1, 2022, 4);
INSERT INTO `cohorts` VALUES (2, '49', 2, 2023, 4);
INSERT INTO `cohorts` VALUES (3, '46', 3, 2020, 4);
INSERT INTO `cohorts` VALUES (4, '47', 4, 2021, 4);

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
  UNIQUE INDEX `uk_cts`(`curriculum_id`, `study_year_no`, `semester_no`, `subject_id`) USING BTREE,
  INDEX `FK29o1vakinqros8d3ya54lfv8p`(`subject_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 278 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of curriculum_term_subjects
-- ----------------------------
INSERT INTO `curriculum_term_subjects` VALUES (1, 1, 1, 1, 1, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (2, 1, 1, 1, 2, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (3, 1, 1, 1, 3, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (4, 1, 1, 1, 4, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (5, 1, 1, 1, 5, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (6, 1, 1, 1, 6, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (7, 1, 1, 1, 7, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (8, 1, 1, 1, 8, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (9, 1, 1, 2, 9, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (10, 1, 1, 2, 10, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (11, 1, 1, 2, 11, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (12, 1, 1, 2, 12, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (13, 1, 1, 2, 13, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (14, 1, 1, 2, 14, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (15, 1, 1, 2, 15, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (16, 1, 1, 2, 16, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (17, 1, 1, 2, 17, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (18, 1, 2, 1, 18, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (19, 1, 2, 1, 19, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (20, 1, 2, 1, 20, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (21, 1, 2, 1, 21, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (22, 1, 2, 1, 22, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (23, 1, 2, 1, 23, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (24, 1, 2, 1, 24, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (25, 1, 2, 1, 25, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (26, 1, 2, 2, 26, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (27, 1, 2, 2, 27, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (28, 1, 2, 2, 28, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (29, 1, 2, 2, 29, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (30, 1, 2, 2, 30, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (31, 1, 2, 2, 31, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (32, 1, 3, 1, 32, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (33, 1, 3, 1, 33, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (34, 1, 3, 1, 34, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (35, 1, 3, 1, 35, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (36, 1, 3, 1, 36, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (37, 1, 3, 1, 37, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (38, 1, 3, 1, 38, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (39, 1, 3, 1, 39, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (40, 1, 3, 1, 40, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (41, 1, 3, 2, 41, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (42, 1, 3, 2, 42, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (43, 1, 3, 2, 43, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (44, 1, 3, 2, 44, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (45, 1, 3, 2, 45, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (46, 1, 3, 2, 46, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (47, 1, 3, 2, 47, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (48, 1, 3, 2, 48, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (49, 1, 4, 1, 49, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (50, 1, 4, 1, 50, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (51, 1, 4, 1, 51, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (52, 1, 4, 1, 52, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (53, 1, 4, 1, 53, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (54, 1, 4, 1, 54, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (55, 1, 4, 1, 55, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (56, 1, 4, 1, 56, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (57, 1, 4, 1, 57, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (58, 1, 4, 1, 58, 0, 10);
INSERT INTO `curriculum_term_subjects` VALUES (59, 1, 4, 1, 59, 0, 11);
INSERT INTO `curriculum_term_subjects` VALUES (60, 1, 4, 2, 60, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (61, 1, 4, 2, 61, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (62, 1, 4, 2, 62, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (63, 1, 4, 2, 63, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (64, 1, 4, 2, 64, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (65, 1, 4, 2, 65, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (66, 1, 4, 2, 66, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (67, 1, 4, 2, 67, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (68, 1, 4, 2, 68, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (69, 1, 4, 2, 69, 1, 10);
INSERT INTO `curriculum_term_subjects` VALUES (70, 2, 1, 1, 1, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (71, 2, 1, 1, 2, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (72, 2, 1, 1, 3, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (73, 2, 1, 1, 4, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (74, 2, 1, 1, 5, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (75, 2, 1, 1, 6, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (76, 2, 1, 1, 7, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (77, 2, 1, 1, 8, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (78, 2, 1, 2, 9, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (79, 2, 1, 2, 10, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (80, 2, 1, 2, 11, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (81, 2, 1, 2, 12, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (82, 2, 1, 2, 13, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (83, 2, 1, 2, 14, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (84, 2, 1, 2, 15, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (85, 2, 1, 2, 16, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (86, 2, 1, 2, 17, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (87, 2, 2, 1, 18, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (88, 2, 2, 1, 19, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (89, 2, 2, 1, 20, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (90, 2, 2, 1, 21, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (91, 2, 2, 1, 22, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (92, 2, 2, 1, 23, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (93, 2, 2, 1, 24, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (94, 2, 2, 1, 25, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (95, 2, 2, 2, 26, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (96, 2, 2, 2, 27, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (97, 2, 2, 2, 28, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (98, 2, 2, 2, 29, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (99, 2, 2, 2, 30, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (100, 2, 2, 2, 31, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (101, 2, 3, 1, 32, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (102, 2, 3, 1, 33, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (103, 2, 3, 1, 34, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (104, 2, 3, 1, 35, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (105, 2, 3, 1, 36, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (106, 2, 3, 1, 37, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (107, 2, 3, 1, 38, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (108, 2, 3, 1, 39, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (109, 2, 3, 1, 40, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (110, 2, 3, 2, 41, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (111, 2, 3, 2, 42, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (112, 2, 3, 2, 43, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (113, 2, 3, 2, 44, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (114, 2, 3, 2, 45, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (115, 2, 3, 2, 46, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (116, 2, 3, 2, 47, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (117, 2, 3, 2, 48, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (118, 2, 4, 1, 49, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (119, 2, 4, 1, 50, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (120, 2, 4, 1, 51, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (121, 2, 4, 1, 52, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (122, 2, 4, 1, 53, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (123, 2, 4, 1, 54, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (124, 2, 4, 1, 55, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (125, 2, 4, 1, 56, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (126, 2, 4, 1, 57, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (127, 2, 4, 1, 58, 0, 10);
INSERT INTO `curriculum_term_subjects` VALUES (128, 2, 4, 1, 59, 0, 11);
INSERT INTO `curriculum_term_subjects` VALUES (129, 2, 4, 2, 60, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (130, 2, 4, 2, 61, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (131, 2, 4, 2, 62, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (132, 2, 4, 2, 63, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (133, 2, 4, 2, 64, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (134, 2, 4, 2, 65, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (135, 2, 4, 2, 66, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (136, 2, 4, 2, 67, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (137, 2, 4, 2, 68, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (138, 3, 1, 1, 1, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (139, 3, 1, 1, 2, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (140, 3, 1, 1, 3, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (141, 3, 1, 1, 4, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (142, 3, 1, 1, 5, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (143, 3, 1, 1, 6, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (144, 3, 1, 1, 7, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (145, 3, 1, 1, 8, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (146, 3, 1, 1, 69, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (147, 3, 1, 2, 9, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (148, 3, 1, 2, 10, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (149, 3, 1, 2, 11, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (150, 3, 1, 2, 12, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (151, 3, 1, 2, 13, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (152, 3, 1, 2, 14, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (153, 3, 1, 2, 15, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (154, 3, 1, 2, 16, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (155, 3, 1, 2, 17, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (156, 3, 2, 1, 18, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (157, 3, 2, 1, 19, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (158, 3, 2, 1, 20, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (159, 3, 2, 1, 21, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (160, 3, 2, 1, 22, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (161, 3, 2, 1, 27, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (162, 3, 2, 1, 70, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (163, 3, 2, 1, 24, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (164, 3, 2, 1, 25, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (165, 3, 2, 2, 26, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (166, 3, 2, 2, 28, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (167, 3, 2, 2, 71, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (168, 3, 2, 2, 29, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (169, 3, 2, 2, 34, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (170, 3, 2, 2, 31, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (171, 3, 3, 1, 32, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (172, 3, 3, 1, 33, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (173, 3, 3, 1, 42, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (174, 3, 3, 1, 35, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (175, 3, 3, 1, 36, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (176, 3, 3, 1, 38, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (177, 3, 3, 1, 39, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (178, 3, 3, 1, 40, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (179, 3, 3, 2, 50, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (180, 3, 3, 2, 41, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (181, 3, 3, 2, 43, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (182, 3, 3, 2, 56, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (183, 3, 3, 2, 44, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (184, 3, 3, 2, 63, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (185, 3, 3, 2, 47, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (186, 3, 3, 2, 48, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (187, 3, 4, 1, 72, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (188, 3, 4, 1, 61, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (189, 3, 4, 1, 52, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (190, 3, 4, 1, 53, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (191, 3, 4, 1, 54, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (192, 3, 4, 1, 55, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (193, 3, 4, 1, 45, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (194, 3, 4, 1, 46, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (195, 3, 4, 1, 57, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (196, 3, 4, 1, 59, 0, 10);
INSERT INTO `curriculum_term_subjects` VALUES (197, 3, 4, 2, 49, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (198, 3, 4, 2, 73, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (199, 3, 4, 2, 60, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (200, 3, 4, 2, 51, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (201, 3, 4, 2, 62, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (202, 3, 4, 2, 74, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (203, 3, 4, 2, 64, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (204, 3, 4, 2, 58, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (205, 3, 4, 2, 75, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (206, 3, 4, 2, 66, 0, 10);
INSERT INTO `curriculum_term_subjects` VALUES (207, 3, 4, 2, 67, 0, 11);
INSERT INTO `curriculum_term_subjects` VALUES (208, 4, 1, 1, 1, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (209, 4, 1, 1, 2, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (210, 4, 1, 1, 3, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (211, 4, 1, 1, 4, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (212, 4, 1, 1, 5, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (213, 4, 1, 1, 6, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (214, 4, 1, 1, 7, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (215, 4, 1, 1, 8, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (216, 4, 1, 1, 69, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (217, 4, 1, 2, 9, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (218, 4, 1, 2, 10, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (219, 4, 1, 2, 11, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (220, 4, 1, 2, 12, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (221, 4, 1, 2, 13, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (222, 4, 1, 2, 14, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (223, 4, 1, 2, 15, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (224, 4, 1, 2, 16, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (225, 4, 1, 2, 17, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (226, 4, 2, 1, 18, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (227, 4, 2, 1, 19, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (228, 4, 2, 1, 20, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (229, 4, 2, 1, 21, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (230, 4, 2, 1, 22, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (231, 4, 2, 1, 27, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (232, 4, 2, 1, 70, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (233, 4, 2, 1, 24, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (234, 4, 2, 1, 25, 1, 9);
INSERT INTO `curriculum_term_subjects` VALUES (235, 4, 2, 2, 26, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (236, 4, 2, 2, 28, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (237, 4, 2, 2, 71, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (238, 4, 2, 2, 29, 1, 4);
INSERT INTO `curriculum_term_subjects` VALUES (239, 4, 2, 2, 34, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (240, 4, 2, 2, 31, 1, 6);
INSERT INTO `curriculum_term_subjects` VALUES (241, 4, 3, 1, 32, 1, 1);
INSERT INTO `curriculum_term_subjects` VALUES (242, 4, 3, 1, 33, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (243, 4, 3, 1, 42, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (244, 4, 3, 1, 35, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (245, 4, 3, 1, 36, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (246, 4, 3, 1, 38, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (247, 4, 3, 1, 39, 1, 7);
INSERT INTO `curriculum_term_subjects` VALUES (248, 4, 3, 1, 40, 1, 8);
INSERT INTO `curriculum_term_subjects` VALUES (249, 4, 3, 2, 50, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (250, 4, 3, 2, 41, 1, 2);
INSERT INTO `curriculum_term_subjects` VALUES (251, 4, 3, 2, 43, 1, 3);
INSERT INTO `curriculum_term_subjects` VALUES (252, 4, 3, 2, 56, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (253, 4, 3, 2, 44, 1, 5);
INSERT INTO `curriculum_term_subjects` VALUES (254, 4, 3, 2, 63, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (255, 4, 3, 2, 47, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (256, 4, 3, 2, 48, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (257, 4, 4, 1, 72, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (258, 4, 4, 1, 61, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (259, 4, 4, 1, 52, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (260, 4, 4, 1, 53, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (261, 4, 4, 1, 54, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (262, 4, 4, 1, 55, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (263, 4, 4, 1, 45, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (264, 4, 4, 1, 46, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (265, 4, 4, 1, 57, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (266, 4, 4, 1, 59, 0, 10);
INSERT INTO `curriculum_term_subjects` VALUES (267, 4, 4, 2, 49, 0, 1);
INSERT INTO `curriculum_term_subjects` VALUES (268, 4, 4, 2, 73, 0, 2);
INSERT INTO `curriculum_term_subjects` VALUES (269, 4, 4, 2, 60, 0, 3);
INSERT INTO `curriculum_term_subjects` VALUES (270, 4, 4, 2, 51, 0, 4);
INSERT INTO `curriculum_term_subjects` VALUES (271, 4, 4, 2, 62, 0, 5);
INSERT INTO `curriculum_term_subjects` VALUES (272, 4, 4, 2, 74, 0, 6);
INSERT INTO `curriculum_term_subjects` VALUES (273, 4, 4, 2, 64, 0, 7);
INSERT INTO `curriculum_term_subjects` VALUES (274, 4, 4, 2, 58, 0, 8);
INSERT INTO `curriculum_term_subjects` VALUES (275, 4, 4, 2, 75, 0, 9);
INSERT INTO `curriculum_term_subjects` VALUES (276, 4, 4, 2, 66, 0, 10);
INSERT INTO `curriculum_term_subjects` VALUES (277, 4, 4, 2, 67, 0, 11);

-- ----------------------------
-- Table structure for curriculums
-- ----------------------------
DROP TABLE IF EXISTS `curriculums`;
CREATE TABLE `curriculums`  (
  `curriculum_id` bigint NOT NULL AUTO_INCREMENT,
  `curriculum_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `curriculum_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`curriculum_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of curriculums
-- ----------------------------
INSERT INTO `curriculums` VALUES (1, 'CTDT_K48_CNTT', 'Chương trình đào tạo CNTT khóa 48 (bắt đầu 2022)');
INSERT INTO `curriculums` VALUES (2, 'CTDT_K49_CNTT', 'Chương trình đào tạo CNTT khóa 49 (bắt đầu 2023)');
INSERT INTO `curriculums` VALUES (3, 'CTDT_K46_CNTT', 'Chương trình đào tạo CNTT khóa 46 (bắt đầu 2020)');
INSERT INTO `curriculums` VALUES (4, 'CTDT_K47_CNTT', 'Chương trình đào tạo CNTT khóa 47 (bắt đầu 2021)');

-- ----------------------------
-- Table structure for student_free_time_slots
-- ----------------------------
DROP TABLE IF EXISTS `student_free_time_slots`;
CREATE TABLE `student_free_time_slots`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `term_id` bigint NOT NULL,
  `day_of_week` tinyint NOT NULL COMMENT '0=Mon ... 6=Sun',
  `slot_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'morning, afternoon, evening',
  `is_available` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_term_day_slot`(`user_id` ASC, `term_id` ASC, `day_of_week` ASC, `slot_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_free_time_slots
-- ----------------------------

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
  PRIMARY KEY (`profile_id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  UNIQUE INDEX `uk_student_code`(`student_code`) USING BTREE,
  INDEX `FKllbs3amaks1ix0vx8lej6yfbw`(`cohort_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_profiles
-- ----------------------------

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
  UNIQUE INDEX `uk_enroll`(`user_id`, `term_id`, `subject_id`) USING BTREE,
  INDEX `FKjkjgt1guy06hcxp184bsqxru3`(`subject_id`) USING BTREE,
  INDEX `FKrs90s5v8e4ot4xfcny6rvupdx`(`term_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of student_subject_enrollments
-- ----------------------------

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
  UNIQUE INDEX `uk_user_term`(`user_id`, `term_id`) USING BTREE,
  INDEX `FK2n01yihkfmgypb0ydmstgdfq`(`term_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_term_profiles
-- ----------------------------

-- ----------------------------
-- Table structure for subjects
-- ----------------------------
DROP TABLE IF EXISTS `subjects`;
CREATE TABLE `subjects`  (
  `subject_id` bigint NOT NULL AUTO_INCREMENT,
  `subject_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `subject_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`subject_id`) USING BTREE,
  UNIQUE INDEX `uk_subject_code`(`subject_code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subjects
-- ----------------------------
INSERT INTO `subjects` VALUES (1, '200101', 'Triết học Mác Lênin');
INSERT INTO `subjects` VALUES (2, '202108', 'Toán cao cấp A1');
INSERT INTO `subjects` VALUES (3, '202109', 'Toán cao cấp A2');
INSERT INTO `subjects` VALUES (4, '202206', 'Vật lý 2');
INSERT INTO `subjects` VALUES (5, '202501', 'Giáo dục thể chất 1*');
INSERT INTO `subjects` VALUES (6, '213603', 'Anh văn 1*');
INSERT INTO `subjects` VALUES (7, '214201', 'Nhập môn tin học');
INSERT INTO `subjects` VALUES (8, '214321', 'Lập trình cơ bản');
INSERT INTO `subjects` VALUES (9, '200102', 'Kinh tế chính trị Mác- Lênin');
INSERT INTO `subjects` VALUES (10, '200201', 'Quân sự 1 (lý thuyết)*');
INSERT INTO `subjects` VALUES (11, '200202', 'Quân sự 2 (thực hành)*');
INSERT INTO `subjects` VALUES (12, '202110', 'Toán cao cấp A3');
INSERT INTO `subjects` VALUES (13, '202502', 'Giáo dục thể chất 2*');
INSERT INTO `subjects` VALUES (14, '213604', 'Anh văn 2*');
INSERT INTO `subjects` VALUES (15, '214231', 'Cấu trúc máy tính');
INSERT INTO `subjects` VALUES (16, '214242', 'Nhập môn hệ điều hành');
INSERT INTO `subjects` VALUES (17, '214331', 'Lập trình nâng cao');
INSERT INTO `subjects` VALUES (18, '200103', 'Chủ nghĩa xã hội khoa học');
INSERT INTO `subjects` VALUES (19, '202121', 'Xác suất thống kê');
INSERT INTO `subjects` VALUES (20, '202620', 'Kỹ năng giao tiếp');
INSERT INTO `subjects` VALUES (21, '202622', 'Pháp luật đại cương');
INSERT INTO `subjects` VALUES (22, '208453', 'Marketing căn bản');
INSERT INTO `subjects` VALUES (23, '214362', 'Giao tiếp người-máy');
INSERT INTO `subjects` VALUES (24, '214389', 'Toán rời rạc');
INSERT INTO `subjects` VALUES (25, '214441', 'Cấu trúc dữ liệu');
INSERT INTO `subjects` VALUES (26, '200107', 'Tư tưởng Hồ Chí Minh');
INSERT INTO `subjects` VALUES (27, '214241', 'Mạng máy tính cơ bản');
INSERT INTO `subjects` VALUES (28, '214251', 'Hệ điều hành nâng cao');
INSERT INTO `subjects` VALUES (29, '214352', 'Thiết kế hướng đối tượng');
INSERT INTO `subjects` VALUES (30, '214354', 'Lý thuyết đồ thị');
INSERT INTO `subjects` VALUES (31, '214442', 'Nhập môn cơ sở dữ liệu');
INSERT INTO `subjects` VALUES (32, '200105', 'Lịch sử Đảng Cộng sản Việt Nam');
INSERT INTO `subjects` VALUES (33, '214252', 'Lập trình mạng');
INSERT INTO `subjects` VALUES (34, '214353', 'Đồ họa máy tính');
INSERT INTO `subjects` VALUES (35, '214372', 'Lập trình .NET');
INSERT INTO `subjects` VALUES (36, '214386', 'Lập trình PHP');
INSERT INTO `subjects` VALUES (37, '214390', 'Lập trình Python');
INSERT INTO `subjects` VALUES (38, '214451', 'Hệ quản trị cơ sở dữ liệu');
INSERT INTO `subjects` VALUES (39, '214462', 'Lập trình Web');
INSERT INTO `subjects` VALUES (40, '214463', 'Nhập môn trí tuệ nhân tạo');
INSERT INTO `subjects` VALUES (41, '214274', 'Lập trình trên thiết bị di động');
INSERT INTO `subjects` VALUES (42, '214282', 'Mạng máy tính nâng cao');
INSERT INTO `subjects` VALUES (43, '214370', 'Nhập môn công nghệ phần mềm');
INSERT INTO `subjects` VALUES (44, '214461', 'Phân tích và thiết kế hệ thống thông tin');
INSERT INTO `subjects` VALUES (45, '214464', 'An toàn và bảo mật hệ thống thông tin');
INSERT INTO `subjects` VALUES (46, '214465', 'Hệ thống thông tin địa lý ứng dụng');
INSERT INTO `subjects` VALUES (47, '214492', 'Máy học');
INSERT INTO `subjects` VALUES (48, '214493', 'Thực tập lập trình Web');
INSERT INTO `subjects` VALUES (49, '214271', 'Quản trị mạng');
INSERT INTO `subjects` VALUES (50, '214273', 'Lập trình mạng nâng cao');
INSERT INTO `subjects` VALUES (51, '214291', 'Xử lý ảnh và thị giác máy tính');
INSERT INTO `subjects` VALUES (52, '214292', 'An ninh mạng');
INSERT INTO `subjects` VALUES (53, '214293', 'Thực tập lập trình trên thiết bị di động');
INSERT INTO `subjects` VALUES (54, '214379', 'Đảm bảo chất lượng và kiểm thử phần mềm');
INSERT INTO `subjects` VALUES (55, '214383', 'Quản lý dự án phần mềm');
INSERT INTO `subjects` VALUES (56, '214388', 'Lập trình Front End');
INSERT INTO `subjects` VALUES (57, '214485', 'Data Mining');
INSERT INTO `subjects` VALUES (58, '214490', 'Phân tích dữ liệu lớn');
INSERT INTO `subjects` VALUES (59, '214491', 'Data Warehouse');
INSERT INTO `subjects` VALUES (60, '214286', 'Chuyên đề Java');
INSERT INTO `subjects` VALUES (61, '214290', 'IoT');
INSERT INTO `subjects` VALUES (62, '214374', 'Chuyên đề WEB');
INSERT INTO `subjects` VALUES (63, '214471', 'Hệ thống thông tin quản lý');
INSERT INTO `subjects` VALUES (64, '214483', 'Thương mại điện tử');
INSERT INTO `subjects` VALUES (65, '214984', 'Đồ án chuyên ngành');
INSERT INTO `subjects` VALUES (66, '214987', 'Khóa luận tốt nghiệp');
INSERT INTO `subjects` VALUES (67, '214988', 'Tiểu luận tốt nghiệp');
INSERT INTO `subjects` VALUES (68, '214989', 'Khởi nghiệp');
INSERT INTO `subjects` VALUES (69, 'NN', 'Chuẩn đầu ra B1');
INSERT INTO `subjects` VALUES (70, '214361', 'Giao tiếp người _máy');
INSERT INTO `subjects` VALUES (71, '214351', 'Lý thuyết đồ thị');
INSERT INTO `subjects` VALUES (72, '208407', 'Khởi nghiệp');
INSERT INTO `subjects` VALUES (73, '214285', 'Giải pháp mạng cho doanh nghiệp');
INSERT INTO `subjects` VALUES (74, '214387', 'Chuyên đề mã nguồn mở');
INSERT INTO `subjects` VALUES (75, '214986', 'Đồ án Công nghệ phần mềm');

SET FOREIGN_KEY_CHECKS = 1;
