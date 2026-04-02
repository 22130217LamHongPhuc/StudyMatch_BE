-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.27-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for ai_service_v3
CREATE DATABASE IF NOT EXISTS `ai_service_v3` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `ai_service_v3`;

-- Dumping structure for table ai_service_v3.feedbacks
CREATE TABLE IF NOT EXISTS `feedbacks` (
  `feedback_id` int(11) NOT NULL AUTO_INCREMENT,
  `rater_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL,
  `target_type` tinytext NOT NULL,
  `score` tinyint(4) NOT NULL CHECK (`score` between 1 and 5),
  `comment` tinytext DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`feedback_id`),
  KEY `idx_target` (`target_id`,`target_type`(255)),
  KEY `idx_rater` (`rater_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table ai_service_v3.feedbacks: ~0 rows (approximately)

-- Dumping structure for table ai_service_v3.interaction_metrics
CREATE TABLE IF NOT EXISTS `interaction_metrics` (
  `metric_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `target_id` int(11) NOT NULL,
  `target_type` tinytext NOT NULL,
  `message_count` int(11) DEFAULT 0,
  `video_call_duration_minutes` float DEFAULT 0,
  `last_interaction_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`metric_id`),
  UNIQUE KEY `unique_interaction` (`user_id`,`target_id`,`target_type`) USING HASH,
  KEY `idx_user` (`user_id`),
  KEY `idx_target` (`target_id`,`target_type`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table ai_service_v3.interaction_metrics: ~0 rows (approximately)

-- Dumping structure for table ai_service_v3.match_history
CREATE TABLE IF NOT EXISTS `match_history` (
  `match_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `suggested_group_id` int(11) DEFAULT NULL,
  `match_score` float DEFAULT NULL,
  `action` tinytext DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`match_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_group` (`suggested_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table ai_service_v3.match_history: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
