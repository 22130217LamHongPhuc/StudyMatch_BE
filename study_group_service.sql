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


-- Dumping database structure for study_group_service_v3
CREATE DATABASE IF NOT EXISTS `study_group_service_v3` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `study_group_service_v3`;

-- Dumping structure for table study_group_service_v3.group_invitations
CREATE TABLE IF NOT EXISTS `group_invitations` (
  `invitation_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `inviter_id` int(11) NOT NULL,
  `invitee_id` int(11) NOT NULL,
  `status` tinytext DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`invitation_id`),
  UNIQUE KEY `unique_invitation` (`group_id`,`invitee_id`),
  KEY `idx_invitee` (`invitee_id`),
  KEY `idx_status` (`status`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table study_group_service_v3.group_invitations: ~0 rows (approximately)

-- Dumping structure for table study_group_service_v3.group_join_requests
CREATE TABLE IF NOT EXISTS `group_join_requests` (
  `request_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `message` tinytext DEFAULT NULL,
  `status` tinytext DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `responded_at` datetime DEFAULT NULL,
  PRIMARY KEY (`request_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_group` (`group_id`),
  KEY `idx_status` (`status`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table study_group_service_v3.group_join_requests: ~0 rows (approximately)

-- Dumping structure for table study_group_service_v3.group_members
CREATE TABLE IF NOT EXISTS `group_members` (
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `role` tinytext DEFAULT 'member',
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`group_id`,`user_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_role` (`role`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table study_group_service_v3.group_members: ~0 rows (approximately)

-- Dumping structure for table study_group_service_v3.schedules
CREATE TABLE IF NOT EXISTS `schedules` (
  `schedule_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `start_datetime` datetime NOT NULL,
  `end_datetime` datetime NOT NULL,
  `meeting_link` varchar(255) DEFAULT NULL,
  `is_online` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`schedule_id`),
  KEY `idx_group` (`group_id`),
  KEY `idx_start_time` (`start_datetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table study_group_service_v3.schedules: ~0 rows (approximately)

-- Dumping structure for table study_group_service_v3.study_groups
CREATE TABLE IF NOT EXISTS `study_groups` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(100) NOT NULL,
  `description` tinytext DEFAULT NULL,
  `subject_id` int(11) DEFAULT NULL,
  `max_capacity` int(11) DEFAULT 10,
  `status` tinytext DEFAULT 'active',
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`group_id`),
  KEY `idx_status` (`status`(255)),
  KEY `idx_subject` (`subject_id`),
  KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table study_group_service_v3.study_groups: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
