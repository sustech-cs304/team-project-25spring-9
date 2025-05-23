-- MySQL dump 10.13  Distrib 8.1.0, for Win64 (x86_64)
--
-- Host: 8.134.53.236    Database: oad_project
-- ------------------------------------------------------
-- Server version	8.0.35-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `building`
--

DROP TABLE IF EXISTS `building`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `building`
(
    `building_id`          int    NOT NULL AUTO_INCREMENT,
    `building_name`        text   NOT NULL,
    `building_img`         int    NOT NULL DEFAULT '0',
    `building_description` text,
    `building_x`           double NOT NULL,
    `building_y`           double NOT NULL,
    `building_comments`    int             DEFAULT '0',
    PRIMARY KEY (`building_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 52
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building`
--

LOCK TABLES `building` WRITE;
/*!40000 ALTER TABLE `building`
    DISABLE KEYS */;
INSERT INTO `building`
VALUES (5, '湖畔4栋宿舍', 0, '', 113.998771, 22.60058, 0),
       (6, '湖畔5栋宿舍', 0, '', 113.99927, 22.600924, 0),
       (7, '湖畔6栋宿舍', 0, '', 113.999594, 22.60064, 0),
       (8, '湖畔1栋宿舍', 4,
        '湖畔宿舍是位于校园内的一组学生宿舍楼。这些宿舍位于校园的湖畔地区，提供给学生一个相对宁静和优美的居住环境。\n\n位置： 湖畔宿舍位于南方科技大学校园内，毗邻校园的湖泊或水域，提供了良好的自然景观。\n\n设施： 宿舍楼内一般会提供公共的洗漱间、浴室、厨房等设施，以满足学生的基本生活需求。同时，宿舍区域可能还会设有学生活动室、自习室等公共空间。\n环境： 由于位于湖泊旁边，湖畔宿舍的环境宁静，并且享有一定的自然景观。这有助于提供一个较为舒适的居住环境。\n交通： 宿舍一般会便利地位于校园内，学生可以方便地前往教学区、图书馆、食堂等校园设施。',
        113.998655, 22.599154, 0),
       (9, '湖畔2栋宿舍', 0, '', 113.998699, 22.599685, 0),
       (10, '湖畔3栋宿舍', 0, '', 113.998489, 22.600189, 0),
       (11, '学生宿舍7栋', 0, '', 113.997341, 22.601825, 0),
       (12, '学生宿舍8栋', 0, '', 113.997403, 22.601243, 0),
       (13, '学生宿舍9栋', 0, '', 113.997966, 22.601647, 0),
       (14, '学生宿舍10栋', 1, '', 113.998393, 22.602032, 0),
       (15, '学生宿舍11栋', 2,
        '学生宿舍是为学校学生提供住宿的地方。\n公共设施： 宿舍楼内会提供公共的洗漱间、浴室、厨房等设施，以满足学生的基本生活需求。有些宿舍还设有学生活动室、自习室、洗衣房等公共空间。\n网络和电力设施： 学生宿舍通常会提供稳定的网络连接，以便学生进行学习和生活所需。同时，宿舍楼也会有电力设施，以供学生使用电器设备。\n安全和管理： 学生宿舍通常会有相关的安全措施，例如门禁系统、监控设备等，以保障学生的安全。宿舍管理处会负责宿舍的日常管理和维护。\n交通： 学生宿舍通常位于校园内，方便学生前往教学区、图书馆、食堂等校园设施。有些宿舍可能还设有周边交通便利的公交站点。',
        113.99919, 22.6022, 0),
       (16, '学生宿舍12栋', 0, '', 113.999842, 22.60256, 0),
       (17, '学生宿舍13栋', 0, '', 113.99953, 22.601617, 0),
       (18, '学生宿舍14栋', 0, '', 114.00003, 22.601838, 0),
       (19, '学生宿舍15栋', 0, '', 114.000394, 22.602218, 0),
       (20, '学生宿舍16栋', 0, '', 114.000853, 22.602459, 0),
       (21, '学生宿舍17栋', 1, '', 114.000915, 22.60292, 0),
       (22, '九华精舍！', 1,
        '九华精舍，也被称为院士楼，是为学校的院士提供的住所。 九华精舍位于湖畔，是校园内的显眼位置。\n院士楼提供相对豪华和舒适的住宿条件。院士的住所包括独立的公寓或套房，内部设施较为齐全，包括卧室、起居区、厨房、卫生间等。\n自然环境： 由于院士楼的特殊地位，其周围环境更为宁静、优美。院士们可以享受到相对较好的自然景观。\n安全和便利： 院士楼有高水平的安全设施，如门禁系统、专业的保安服务等，以确保院士们的安全。同时，院士楼的位置相对便利，方便院士们前往学校的教学区、研究机构等地。\n社交和交流： 院士楼可能会提供一定的社交和交流空间，以促进院士们之间的互动和学术合作。',
        113.999827, 22.600086, 0),
       (25, '湖畔食堂', 0, '', 113.997859, 22.596788, 0),
       (29, '中心食堂', 2,
        '中心食堂是学校内主要的用餐场所之一。\n\n位置： 中心食堂一般学校校园的核心区域，在教学区或学生活动区域附近，以方便学生和教职工用餐。\n用餐环境： 中心食堂的用餐环境相对宽敞，配备有餐桌椅等用餐设施。为了迎合不同的用餐需求，有不同类型的餐桌，如单人桌、多人桌等。\n菜品种类： 中心食堂一般会提供丰富多样的菜品，包括中餐、西餐、快餐等，以满足师生的口味需求。菜品根据季节和需求进行调整，提供一定的食品多样性。\n价格和支付： 食堂的价格通常相对较为亲民，适合学生。支付方式包括学生卡、移动支付等多种方式，为师生提供便捷的用餐体验。\n开放时间： 为了满足不同人群的用餐需求，中心食堂会提供较长的开放时间，包括早餐、午餐和晚餐。\n卫生和安全： 食堂的卫生和安全是非常重要的，学校通常会加强食品安全管理，确保提供的食品卫生合格。',
        113.997516, 22.597257, 0),
       (30, '第二学生食堂', 1, '', 113.99854, 22.601822, 0),
       (31, '荔园食堂', 1, '', 113.999583, 22.604848, 0),
       (32, '欣园食堂', 0, '', 114.00201, 22.608, 0),
       (33, '教工餐厅', 0, '', 114.002782, 22.599965, 0),
       (34, '茶餐厅', 0, '', 114.002687, 22.598695, 0),
       (35, '西餐厅', 0, '', 114.002972, 22.598781, 0),
       (36, '商学院', 0, '', 114.00056, 22.595462, 0),
       (37, '理学院', 1, '', 113.999732, 22.594685, 0),
       (38, '工学院南楼', 1,
        '工学院是专注于工程科学与技术的教育和研究。\n\n学科设置： 工学院涵盖多个工程领域，例如电子工程、计算机工程、土木工程、机械工程、化学工程等。学院内设有不同的系或专业，以满足学生对不同工程学科的学习需求。\n教学和研究： 工学院旨在培养学生在工程领域的专业知识和实践能力。除了本科学位课程外，学院还提供硕士和博士学位课程，以满足不同层次学生的需求。同时，工学院开展各种研究项目和实验室工作，推动工程科学的前沿研究。\n师资力量： 优秀的师资力量是工学院的重要组成部分。学院有一支由有丰富教学和研究经验的教授、副教授和助理教授组成的教师团队。\n实践与实习： 工学院通常会注重学生的实践能力培养，有实习机会、工程实践项目或者与企业合作的机会，以让学生将理论知识应用到实际问题中。\n学术活动： 学院会组织各种学术活动，如学术讲座、研讨会、工程竞赛等，以促进学术交流和提高学生的综合素质。',
        113.996012, 22.600425, 0),
       (39, '工学院北楼', 0, '', 113.995972, 22.600898, 0),
       (40, '第三教学楼', 2, '', 113.999814, 22.595967, 0),
       (41, '第一教学楼', 0, '', 113.997361, 22.596008, 0),
       (42, '第二教学楼', 0, '', 113.996922, 22.594703, 0),
       (43, '第一科研楼', 1, '', 113.996629, 22.596343, 0),
       (44, '第二科研楼', 0, '', 113.996064, 22.595845, 0),
       (45, '检测中心', 0, '', 113.997025, 22.5952, 0),
       (46, '琳恩图书馆', 1, '', 113.998394, 22.595165, 0),
       (47, '会议中心', 2, '', 113.996776, 22.59273, 0),
       (48, '创园', 0, '', 114.003302, 22.604456, 0),
       (50, '松禾田径场', 0, '', 114.003053, 22.601811, 0),
       (51, '润杨体育馆', 1, '', 114.004242, 22.601758, 0);
/*!40000 ALTER TABLE `building`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `building_img`
--

DROP TABLE IF EXISTS `building_img`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `building_img`
(
    `buildingImg_id` int  NOT NULL AUTO_INCREMENT,
    `building_id`    int  NOT NULL,
    `img_name`       text NOT NULL,
    PRIMARY KEY (`buildingImg_id`),
    KEY `buildingImg_building_building_id_fk` (`building_id`),
    CONSTRAINT `buildingImg_building_building_id_fk` FOREIGN KEY (`building_id`) REFERENCES `building` (`building_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 86
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `building_img`
--

LOCK TABLES `building_img` WRITE;
/*!40000 ALTER TABLE `building_img`
    DISABLE KEYS */;
INSERT INTO `building_img`
VALUES (62, 8, '8_1.jpeg'),
       (63, 15, '15_1.jpeg'),
       (64, 22, '22_1.jpeg'),
       (65, 29, '29_1.jpeg'),
       (66, 38, '38_1.jpeg'),
       (68, 40, '40_1.jpeg'),
       (69, 37, '37_1.jpeg'),
       (70, 47, '47_1.jpeg'),
       (71, 8, '8_2.jpeg'),
       (72, 31, '31_1.jpeg'),
       (73, 21, '21_1.jpeg'),
       (74, 30, '30_1.jpeg'),
       (75, 14, '14_1.jpeg'),
       (76, 29, '29_2.jpeg'),
       (77, 43, '43_1.jpeg'),
       (79, 40, '40_2.jpeg'),
       (80, 8, '8_3.jpeg'),
       (81, 51, '51_1.jpeg'),
       (82, 47, '47_2.jpeg'),
       (83, 8, '8_4.jpeg'),
       (84, 15, '15_2.jpeg'),
       (85, 46, '46_1.jpeg');
/*!40000 ALTER TABLE `building_img`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_img_to_building`
    AFTER INSERT
    ON `building_img`
    FOR EACH ROW
begin
    update building
    set building_img = building.building_img + 1
    where building_id = new.building_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `remove_img_to_building`
    AFTER DELETE
    ON `building_img`
    FOR EACH ROW
begin
    update building
    set building_img = building.building_img - 1
    where building_id = old.building_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `bus`
--

DROP TABLE IF EXISTS `bus`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bus`
(
    `bus_id`    int NOT NULL AUTO_INCREMENT,
    `bus_name`  text,
    `bus_valid` tinyint(1) DEFAULT '1',
    PRIMARY KEY (`bus_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 81
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bus`
--

LOCK TABLES `bus` WRITE;
/*!40000 ALTER TABLE `bus`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `bus`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment`
(
    `comment_id`      int       NOT NULL AUTO_INCREMENT,
    `user_id`         int       NOT NULL,
    `building_id`     int       NOT NULL,
    `comment_img`     int       NOT NULL DEFAULT '0',
    `comment_content` text      NOT NULL,
    `comment_time`    timestamp NOT NULL DEFAULT (now()),
    `comment_likes`   int       NOT NULL DEFAULT '0',
    `comment_replies` int       NOT NULL DEFAULT '0',
    `comment_valid`   int       NOT NULL DEFAULT '0',
    PRIMARY KEY (`comment_id`),
    UNIQUE KEY `comment_pk` (`comment_id`),
    KEY `comment_building_null_fk` (`building_id`),
    KEY `comment_user_null_fk` (`user_id`),
    CONSTRAINT `comment_building_null_fk` FOREIGN KEY (`building_id`) REFERENCES `building` (`building_id`),
    CONSTRAINT `comment_user_null_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`User_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 87
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment`
    DISABLE KEYS */;
INSERT INTO `comment`
VALUES (40, 0, 5, 0, '初始评论', '2023-12-06 13:12:14', 0, 0, 0),
       (41, 0, 6, 0, '初始评论', '2023-12-06 13:12:24', 0, 0, 0),
       (42, 0, 7, 0, '初始评论', '2023-12-06 13:12:38', 0, 0, 0),
       (43, 0, 8, 0, '初始评论', '2023-12-06 13:14:35', 0, 0, 0),
       (44, 0, 9, 0, '初始评论', '2023-12-06 13:14:41', 0, 0, 0),
       (45, 0, 10, 0, '初始评论', '2023-12-06 13:14:47', 0, 0, 0),
       (46, 0, 11, 0, '初始评论', '2023-12-06 13:41:31', 0, 0, 0),
       (47, 0, 12, 0, '初始评论', '2023-12-06 13:41:38', 0, 0, 0),
       (48, 0, 13, 0, '初始评论', '2023-12-06 13:42:02', 0, 0, 0),
       (49, 0, 14, 0, '初始评论', '2023-12-06 13:42:10', 0, 0, 0),
       (50, 0, 15, 0, '初始评论', '2023-12-06 13:42:47', 0, 0, 0),
       (51, 0, 16, 0, '初始评论', '2023-12-06 13:42:55', 0, 0, 0),
       (52, 0, 17, 0, '初始评论', '2023-12-06 13:43:16', 0, 0, 0),
       (53, 0, 18, 0, '初始评论', '2023-12-06 13:43:37', 0, 0, 0),
       (54, 0, 19, 0, '初始评论', '2023-12-06 13:43:44', 0, 0, 0),
       (55, 0, 20, 0, '初始评论', '2023-12-06 13:43:51', 0, 0, 0),
       (56, 0, 21, 0, '初始评论', '2023-12-06 13:43:58', 0, 0, 0),
       (57, 0, 22, 0, '初始评论', '2023-12-06 13:46:29', 0, 0, 0),
       (60, 0, 25, 0, '初始评论', '2023-12-06 13:51:59', 0, 0, 0),
       (64, 0, 29, 0, '初始评论', '2023-12-06 13:53:46', 0, 0, 0),
       (65, 0, 30, 0, '初始评论', '2023-12-06 13:55:36', 0, 0, 0),
       (66, 0, 31, 0, '初始评论', '2023-12-06 13:56:39', 0, 0, 0),
       (67, 0, 32, 0, '初始评论', '2023-12-06 13:57:06', 0, 0, 0),
       (68, 0, 33, 0, '初始评论', '2023-12-06 13:58:44', 0, 0, 0),
       (69, 0, 34, 0, '初始评论', '2023-12-06 13:59:05', 0, 0, 0),
       (70, 0, 35, 0, '初始评论', '2023-12-06 13:59:21', 0, 0, 0),
       (71, 0, 36, 0, '初始评论', '2023-12-06 14:03:35', 0, 0, 0),
       (72, 0, 37, 0, '初始评论', '2023-12-06 14:03:45', 0, 0, 0),
       (73, 0, 38, 0, '初始评论', '2023-12-06 14:04:18', 0, 0, 0),
       (74, 0, 39, 0, '初始评论', '2023-12-06 14:04:28', 0, 0, 0),
       (75, 0, 40, 0, '初始评论', '2023-12-06 14:04:46', 0, 0, 0),
       (76, 0, 41, 0, '初始评论', '2023-12-06 14:04:56', 0, 0, 0),
       (77, 0, 42, 0, '初始评论', '2023-12-06 14:05:11', 0, 0, 0),
       (78, 0, 43, 0, '初始评论', '2023-12-06 14:05:31', 0, 0, 0),
       (79, 0, 44, 0, '初始评论', '2023-12-06 14:05:39', 0, 0, 0),
       (80, 0, 45, 0, '初始评论', '2023-12-07 00:27:31', 0, 0, 0),
       (81, 0, 46, 0, '初始评论', '2023-12-07 01:11:01', 0, 0, 0),
       (82, 0, 47, 0, '初始评论', '2023-12-07 01:12:09', 0, 0, 0),
       (83, 0, 48, 0, '初始评论', '2023-12-07 01:14:21', 0, 0, 0),
       (85, 0, 50, 0, '初始评论', '2023-12-07 01:31:22', 0, 0, 0),
       (86, 0, 51, 0, '初始评论', '2023-12-07 01:31:31', 0, 0, 0);
/*!40000 ALTER TABLE `comment`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_comments_to_building_admin`
    AFTER INSERT
    ON `comment`
    FOR EACH ROW
begin
    update building
    set building_comments = building.building_comments + 1
    where building_id = new.building_id
      and new.comment_valid = 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_comments_to_building`
    AFTER UPDATE
    ON `comment`
    FOR EACH ROW
begin
    update building
    set building_comments = building.building_comments + 1
    where building_id = new.building_id
      and new.comment_valid = 1
      and old.comment_valid != 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `minus_comments_to_building`
    AFTER UPDATE
    ON `comment`
    FOR EACH ROW
begin
    update building
    set building_comments = building.building_comments - 1
    where building_id = new.building_id
      and new.comment_valid != 1
      and old.comment_valid = 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `remove_comments_to_building`
    AFTER DELETE
    ON `comment`
    FOR EACH ROW
begin
    update building
    set building_comments = building.building_comments - 1
    where building_id = old.building_id
      and old.comment_valid = 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `comment_img`
--

DROP TABLE IF EXISTS `comment_img`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_img`
(
    `commentImg_id` int  NOT NULL AUTO_INCREMENT,
    `comment_id`    int  NOT NULL,
    `img_name`      text NOT NULL,
    PRIMARY KEY (`commentImg_id`),
    KEY `comment_img_comment_comment_id_fk` (`comment_id`),
    CONSTRAINT `comment_img_comment_comment_id_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_img`
--

LOCK TABLES `comment_img` WRITE;
/*!40000 ALTER TABLE `comment_img`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_img`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_img_to_comment`
    AFTER INSERT
    ON `comment_img`
    FOR EACH ROW
begin
    update comment
    set comment_img = comment.comment_img + 1
    where comment_id = new.comment_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `remove_img_to_comment`
    AFTER DELETE
    ON `comment_img`
    FOR EACH ROW
begin
    update comment
    set comment_img = comment.comment_img - 1
    where comment_id = old.comment_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `comment_likes`
--

DROP TABLE IF EXISTS `comment_likes`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_likes`
(
    `id`             int        NOT NULL AUTO_INCREMENT,
    `user_id`        int        NOT NULL,
    `comment_id`     int        NOT NULL,
    `like_time`      timestamp  NOT NULL DEFAULT (now()),
    `read_condition` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `comment_likes_pk` (`comment_id`, `user_id`),
    KEY `comment_likes_user_User_id_fk` (`user_id`),
    CONSTRAINT `comment_likes_comment_comment_id_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`),
    CONSTRAINT `comment_likes_user_User_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`User_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 29
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_likes`
--

LOCK TABLES `comment_likes` WRITE;
/*!40000 ALTER TABLE `comment_likes`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_likes`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_likes_to_comment`
    AFTER INSERT
    ON `comment_likes`
    FOR EACH ROW
begin
    update comment set comment_likes = comment_likes + 1 where comment_id = new.comment_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `remove_likes_to_comment`
    AFTER DELETE
    ON `comment_likes`
    FOR EACH ROW
begin
    update comment set comment_likes = comment_likes - 1 where comment_id = old.comment_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `commodity`
--

DROP TABLE IF EXISTS `commodity`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commodity`
(
    `commodity_name` text,
    `commodity_id`   int NOT NULL AUTO_INCREMENT,
    `window_id`      int          DEFAULT NULL,
    `price`          double       DEFAULT NULL,
    `amount_left`    int NOT NULL DEFAULT '0',
    PRIMARY KEY (`commodity_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commodity`
--

LOCK TABLES `commodity` WRITE;
/*!40000 ALTER TABLE `commodity`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `commodity`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food`
--

DROP TABLE IF EXISTS `food`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food`
(
    `food_id`                   int  NOT NULL AUTO_INCREMENT,
    `food_name`                 text NOT NULL,
    `food_img`                  text,
    `food_amount`               int  NOT NULL COMMENT 'the amount of food that can be ordered daily',
    `food_ordered_amount`       int  NOT NULL DEFAULT '0' COMMENT 'the amount of food that have be ordered today',
    `window_id`                 int  NOT NULL,
    `food_total_ordered_amount` int  NOT NULL DEFAULT '0' COMMENT 'the amount of food that have be ordered up to now',
    `food_price`                double        DEFAULT NULL,
    `food_valid`                tinyint(1)    DEFAULT '1',
    PRIMARY KEY (`food_id`),
    KEY `food_restaurant_null_fk` (`window_id`),
    CONSTRAINT `food_restaurant_null_fk` FOREIGN KEY (`window_id`) REFERENCES `restaurant` (`restaurant_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 15
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food`
--

LOCK TABLES `food` WRITE;
/*!40000 ALTER TABLE `food`
    DISABLE KEYS */;
INSERT INTO `food`
VALUES (1, '米饭', '1.jpeg', 10, 1, 1, 123, NULL, 0),
       (2, '鱼香肉丝', '2.jpeg', 12, 3, 1, 223, NULL, 0),
       (3, '12', NULL, 12, 0, 1, 0, NULL, 0),
       (4, '甜甜花酿鸡', NULL, 100, 0, 1, 0, NULL, 1),
       (6, '仙跳墙', NULL, 100, 0, 1, 0, NULL, 1),
       (7, '烤肉排', NULL, 100, 0, 1, 0, NULL, 1),
       (8, '满足沙拉', NULL, 100, 0, 1, 0, NULL, 1),
       (9, '北地烟熏鸡', NULL, 100, 0, 1, 0, NULL, 1),
       (10, '蜜酱胡萝卜煎肉', NULL, 100, 0, 1, 0, NULL, 1),
       (11, '蒙德烤鱼', NULL, 100, 0, 1, 0, NULL, 1),
       (12, '嘟嘟莲海鲜羹', NULL, 100, 0, 1, 0, NULL, 1),
       (13, '提瓦特煎蛋', NULL, 100, 0, 2, 0, NULL, 1),
       (14, 'test', NULL, 1212, 0, 2, 0, NULL, 1);
/*!40000 ALTER TABLE `food`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_order`
--

DROP TABLE IF EXISTS `food_order`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_order`
(
    `food_order_id` int NOT NULL AUTO_INCREMENT,
    `food_id`       int NOT NULL,
    `order_id`      int NOT NULL,
    `food_amount`   int NOT NULL DEFAULT '1',
    `order_valid`   tinyint(1)   DEFAULT '1',
    PRIMARY KEY (`food_order_id`),
    KEY `food_order_food_null_fk` (`food_id`),
    CONSTRAINT `food_order_food_null_fk` FOREIGN KEY (`food_id`) REFERENCES `food` (`food_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 14
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_order`
--

LOCK TABLES `food_order` WRITE;
/*!40000 ALTER TABLE `food_order`
    DISABLE KEYS */;
INSERT INTO `food_order`
VALUES (1, 1, 1, 1, 1),
       (2, 2, 1, 1, 1),
       (3, 1, 3, 1, 1),
       (4, 2, 3, 1, 1),
       (5, 1, 6, 1, 1),
       (6, 2, 6, 1, 1),
       (7, 1, 8, 1, 1),
       (8, 2, 8, 1, 1),
       (10, 1, 11, 1, 1),
       (11, 2, 11, 1, 1),
       (12, 1, 12, 1, 1),
       (13, 2, 12, 1, 1);
/*!40000 ALTER TABLE `food_order`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `order_new_food`
    BEFORE INSERT
    ON `food_order`
    FOR EACH ROW
BEGIN
    update food
    set food.food_ordered_amount=food_ordered_amount + NEW.food_amount
    where food.food_id = NEW.food_id;
    IF (SELECT COUNT(*) FROM food_rank_date WHERE date = CURDATE() and food_rank_date.food_id = NEW.food_id) = 0 THEN
        insert into food_rank_date (food_id, food_cnt, date) VALUES (NEW.food_id, 0, CURDATE());
    END IF;
    UPDATE food_rank_date
    SET food_cnt = food_cnt + NEW.food_amount
    WHERE date = CURDATE()
      and food_rank_date.food_id = NEW.food_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `order_delete_food`
    BEFORE DELETE
    ON `food_order`
    FOR EACH ROW
BEGIN
    update food
    set food.food_ordered_amount=food_ordered_amount + OLD.food_amount
    where food.food_id = OLD.food_id;
    IF (SELECT order_taken from orders where orders.order_id = OLD.order_id) = FALSE THEN
        IF (SELECT COUNT(*) FROM food_rank_date WHERE date = CURDATE() and food_rank_date.food_id = OLD.food_id) =
           0 THEN
            insert into food_rank_date (food_id, food_cnt, date) VALUES (OLD.food_id, 0, CURDATE());
        END IF;
        UPDATE food_rank_date
        SET food_cnt = food_cnt - OLD.food_amount
        WHERE date = CURDATE()
          and food_rank_date.food_id = OLD.food_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `food_rank_date`
--

DROP TABLE IF EXISTS `food_rank_date`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_rank_date`
(
    `food_rank_id` int NOT NULL AUTO_INCREMENT,
    `food_id`      int          DEFAULT NULL,
    `food_cnt`     int NOT NULL DEFAULT '0',
    `date`         date         DEFAULT NULL,
    PRIMARY KEY (`food_rank_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_rank_date`
--

LOCK TABLES `food_rank_date` WRITE;
/*!40000 ALTER TABLE `food_rank_date`
    DISABLE KEYS */;
INSERT INTO `food_rank_date`
VALUES (2, 1, 2, '2023-12-07'),
       (3, 2, 2, '2023-12-07');
/*!40000 ALTER TABLE `food_rank_date`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_window`
--

DROP TABLE IF EXISTS `food_window`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_window`
(
    `window_id`     int        NOT NULL AUTO_INCREMENT,
    `restaurant_id` int                 DEFAULT NULL,
    `window_name`   text,
    `window_valid`  tinyint(1) NOT NULL DEFAULT '1',
    PRIMARY KEY (`window_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_window`
--

LOCK TABLES `food_window` WRITE;
/*!40000 ALTER TABLE `food_window`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `food_window`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders`
(
    `order_id`        int        NOT NULL AUTO_INCREMENT,
    `user_id`         int                 DEFAULT NULL,
    `food_order_time` datetime   NOT NULL,
    `pay_time`        datetime            DEFAULT NULL,
    `payed`           tinyint(1) NOT NULL DEFAULT '0',
    `orders_valid`    tinyint(1)          DEFAULT '1',
    `food_get_time`   datetime            DEFAULT NULL,
    `orders_price`    double              DEFAULT '0',
    `order_taken`     tinyint(1)          DEFAULT '0',
    `order_reference` int                 DEFAULT NULL,
    PRIMARY KEY (`order_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders`
    DISABLE KEYS */;
INSERT INTO `orders`
VALUES (1, 3, '2023-11-04 00:45:52', '2023-11-05 11:58:58', 1, 1, NULL, NULL, 0, NULL),
       (2, 3, '2023-11-05 11:22:25', '2023-11-05 11:58:58', 1, 1, NULL, NULL, 0, NULL),
       (3, 3, '2023-11-05 11:30:13', '2023-11-05 11:58:58', 1, 1, NULL, NULL, 0, NULL),
       (4, 3, '2023-11-05 12:11:46', NULL, 0, 1, NULL, NULL, 0, NULL),
       (5, 3, '2023-11-05 12:13:15', NULL, 0, 1, NULL, NULL, 0, NULL),
       (6, 3, '2023-11-05 13:03:17', NULL, 0, 1, NULL, NULL, 0, NULL),
       (7, 3, '2023-11-05 13:15:01', NULL, 0, 1, NULL, NULL, 0, NULL),
       (8, 3, '2023-11-05 13:16:46', '2023-11-05 13:26:11', 1, 1, NULL, NULL, 0, NULL),
       (9, 3, '2023-11-26 22:49:01', NULL, 0, 1, NULL, NULL, 0, NULL),
       (10, 3, '2023-12-07 14:36:56', NULL, 0, 1, '2023-12-07 06:36:53', 0, 0, NULL),
       (11, 3, '2023-12-07 14:37:41', NULL, 0, 1, '2023-12-02 15:16:41', 0, 0, NULL),
       (12, 3, '2023-12-07 14:38:12', NULL, 0, 1, '2023-12-02 15:16:41', 0, 0, NULL);
/*!40000 ALTER TABLE `orders`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `pay`
    BEFORE UPDATE
    ON `orders`
    FOR EACH ROW
BEGIN
    IF NEW.pay_time is not null and OLD.pay_time is null THEN
        SET NEW.payed = true;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission`
(
    `user_id`       int NOT NULL,
    `role_id`       int NOT NULL,
    `permission_id` int NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`permission_id`),
    UNIQUE KEY `permission_pk` (`permission_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission`
    DISABLE KEYS */;
INSERT INTO `permission`
VALUES (3, 1, 1),
       (3, 2, 2),
       (3, 3, 3),
       (5, 1, 4),
       (5, 2, 5),
       (5, 3, 6),
       (20, 2, 7),
       (20, 1, 8),
       (30, 2, 9),
       (29, 2, 10);
/*!40000 ALTER TABLE `permission`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reply`
--

DROP TABLE IF EXISTS `reply`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reply`
(
    `reply_id`      int        NOT NULL AUTO_INCREMENT,
    `user_id`       int        NOT NULL COMMENT '进行回复的用户',
    `target_id`     int        NOT NULL COMMENT '被回复的用户',
    `replied_id`    int        NOT NULL DEFAULT '-1',
    `comment_id`    int        NOT NULL COMMENT '被回复的评论',
    `reply_content` text       NOT NULL,
    `reply_time`    timestamp  NOT NULL DEFAULT (now()),
    `reply_likes`   int        NOT NULL DEFAULT '0',
    `reply_valid`   int        NOT NULL DEFAULT '0',
    `reply_read`    tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`reply_id`),
    KEY `reply_user_User_id_fk` (`user_id`),
    KEY `reply_user_User_id_fk2` (`target_id`),
    KEY `reply_comment_comment_id_fk` (`comment_id`),
    CONSTRAINT `reply_comment_comment_id_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`),
    CONSTRAINT `reply_user_User_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`User_id`),
    CONSTRAINT `reply_user_User_id_fk2` FOREIGN KEY (`target_id`) REFERENCES `user` (`User_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 87
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reply`
--

LOCK TABLES `reply` WRITE;
/*!40000 ALTER TABLE `reply`
    DISABLE KEYS */;
INSERT INTO `reply`
VALUES (40, 0, 0, -1, 40, '初始回复', '2023-12-06 13:12:14', 0, 0, 0),
       (41, 0, 0, -1, 41, '初始回复', '2023-12-06 13:12:24', 0, 0, 0),
       (42, 0, 0, -1, 42, '初始回复', '2023-12-06 13:12:38', 0, 0, 0),
       (43, 0, 0, -1, 43, '初始回复', '2023-12-06 13:14:35', 0, 0, 0),
       (44, 0, 0, -1, 44, '初始回复', '2023-12-06 13:14:41', 0, 0, 0),
       (45, 0, 0, -1, 45, '初始回复', '2023-12-06 13:14:47', 0, 0, 0),
       (46, 0, 0, -1, 46, '初始回复', '2023-12-06 13:41:31', 0, 0, 0),
       (47, 0, 0, -1, 47, '初始回复', '2023-12-06 13:41:38', 0, 0, 0),
       (48, 0, 0, -1, 48, '初始回复', '2023-12-06 13:42:02', 0, 0, 0),
       (49, 0, 0, -1, 49, '初始回复', '2023-12-06 13:42:10', 0, 0, 0),
       (50, 0, 0, -1, 50, '初始回复', '2023-12-06 13:42:47', 0, 0, 0),
       (51, 0, 0, -1, 51, '初始回复', '2023-12-06 13:42:55', 0, 0, 0),
       (52, 0, 0, -1, 52, '初始回复', '2023-12-06 13:43:16', 0, 0, 0),
       (53, 0, 0, -1, 53, '初始回复', '2023-12-06 13:43:37', 0, 0, 0),
       (54, 0, 0, -1, 54, '初始回复', '2023-12-06 13:43:44', 0, 0, 0),
       (55, 0, 0, -1, 55, '初始回复', '2023-12-06 13:43:51', 0, 0, 0),
       (56, 0, 0, -1, 56, '初始回复', '2023-12-06 13:43:58', 0, 0, 0),
       (57, 0, 0, -1, 57, '初始回复', '2023-12-06 13:46:29', 0, 0, 0),
       (60, 0, 0, -1, 60, '初始回复', '2023-12-06 13:51:59', 0, 0, 0),
       (64, 0, 0, -1, 64, '初始回复', '2023-12-06 13:53:46', 0, 0, 0),
       (65, 0, 0, -1, 65, '初始回复', '2023-12-06 13:55:36', 0, 0, 0),
       (66, 0, 0, -1, 66, '初始回复', '2023-12-06 13:56:39', 0, 0, 0),
       (67, 0, 0, -1, 67, '初始回复', '2023-12-06 13:57:06', 0, 0, 0),
       (68, 0, 0, -1, 68, '初始回复', '2023-12-06 13:58:44', 0, 0, 0),
       (69, 0, 0, -1, 69, '初始回复', '2023-12-06 13:59:05', 0, 0, 0),
       (70, 0, 0, -1, 70, '初始回复', '2023-12-06 13:59:21', 0, 0, 0),
       (71, 0, 0, -1, 71, '初始回复', '2023-12-06 14:03:35', 0, 0, 0),
       (72, 0, 0, -1, 72, '初始回复', '2023-12-06 14:03:45', 0, 0, 0),
       (73, 0, 0, -1, 73, '初始回复', '2023-12-06 14:04:18', 0, 0, 0),
       (74, 0, 0, -1, 74, '初始回复', '2023-12-06 14:04:29', 0, 0, 0),
       (75, 0, 0, -1, 75, '初始回复', '2023-12-06 14:04:46', 0, 0, 0),
       (76, 0, 0, -1, 76, '初始回复', '2023-12-06 14:04:56', 0, 0, 0),
       (77, 0, 0, -1, 77, '初始回复', '2023-12-06 14:05:11', 0, 0, 0),
       (78, 0, 0, -1, 78, '初始回复', '2023-12-06 14:05:31', 0, 0, 0),
       (79, 0, 0, -1, 79, '初始回复', '2023-12-06 14:05:39', 0, 0, 0),
       (80, 0, 0, -1, 80, '初始回复', '2023-12-07 00:27:31', 0, 0, 0),
       (81, 0, 0, -1, 81, '初始回复', '2023-12-07 01:11:01', 0, 0, 0),
       (82, 0, 0, -1, 82, '初始回复', '2023-12-07 01:12:09', 0, 0, 0),
       (83, 0, 0, -1, 83, '初始回复', '2023-12-07 01:14:21', 0, 0, 0),
       (85, 0, 0, -1, 85, '初始回复', '2023-12-07 01:31:22', 0, 0, 0),
       (86, 0, 0, -1, 86, '初始回复', '2023-12-07 01:31:31', 0, 0, 0);
/*!40000 ALTER TABLE `reply`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_replies_to_comment_admin`
    AFTER INSERT
    ON `reply`
    FOR EACH ROW
begin
    update comment
    set comment_replies = comment.comment_replies + 1
    where comment_id = new.comment_id
      and new.reply_valid = 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_replies_to_comment`
    AFTER UPDATE
    ON `reply`
    FOR EACH ROW
begin
    update comment
    set comment_replies = comment.comment_replies + 1
    where comment_id = new.comment_id
      and new.reply_valid = 1
      and old.reply_valid != 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `minus_replies_to_comment`
    AFTER UPDATE
    ON `reply`
    FOR EACH ROW
begin
    update comment
    set comment_replies = comment.comment_replies - 1
    where comment_id = new.comment_id
      and new.reply_valid != 1
      and old.reply_valid = 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `remove_replies_to_comment`
    AFTER DELETE
    ON `reply`
    FOR EACH ROW
begin
    update comment
    set comment_replies = comment.comment_replies - 1
    where comment_id = old.comment_id
      and old.reply_valid = 1;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `reply_likes`
--

DROP TABLE IF EXISTS `reply_likes`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reply_likes`
(
    `id`             int        NOT NULL AUTO_INCREMENT,
    `user_id`        int        NOT NULL,
    `reply_id`       int        NOT NULL,
    `like_time`      timestamp  NOT NULL DEFAULT (now()),
    `read_condition` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`id`),
    UNIQUE KEY `reply_likes_pk` (`user_id`, `reply_id`),
    KEY `reply_like_reply_reply_id_fk` (`reply_id`),
    CONSTRAINT `reply_like_reply_reply_id_fk` FOREIGN KEY (`reply_id`) REFERENCES `reply` (`reply_id`),
    CONSTRAINT `reply_like_user_User_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`User_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 42
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reply_likes`
--

LOCK TABLES `reply_likes` WRITE;
/*!40000 ALTER TABLE `reply_likes`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `reply_likes`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `add_likes_to_reply`
    AFTER INSERT
    ON `reply_likes`
    FOR EACH ROW
begin
    update reply set reply_likes = reply_likes + 1 where reply_id = new.reply_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `remove_likes_to_reply`
    AFTER DELETE
    ON `reply_likes`
    FOR EACH ROW
begin
    update reply set reply_likes = reply_likes - 1 where reply_id = old.reply_id;
end */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;

--
-- Table structure for table `restaurant`
--

DROP TABLE IF EXISTS `restaurant`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant`
(
    `restaurant_id`    int  NOT NULL AUTO_INCREMENT,
    `restaurant_name`  text NOT NULL,
    `restaurant_valid` tinyint(1) DEFAULT '1',
    PRIMARY KEY (`restaurant_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restaurant`
--

LOCK TABLES `restaurant` WRITE;
/*!40000 ALTER TABLE `restaurant`
    DISABLE KEYS */;
INSERT INTO `restaurant`
VALUES (1, '中心食堂', 1),
       (2, '湖畔食堂一楼', 0),
       (3, '湖畔食堂二楼', 0),
       (4, '第二学生食堂（A区一楼）', 0),
       (5, '湖畔食堂', 1),
       (6, '第二学生食堂', 1),
       (7, '荔园食堂', 1),
       (8, '教工餐厅', 1),
       (9, '茶餐厅', 1),
       (10, '西餐厅', 1);
/*!40000 ALTER TABLE `restaurant`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role`
(
    `role_id`   int  NOT NULL AUTO_INCREMENT,
    `role_name` text NOT NULL,
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `role_pk` (`role_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role`
    DISABLE KEYS */;
INSERT INTO `role`
VALUES (1, 'student'),
       (2, 'admin'),
       (3, 'manager');
/*!40000 ALTER TABLE `role`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_order`
--

DROP TABLE IF EXISTS `store_order`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_order`
(
    `order_id`   int NOT NULL AUTO_INCREMENT,
    `user_id`    int        DEFAULT NULL,
    `order_time` datetime   DEFAULT CURRENT_TIMESTAMP,
    `pay_time`   datetime   DEFAULT NULL,
    `payed`      tinyint(1) DEFAULT '0',
    `price`      double     DEFAULT NULL,
    PRIMARY KEY (`order_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_order`
--

LOCK TABLES `store_order` WRITE;
/*!40000 ALTER TABLE `store_order`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `store_order`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_order_item`
--

DROP TABLE IF EXISTS `store_order_item`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_order_item`
(
    `store_order_item_id` int NOT NULL AUTO_INCREMENT,
    `order_id`            int DEFAULT NULL,
    `item_id`             int DEFAULT NULL,
    `item_count`          int DEFAULT NULL,
    PRIMARY KEY (`store_order_item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_order_item`
--

LOCK TABLES `store_order_item` WRITE;
/*!40000 ALTER TABLE `store_order_item`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `store_order_item`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_window`
--

DROP TABLE IF EXISTS `store_window`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_window`
(
    `window_id`   int NOT NULL AUTO_INCREMENT,
    `window_name` text,
    PRIMARY KEY (`window_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_window`
--

LOCK TABLES `store_window` WRITE;
/*!40000 ALTER TABLE `store_window`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `store_window`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user`
(
    `User_id`       int         NOT NULL AUTO_INCREMENT,
    `user_name`     varchar(30) NOT NULL,
    `user_password` text        NOT NULL,
    `user_img`      text,
    `user_mail`     text,
    `user_nickname` text,
    `user_valid`    tinyint(1) DEFAULT '1',
    PRIMARY KEY (`User_id`),
    UNIQUE KEY `user_name_pk` (`user_name`),
    UNIQUE KEY `user_pk` (`User_id`),
    KEY `user_User_id_index` (`User_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 41
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user`
    DISABLE KEYS */;
INSERT INTO `user`
VALUES (0, '0', '0', '0', '0', '0', 0),
       (3, 'admin', '654321', '1', '12111114@mail.sustech.edu.cn', 'admin', 1),
       (4, '654321', '654321', '4.jpeg', '654321@mail.sustech.edu.cn', '654321', 1),
       (5, '12341234', '1234', '5.jpeg', '12341234@mail.sustech.edu.cn', '12341234', 1),
       (19, '111111', '111111', '19.jpeg', '111111@mail.sustech.edu.cn', '111111', 1),
       (20, '2222', '12345678', '20.jpeg', '2222@mail.sustech.edu.cn', '2222', 1),
       (23, '22223', '123456', '23.jpeg', '22223@mail.sustech.edu.cn', '22223', 1),
       (29, '12110601', '12110601', '29.jpeg', '12110601@mail.sustech.edu.cn', '最可爱的小猫咪', 1),
       (30, '12110644', '12110644', '30.jpeg', '12110644@mail.sustech.edu.cn', 'Claudia', 1),
       (35, '12111114', '12345678', '0.jpeg', '12111114@mail.sustech.edu.cn', '12111114', 1),
       (39, '12112411', '12112411', '0.jpeg', '12112411@mail.sustech.edu.cn', '12112411', 1);
/*!40000 ALTER TABLE `user`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client = @@character_set_client */;
/*!50003 SET @saved_cs_results = @@character_set_results */;
/*!50003 SET @saved_col_connection = @@collation_connection */;
/*!50003 SET character_set_client = utf8mb4 */;
/*!50003 SET character_set_results = utf8mb4 */;
/*!50003 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50003 SET @saved_sql_mode = @@sql_mode */;
/*!50003 SET sql_mode =
        'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */;
DELIMITER ;;
/*!50003 CREATE */ /*!50017 DEFINER =`root`@`%`*/ /*!50003 TRIGGER `update_user_after_insert`
    BEFORE INSERT
    ON `user`
    FOR EACH ROW
BEGIN
    IF NEW.user_img IS NULL THEN
        SET NEW.user_img = CONCAT(NEW.user_id, '.jpeg');
    END IF;

    -- 更新user_mail
    IF NEW.user_mail IS NULL THEN
        SET NEW.user_mail = CONCAT(NEW.user_name, '@mail.sustech.edu.cn');
    END IF;

    -- 更新user_nickname
    IF NEW.user_nickname IS NULL THEN
        SET NEW.user_nickname = NEW.user_name;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode = @saved_sql_mode */;
/*!50003 SET character_set_client = @saved_cs_client */;
/*!50003 SET character_set_results = @saved_cs_results */;
/*!50003 SET collation_connection = @saved_col_connection */;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2023-12-07 14:39:57
