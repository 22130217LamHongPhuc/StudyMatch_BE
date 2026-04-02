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


-- Dumping database structure for user_service_v3
CREATE DATABASE IF NOT EXISTS `user_service_v3` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `user_service_v3`;

-- Dumping structure for table user_service_v3.availabilities
CREATE TABLE IF NOT EXISTS `availabilities` (
  `availability_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `day_of_week` tinytext NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  PRIMARY KEY (`availability_id`),
  KEY `idx_user_day` (`user_id`,`day_of_week`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table user_service_v3.availabilities: ~0 rows (approximately)

-- Dumping structure for table user_service_v3.student_profiles
CREATE TABLE IF NOT EXISTS `student_profiles` (
  `profile_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `bio` tinytext DEFAULT NULL,
  `learning_style` tinytext DEFAULT NULL,
  `gpa` float DEFAULT NULL,
  PRIMARY KEY (`profile_id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_learning_style` (`learning_style`(255))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table user_service_v3.student_profiles: ~4 rows (approximately)
INSERT INTO `student_profiles` (`profile_id`, `user_id`, `bio`, `learning_style`, `gpa`) VALUES
	(1, 5, ' thích Machine Learning và Data Mining', 'SOLITARY', NULL),
	(2, 6, ' thích Machine Learning và Data Mining', 'SOLITARY', NULL),
	(3, 7, ' thích Machine Learning và Data Mining', 'SOLITARY', NULL),
	(4, 8, 'thích Machine Learning và Data Mining', 'SOLITARY', 3.5);

-- Dumping structure for table user_service_v3.subjects
CREATE TABLE IF NOT EXISTS `subjects` (
  `subject_id` int(11) NOT NULL AUTO_INCREMENT,
  `subject_name` varchar(100) NOT NULL,
  `description` tinytext DEFAULT NULL,
  PRIMARY KEY (`subject_id`),
  UNIQUE KEY `subject_name` (`subject_name`),
  KEY `idx_subject_name` (`subject_name`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table user_service_v3.subjects: ~67 rows (approximately)
INSERT INTO `subjects` (`subject_id`, `subject_name`, `description`) VALUES
	(6, 'Triết học Mác Lênin', 'Môn học cơ bản về thế giới quan duy vật biện chứng và duy vật lịch sử, cung cấp nền tảng tư tưởng chính trị cho sinh viên.'),
	(7, 'Toán cao cấp A1', 'Môn toán nền tảng bao gồm giải tích hàm một biến, vi phân, tích phân và ứng dụng trong các lĩnh vực khoa học kỹ thuật.'),
	(8, 'Toán cao cấp A2', 'Môn toán nâng cao về giải tích hàm nhiều biến, tích phân bội, phương trình vi phân và chuỗi số.'),
	(9, 'Vật lý 2', 'Môn học về điện từ học, quang học và các hiện tượng vật lý cơ bản liên quan đến công nghệ thông tin.'),
	(10, 'Giáo dục thể chất 1', 'Môn thể dục rèn luyện sức khỏe, phát triển thể lực và kỹ năng vận động cơ bản cho sinh viên.'),
	(11, 'Anh văn 1', 'Môn tiếng Anh cơ bản phát triển 4 kỹ năng nghe, nói, đọc, viết và từ vựng chuyên ngành công nghệ thông tin.'),
	(12, 'Nhập môn tin học', 'Môn học giới thiệu kiến thức cơ bản về máy tính, hệ điều hành, mạng máy tính và ứng dụng tin học văn phòng.'),
	(13, 'Kinh tế chính trị Mác-Lênin', 'Môn học về các quy luật kinh tế cơ bản, phương thức sản xuất và các học thuyết kinh tế chính trị Mác-Lênin.'),
	(14, 'Quân sự 1 (lý thuyết)', 'Môn giáo dục quốc phòng lý thuyết về chiến lược quốc phòng, an ninh quốc gia và nghĩa vụ công dân.'),
	(15, 'Quân sự 2 (thực hành)', 'Môn thực hành kỹ năng quân sự cơ bản, rèn luyện kỷ luật và tinh thần trách nhiệm với đất nước.'),
	(16, 'Toán cao cấp A3', 'Môn toán về đại số tuyến tính, không gian vector, ma trận và các phép biến đổi tuyến tính.'),
	(17, 'Giáo dục thể chất 2', 'Môn thể dục nâng cao với các môn thể thao chuyên môn và rèn luyện thể lực chuyên sâu.'),
	(18, 'Anh văn 2', 'Môn tiếng Anh trung cấp tập trung vào kỹ năng giao tiếp chuyên ngành và đọc hiểu tài liệu kỹ thuật.'),
	(19, 'Chủ nghĩa xã hội khoa học', 'Môn học về lý luận xây dựng chủ nghĩa xã hội, con đường đi lên chủ nghĩa xã hội ở Việt Nam.'),
	(20, 'Xác suất thống kê', 'Môn học về lý thuyết xác suất, biến ngẫu nhiên, phân phối xác suất và thống kê ứng dụng trong công nghệ.'),
	(21, 'Pháp luật đại cương', 'Môn giáo dục pháp luật cơ bản về hệ thống pháp luật Việt Nam, quyền và nghĩa vụ công dân.'),
	(22, 'Toán rời rạc', 'Môn toán nền tảng cho khoa học máy tính bao gồm logic, tập hợp, đồ thị, tổ hợp và lý thuyết số.'),
	(23, 'Tư tưởng Hồ Chí Minh', 'Môn học về hệ thống tư tưởng, đạo đức và phong cách của Chủ tịch Hồ Chí Minh.'),
	(24, 'Lịch sử Đảng Cộng sản Việt Nam', 'Môn học về quá trình hình thành và phát triển của Đảng Cộng sản Việt Nam qua các thời kỳ cách mạng.'),
	(25, 'Lập trình cơ bản', 'Môn học nền tảng về lập trình, cú pháp, cấu trúc dữ liệu cơ bản và giải thuật sử dụng ngôn ngữ lập trình hiện đại.'),
	(26, 'Cấu trúc máy tính', 'Môn học về kiến trúc tổ chức máy tính, bộ vi xử lý, bộ nhớ và các thành phần phần cứng cơ bản.'),
	(27, 'Nhập môn hệ điều hành', 'Môn học giới thiệu về chức năng, cấu trúc và quản lý tài nguyên của hệ điều hành máy tính.'),
	(28, 'Lập trình nâng cao (A)', 'Môn học nâng cao về lập trình hướng đối tượng, cấu trúc dữ liệu phức tạp và thiết kế phần mềm.'),
	(29, 'Giao tiếp người-máy', 'Môn học về thiết kế giao diện người dùng, trải nghiệm người dùng và tương tác người-máy tính.'),
	(30, 'Cấu trúc dữ liệu', 'Môn học về các cấu trúc dữ liệu quan trọng như mảng, danh sách, cây, đồ thị và thuật toán xử lý.'),
	(31, 'Mạng máy tính cơ bản', 'Môn học về kiến trúc mạng, giao thức truyền thông, mô hình TCP/IP và các công nghệ mạng cơ bản.'),
	(32, 'Hệ điều hành nâng cao', 'Môn học chuyên sâu về quản lý tiến trình, bộ nhớ, file system và lập trình hệ thống.'),
	(33, 'Thiết kế hướng đối tượng', 'Môn học về phân tích, thiết kế phần mềm theo mô hình hướng đối tượng và các design pattern.'),
	(34, 'Lý thuyết đồ thị', 'Môn học về lý thuyết đồ thị, các thuật toán trên đồ thị và ứng dụng trong khoa học máy tính.'),
	(35, 'Nhập môn cơ sở dữ liệu', 'Môn học cơ bản về hệ quản trị cơ sở dữ liệu, mô hình quan hệ, SQL và thiết kế database.'),
	(36, 'Lập trình mạng', 'Môn học về lập trình ứng dụng mạng, socket programming và các giao thức lập trình mạng.'),
	(37, 'Lập trình Web (A)', 'Môn học về phát triển ứng dụng web cơ bản, HTML, CSS, JavaScript và framework web phổ biến.'),
	(38, 'Nhập môn trí tuệ nhân tạo', 'Môn học giới thiệu về AI, machine learning, thuật toán tìm kiếm và các ứng dụng trí tuệ nhân tạo.'),
	(39, 'Lập trình trên thiết bị di động', 'Môn học về phát triển ứng dụng di động trên các nền tảng Android, iOS và cross-platform.'),
	(40, 'Nhập môn công nghệ phần mềm', 'Môn học về quy trình phát triển phần mềm, quản lý dự án, testing và bảo trì phần mềm.'),
	(41, 'Kỹ năng giao tiếp', 'Môn học phát triển kỹ năng giao tiếp trong công việc, thuyết trình và làm việc nhóm hiệu quả.'),
	(42, 'Marketing căn bản', 'Môn học về các nguyên lý marketing cơ bản, chiến lược marketing và ứng dụng trong kinh doanh công nghệ.'),
	(43, 'Đồ họa máy tính', 'Môn học về các thuật toán đồ họa 2D/3D, xử lý hình ảnh và lập trình đồ họa máy tính.'),
	(44, 'Hệ quản trị cơ sở dữ liệu', 'Môn học chuyên sâu về thiết kế, quản trị và tối ưu hóa hệ thống cơ sở dữ liệu quy mô lớn.'),
	(45, 'Lập trình .NET', 'Môn học về phát triển ứng dụng trên nền tảng .NET Framework/Core sử dụng ngôn ngữ C#.'),
	(46, 'Lập trình PHP', 'Môn học về lập trình web động với PHP, MySQL và các framework PHP phổ biến.'),
	(47, 'Lập trình Python', 'Môn học về ngôn ngữ Python, ứng dụng trong data science, web development và automation.'),
	(48, 'Mạng máy tính nâng cao', 'Môn học chuyên sâu về định tuyến, chuyển mạch, bảo mật mạng và quản trị hệ thống mạng doanh nghiệp.'),
	(49, 'An toàn và bảo mật hệ thống thông tin', 'Môn học về các kỹ thuật bảo mật, mã hóa, phòng chống tấn công và quản lý an ninh thông tin.'),
	(50, 'Hệ thống thông tin địa lý ứng dụng', 'Môn học về GIS, xử lý dữ liệu không gian và ứng dụng bản đồ số trong các lĩnh vực khác nhau.'),
	(51, 'Máy học', 'Môn học về các thuật toán machine learning, deep learning và ứng dụng AI trong thực tế.'),
	(52, 'Thực tập lập trình Web', 'Môn thực hành phát triển dự án web hoàn chỉnh từ phân tích, thiết kế đến triển khai.'),
	(53, 'Lập trình mạng nâng cao', 'Môn học nâng cao về lập trình ứng dụng mạng phân tán, web service và cloud computing.'),
	(54, 'Xử lý ảnh và thị giác máy tính', 'Môn học về các kỹ thuật xử lý ảnh số, nhận dạng pattern và computer vision.'),
	(55, 'Đảm bảo chất lượng và kiểm thử phần mềm', 'Môn học về quy trình QA, các phương pháp testing và công cụ tự động hóa kiểm thử.'),
	(56, 'Lập trình Front End', 'Môn học về phát triển giao diện web hiện đại với React, Vue, Angular và responsive design.'),
	(57, 'Data Mining', 'Môn học về khai phá dữ liệu, phân tích dữ liệu lớn và các thuật toán data mining.'),
	(58, 'Phân tích dữ liệu lớn', 'Môn học về xử lý và phân tích big data sử dụng Hadoop, Spark và các công nghệ phân tán.'),
	(59, 'Quản trị mạng', 'Môn học về quản trị hệ thống mạng, giám sát, bảo trì và khắc phục sự cố mạng doanh nghiệp.'),
	(60, 'An ninh mạng', 'Môn học về các kỹ thuật phòng chống xâm nhập, tường lửa, phát hiện và xử lý tấn công mạng.'),
	(61, 'Thực tập lập trình trên thiết bị di động', 'Môn thực hành phát triển ứng dụng di động hoàn chỉnh trên Android hoặc iOS.'),
	(62, 'Quản lý dự án phần mềm', 'Môn học về quy trình quản lý dự án IT, Agile, Scrum và các công cụ quản lý dự án.'),
	(63, 'Data Warehouse', 'Môn học về thiết kế và xây dựng kho dữ liệu, ETL process và business intelligence.'),
	(64, 'IoT', 'Môn học về Internet of Things, lập trình thiết bị nhúng, cảm biến và hệ thống IoT.'),
	(65, 'Hệ thống thông tin quản lý', 'Môn học về phân tích, thiết kế và triển khai hệ thống thông tin phục vụ quản lý doanh nghiệp.'),
	(66, 'Thương mại điện tử', 'Môn học về mô hình kinh doanh online, thanh toán điện tử và xây dựng website thương mại điện tử.'),
	(67, 'Chuyên đề Java', 'Môn học chuyên sâu về lập trình Java, Spring Framework và phát triển ứng dụng doanh nghiệp.'),
	(68, 'Chuyên đề WEB', 'Môn học chuyên đề về các công nghệ web tiên tiến, API, microservices và web security.'),
	(69, 'Đồ án chuyên ngành', 'Môn thực hiện đồ án tốt nghiệp, áp dụng kiến thức tổng hợp để giải quyết vấn đề thực tế.'),
	(70, 'Khóa luận tốt nghiệp', 'Môn nghiên cứu chuyên sâu, viết luận văn về một chủ đề chuyên ngành dưới sự hướng dẫn giảng viên.'),
	(71, 'Tiểu luận tốt nghiệp', 'Môn viết tiểu luận tổng hợp kiến thức về một vấn đề cụ thể trong lĩnh vực công nghệ thông tin.'),
	(72, 'Khởi nghiệp', 'Môn học về kỹ năng khởi nghiệp, lập kế hoạch kinh doanh và xây dựng startup công nghệ.');

-- Dumping structure for table user_service_v3.users
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `role` tinytext DEFAULT 'student',
  `status` tinytext DEFAULT 'active',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_email` (`email`),
  KEY `idx_role` (`role`(255)),
  KEY `idx_status` (`status`(255))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table user_service_v3.users: ~4 rows (approximately)
INSERT INTO `users` (`user_id`, `email`, `password`, `full_name`, `avatar_url`, `role`, `status`, `created_at`, `updated_at`) VALUES
	(5, 'nguyenvana@gmail.com', '$2a$10$VqB4WVEOT1s4px8eLlkfKutZPNYzpqH7ErXbafKJiRTxmi4Q0A4cW', 'Tài đẹp trai', 'https://tse4.mm.bing.net/th/id/OIP.r6zjaQBeP9jnIo4za2l2TQHaEK?rs=1&pid=ImgDetMain&o=7&rm=3', NULL, NULL, '2026-01-26 02:38:18', '2026-01-26 02:38:18'),
	(6, 'hihihi@gmail.com', '$2a$10$7lk8h8Oycqot4OIpODJ4E.r3g0sKemaOw5zETv68twko8sg/voxWu', 'Tài đẹp trai', 'https://tse4.mm.bing.net/th/id/OIP.r6zjaQBeP9jnIo4za2l2TQHaEK?rs=1&pid=ImgDetMain&o=7&rm=3', NULL, NULL, '2026-01-26 02:44:47', '2026-01-26 02:44:47'),
	(7, 'hihihi2@gmail.com', '$2a$10$KJnQFbqCCArO.rOO0pFCRuT/UCyGFPHXcFNTWGEguhG/wZwXORo56', 'Tài đẹp trai', 'https://tse4.mm.bing.net/th/id/OIP.r6zjaQBeP9jnIo4za2l2TQHaEK?rs=1&pid=ImgDetMain&o=7&rm=3', NULL, NULL, '2026-01-26 02:44:54', '2026-01-26 02:44:54'),
	(8, 'taia@gmail.com', '$2a$10$juG0rTrFeeYj3y0VoRgR..Zahp1l1BtVypt2Ca60GxcWZ4ndxMgPK', 'Tài đẹp trai', 'https://tse4.mm.bing.net/th/id/OIP.r6zjaQBeP9jnIo4za2l2TQHaEK?rs=1&pid=ImgDetMain', NULL, 'ACTIVE', '2026-01-26 02:50:18', '2026-01-26 02:50:18');

-- Dumping structure for table user_service_v3.user_subjects
CREATE TABLE IF NOT EXISTS `user_subjects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_subject` (`user_id`,`subject_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_subject` (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table user_service_v3.user_subjects: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
