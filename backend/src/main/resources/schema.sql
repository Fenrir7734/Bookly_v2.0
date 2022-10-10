DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `created_at` datetime(6) NOT NULL,
                        `email` varchar(255) NOT NULL,
                        `firstname` varchar(50) NOT NULL,
                        `lastname` varchar(50) NOT NULL,
                        `password` varchar(60) NOT NULL,
                        `role` varchar(255) DEFAULT NULL,
                        `username` varchar(30) NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_username` (`username`),
                        UNIQUE KEY `UK_email` (`email`)
);

CREATE TABLE `book` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `author` varchar(255) DEFAULT NULL,
                        `cover` varchar(255) DEFAULT NULL,
                        `created_at` datetime(6) DEFAULT NULL,
                        `description` varchar(5000) DEFAULT NULL,
                        `title` varchar(255) DEFAULT NULL,
                        `updated_at` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`id`)
);

CREATE TABLE `review` (
                          `book_id` bigint NOT NULL,
                          `user_id` bigint NOT NULL,
                          `content` varchar(1000) DEFAULT NULL,
                          `created_at` datetime(6) DEFAULT NULL,
                          `rate` int NOT NULL,
                          `updated_at` datetime(6) DEFAULT NULL,
                          PRIMARY KEY (`book_id`,`user_id`),
                          CONSTRAINT `book_id_FK` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE,
                          CONSTRAINT `user_id_FK` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);