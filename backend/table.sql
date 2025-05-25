CREATE TABLE `album`
(
    `album_id`          int NOT NULL AUTO_INCREMENT,
    `album_name`        text,
    `album_describtion` text,
    `user_id`           int DEFAULT NULL,
    `name`              text,
    PRIMARY KEY (`album_id`)
);
CREATE TABLE `img`
(
    `img_id`          int        NOT NULL AUTO_INCREMENT,
    `img_name`        text,
    `img_date`        timestamp  NULL     DEFAULT NULL,
    `pub`             tinyint(1)          DEFAULT '0',
    `img_pos`         text,
    `user_id`         int                 DEFAULT NULL,
    `valid`           tinyint(1) NOT NULL DEFAULT '1',
    `img_describtion` text,
    `album_id`        int                 DEFAULT NULL,
    PRIMARY KEY (`img_id`)
);
CREATE TABLE `img_people`
(
    `ip_id`     int NOT NULL AUTO_INCREMENT,
    `img_id`    int DEFAULT NULL,
    `people_id` int DEFAULT NULL,
    `user_id`   int DEFAULT NULL,
    PRIMARY KEY (`ip_id`)
);
CREATE TABLE `img_tag`
(
    `it_id`   int NOT NULL AUTO_INCREMENT,
    `img_id`  int DEFAULT NULL,
    `tag_id`  int DEFAULT NULL,
    `user_id` int DEFAULT NULL,
    PRIMARY KEY (`it_id`)
);

CREATE TABLE `people` (
    `people_id` int NOT NULL AUTO_INCREMENT,
    `name`      text,
    `user_id`   int DEFAULT NULL,
    `valid`     tinyint(1) NOT NULL DEFAULT 1,
    `nickname`  text AS (`name`) STORED, -- `TEXT` 类型通常需要 STORED
    PRIMARY KEY (`people_id`)
);
CREATE TABLE `tag`
(
    `tag_id`   int        NOT NULL AUTO_INCREMENT,
    `tag_name` text,
    `valid`    tinyint(1) NOT NULL DEFAULT '1',
    `user_id`  int                 DEFAULT NULL,
    PRIMARY KEY (`tag_id`)
);
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
);
CREATE TABLE `role`
(
    `role_id`   int  NOT NULL AUTO_INCREMENT,
    `role_name` text NOT NULL,
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `role_pk` (`role_id`)
);
CREATE TABLE `permission`
(
    `user_id`       int NOT NULL,
    `role_id`       int NOT NULL,
    `permission_id` int NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`permission_id`),
    UNIQUE KEY `permission_pk` (`permission_id`)
);