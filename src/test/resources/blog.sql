SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

DROP TABLE IF EXISTS `blogs`;
CREATE TABLE `blogs` (
                         `blog_id` bigint NOT NULL COMMENT '블로그 아이디 , auto increasement',
                         `blog_fid` varchar(50) NOT NULL COMMENT '블로그_대표_아이디',
                         `blog_main` tinyint NOT NULL DEFAULT '0' COMMENT '1-대표불로그, 2-일반블로그',
                         `blog_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '불로그 이름',
                         `blog_mb_nickname` varchar(100) NOT NULL COMMENT '블로그  맴버 닉네임',
                         `blog_description` text COMMENT '블로그 설명',
                         `blog_is_public` tinyint NOT NULL DEFAULT '1' COMMENT 'default=1, 1-공개, 2-비공개',
                         `created_at` datetime NOT NULL COMMENT '생성일자',
                         `updated_at` datetime DEFAULT NULL COMMENT '수정일자'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `blog_members_mapping`;
CREATE TABLE `blog_members_mapping` (
                                        `blog_member_id` bigint NOT NULL COMMENT 'Auto Increaments',
                                        `mb_no` bigint NOT NULL COMMENT '회원번호 ,  mb_no + blog_id uniquekey 설정',
                                        `blog_id` bigint NOT NULL COMMENT '블로그 아이디 , mb_no + blog_id uniquekey 설정',
                                        `role_id` varchar(50) NOT NULL COMMENT '권한_아이디'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
                              `category_id` bigint NOT NULL COMMENT '블로그_ 카테고리_아이디 , auto_increament',
                              `category_pid` bigint DEFAULT NULL COMMENT '블로그_ 카테고리_부모_아이디,  자기참조',
                              `blog_id` bigint NOT NULL COMMENT '블로그 아이디 , auto increasement',
                              `topic_id` int DEFAULT NULL COMMENT '토픽_아이디(FK)',
                              `category_name` varchar(100) NOT NULL COMMENT '블로그_카테고리명',
                              `category_sec` int NOT NULL COMMENT '블로그_카테고리  정렬순서',
                              `created_at` datetime NOT NULL COMMENT '블로그_카테고리 생성일자',
                              `updated_at` datetime DEFAULT NULL COMMENT '블로그_카테고리_수정일자'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `members`;
CREATE TABLE `members` (
                           `mb_no` bigint NOT NULL COMMENT '회원번호 ,  auto_increament',
                           `mb_email` varchar(100) NOT NULL COMMENT '이메일 중복체크를 위해서 unique index 설정',
                           `mb_name` varchar(50) NOT NULL COMMENT '회원_이름',
                           `mb_password` varchar(200) NOT NULL COMMENT '회원_비밀번호',
                           `mb_mobile` varchar(100) DEFAULT NULL COMMENT '모바일 연락처',
                           `created_at` datetime NOT NULL COMMENT '생성일자',
                           `updated_at` datetime DEFAULT NULL COMMENT '수정일자',
                           `withdrawal_at` datetime DEFAULT NULL COMMENT '탈퇴일자'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts` (
                         `post_id` bigint NOT NULL COMMENT '게시물 아이디,auto_increamet',
                         `blog_id` bigint NOT NULL COMMENT '블로그 아이디 , auto increasement',
                         `created_mb_no` bigint NOT NULL COMMENT '게시글_생성_회원번호',
                         `updated_mb_no` bigint DEFAULT NULL COMMENT '게시글_수정_회원번호',
                         `post_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '게시글  제목',
                         `post_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '게시글 내용',
                         `post_is_public` tinyint NOT NULL DEFAULT '1' COMMENT '공개여부 , DEFAULT  1, 공개',
                         `created_at` datetime NOT NULL COMMENT '작성일자',
                         `updated_at` datetime DEFAULT NULL COMMENT '수정일자'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `post_categories_mapping`;
CREATE TABLE `post_categories_mapping` (
                                           `post_category_id` bigint NOT NULL COMMENT '포스트_카테고리_맵핑 아이디',
                                           `post_id` bigint NOT NULL COMMENT '게시물 아이디,auto_increamet',
                                           `category_id` bigint NOT NULL COMMENT '블로그_ 카테고리_아이디 , auto_increament'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `post_tags_mapping`;
CREATE TABLE `post_tags_mapping` (
                                     `post_tag_id` bigint NOT NULL COMMENT '포스트 테그_연결_아이디,auto_increaments',
                                     `tag_id` bigint NOT NULL COMMENT '테그 아이디 , tag_id + post_id unique key 설정',
                                     `post_id` bigint NOT NULL COMMENT '게시물 아이디'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
                         `role_id` varchar(50) NOT NULL COMMENT '권한_아이디',
                         `role_name` varchar(100) NOT NULL COMMENT '권한_이름',
                         `role_description` varchar(200) DEFAULT NULL COMMENT '권한_설명'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `roles` (`role_id`, `role_name`, `role_description`) VALUES
                                                                     ('ROLE_ADMIN', '블로그 시스템 관리자', '전체 시스템 관리자'),
                                                                     ('ROLE_MEMBER', '블로그_회원', '블로그 회원,  팀 블로그 사용시 설정'),
                                                                     ('ROLE_OWNER', '블로그 소유자', '블로그를 소유한 관리자');

DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
                        `tag_id` bigint NOT NULL COMMENT 'auto_increaments, 테그 아이디',
                        `tag_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'unique 설정'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `topics`;
CREATE TABLE `topics` (
                          `topic_id` int NOT NULL COMMENT '토픽_아이디, AUTO_INCREAMENTS',
                          `topic_pid` int DEFAULT NULL COMMENT '토픽_부모_아이디',
                          `topic_name` varchar(100) NOT NULL COMMENT '토픽_네임',
                          `topic_sec` int NOT NULL COMMENT '토픽_정렬순서,default 1',
                          `created_at` datetime NOT NULL COMMENT '생성일자',
                          `updated_at` datetime DEFAULT NULL COMMENT '수정일자'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `topics` (`topic_id`, `topic_pid`, `topic_name`, `topic_sec`, `created_at`, `updated_at`) VALUES
                                                                                                          (1, NULL, '라이프', 1, '2024-12-11 18:08:57', NULL),
                                                                                                          (2, NULL, '여행·맛집', 2, '2024-12-11 18:08:57', NULL),
                                                                                                          (3, NULL, '문화·연예', 3, '2024-12-11 18:08:57', NULL),
                                                                                                          (4, NULL, ' IT', 4, '2024-12-11 18:08:57', NULL),
                                                                                                          (5, NULL, '스포츠', 5, '2024-12-11 18:08:57', NULL),
                                                                                                          (6, NULL, '시사', 6, '2024-12-11 18:08:57', NULL),
                                                                                                          (7, NULL, '이벤트', 7, '2024-12-11 18:08:57', NULL),
                                                                                                          (8, 1, '일상다반사', 1, '2024-12-11 18:08:57', NULL),
                                                                                                          (9, 1, '해외생활', 2, '2024-12-11 18:08:57', NULL),
                                                                                                          (10, 1, '연예·결혼', 3, '2024-12-11 18:08:57', NULL),
                                                                                                          (11, 1, '육아', 4, '2024-12-11 18:08:57', NULL),
                                                                                                          (12, 1, '생활정보', 5, '2024-12-11 18:08:57', NULL),
                                                                                                          (13, 1, '반려동물', 6, '2024-12-11 18:08:57', NULL),
                                                                                                          (14, 1, '취미', 7, '2024-12-11 18:08:57', NULL),
                                                                                                          (15, 1, '사진', 8, '2024-12-11 18:08:57', NULL),
                                                                                                          (16, 1, '요리', 9, '2024-12-11 18:08:57', NULL),
                                                                                                          (17, 1, '자동차', 10, '2024-12-11 18:08:57', NULL),
                                                                                                          (18, 1, '인테리어', 11, '2024-12-11 18:08:57', NULL),
                                                                                                          (19, 1, '패션뷰티', 12, '2024-12-11 18:08:57', NULL),
                                                                                                          (20, 1, '건강', 13, '2024-12-11 18:08:57', NULL),
                                                                                                          (21, 1, '군대', 14, '2024-12-11 18:08:57', NULL),
                                                                                                          (22, 2, '국내여행', 1, '2024-12-11 18:08:57', NULL),
                                                                                                          (23, 2, '해외여행', 2, '2024-12-11 18:08:57', NULL),
                                                                                                          (24, 2, '캠핑·등산', 3, '2024-12-11 18:08:57', NULL),
                                                                                                          (25, 2, '맛집', 4, '2024-12-11 18:08:57', NULL),
                                                                                                          (26, 2, '카페·디저트', 5, '2024-12-11 18:08:57', NULL),
                                                                                                          (27, 3, 'TV', 1, '2024-12-11 18:08:57', NULL),
                                                                                                          (28, 3, '스타', 2, '2024-12-11 18:08:57', NULL),
                                                                                                          (29, 3, '영화', 3, '2024-12-11 18:08:57', NULL),
                                                                                                          (30, 3, '음악', 4, '2024-12-11 18:08:57', NULL),
                                                                                                          (31, 3, '책', 5, '2024-12-11 18:08:57', NULL),
                                                                                                          (32, 3, '만화·애니', 6, '2024-12-11 18:08:57', NULL),
                                                                                                          (33, 3, '공연·전시·축제', 7, '2024-12-11 18:08:57', NULL),
                                                                                                          (34, 3, '창작', 8, '2024-12-11 18:08:57', NULL),
                                                                                                          (35, 4, 'IT 인터넷', 1, '2024-12-11 18:08:57', NULL),
                                                                                                          (36, 4, '모바일', 2, '2024-12-11 18:08:57', NULL),
                                                                                                          (37, 4, '게임', 3, '2024-12-11 18:08:57', NULL),
                                                                                                          (38, 4, '과학', 4, '2024-12-11 18:08:57', NULL),
                                                                                                          (39, 4, 'IT 제품리뷰', 5, '2024-12-11 18:08:57', NULL),
                                                                                                          (40, 5, '스포츠일반', 1, '2024-12-11 18:08:57', NULL),
                                                                                                          (41, 5, '축구', 2, '2024-12-11 18:08:57', NULL),
                                                                                                          (42, 5, '야구', 3, '2024-12-11 18:08:57', NULL),
                                                                                                          (43, 5, '농구', 4, '2024-12-11 18:08:57', NULL),
                                                                                                          (44, 5, '배구', 5, '2024-12-11 18:08:57', NULL),
                                                                                                          (45, 5, '골프', 6, '2024-12-11 18:08:57', NULL),
                                                                                                          (46, 6, '정치', 1, '2024-12-11 18:08:57', NULL),
                                                                                                          (47, 6, '사회', 2, '2024-12-11 18:08:57', NULL),
                                                                                                          (48, 6, '교육', 3, '2024-12-11 18:08:57', NULL),
                                                                                                          (49, 6, '국제', 4, '2024-12-11 18:08:57', NULL),
                                                                                                          (50, 6, '경제', 5, '2024-12-11 18:08:57', NULL),
                                                                                                          (51, 6, '경영·직장', 6, '2024-12-11 18:08:57', NULL);


ALTER TABLE `blogs`
    ADD PRIMARY KEY (`blog_id`),
  ADD UNIQUE KEY `blog_fid` (`blog_fid`);

ALTER TABLE `blog_members_mapping`
    ADD PRIMARY KEY (`blog_member_id`),
  ADD UNIQUE KEY `uk_mb_no_blog_id` (`mb_no`,`blog_id`),
  ADD KEY `fk_mapping_blog_id` (`blog_id`),
  ADD KEY `fk_mapping_role_id` (`role_id`);

ALTER TABLE `categories`
    ADD PRIMARY KEY (`category_id`),
  ADD KEY `fk_blog_id` (`blog_id`),
  ADD KEY `fk_topic_id` (`topic_id`),
  ADD KEY `fk_parrent_category_id` (`category_pid`);

ALTER TABLE `members`
    ADD PRIMARY KEY (`mb_no`),
  ADD UNIQUE KEY `mb_email` (`mb_email`),
  ADD UNIQUE KEY `mb_mobile` (`mb_mobile`);

ALTER TABLE `posts`
    ADD PRIMARY KEY (`post_id`),
  ADD KEY `fk_posts_blog_id` (`blog_id`),
  ADD KEY `fk_posts_created_mb_no` (`created_mb_no`),
  ADD KEY `fk_posts_updated_mb_no` (`updated_mb_no`);

ALTER TABLE `post_categories_mapping`
    ADD PRIMARY KEY (`post_category_id`),
  ADD UNIQUE KEY `uk_post_id_category_id` (`post_id`,`category_id`) USING BTREE,
  ADD KEY `fk_mapping_category_id` (`category_id`);

ALTER TABLE `post_tags_mapping`
    ADD PRIMARY KEY (`post_tag_id`),
  ADD KEY `fk_post_tag_post_id` (`post_id`),
  ADD KEY `fk_post_tag_tag_id` (`tag_id`);

ALTER TABLE `roles`
    ADD PRIMARY KEY (`role_id`);

ALTER TABLE `tags`
    ADD PRIMARY KEY (`tag_id`),
  ADD UNIQUE KEY `tag_name` (`tag_name`);

ALTER TABLE `topics`
    ADD PRIMARY KEY (`topic_id`),
  ADD KEY `fk_parent_topic_id` (`topic_pid`);


ALTER TABLE `blogs`
    MODIFY `blog_id` bigint NOT NULL AUTO_INCREMENT COMMENT '블로그 아이디 , auto increasement', AUTO_INCREMENT=582;

ALTER TABLE `blog_members_mapping`
    MODIFY `blog_member_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Auto Increaments', AUTO_INCREMENT=33;

ALTER TABLE `categories`
    MODIFY `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '블로그_ 카테고리_아이디 , auto_increament', AUTO_INCREMENT=662;

ALTER TABLE `members`
    MODIFY `mb_no` bigint NOT NULL AUTO_INCREMENT COMMENT '회원번호 ,  auto_increament', AUTO_INCREMENT=652;

ALTER TABLE `posts`
    MODIFY `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '게시물 아이디,auto_increamet';

ALTER TABLE `post_categories_mapping`
    MODIFY `post_category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '포스트_카테고리_맵핑 아이디';

ALTER TABLE `post_tags_mapping`
    MODIFY `post_tag_id` bigint NOT NULL AUTO_INCREMENT COMMENT '포스트 테그_연결_아이디,auto_increaments';

ALTER TABLE `tags`
    MODIFY `tag_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'auto_increaments, 테그 아이디';

ALTER TABLE `topics`
    MODIFY `topic_id` int NOT NULL AUTO_INCREMENT COMMENT '토픽_아이디, AUTO_INCREAMENTS', AUTO_INCREMENT=627;


ALTER TABLE `blog_members_mapping`
    ADD CONSTRAINT `fk_mapping_blog_id` FOREIGN KEY (`blog_id`) REFERENCES `blogs` (`blog_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_mapping_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_mb_no` FOREIGN KEY (`mb_no`) REFERENCES `members` (`mb_no`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `categories`
    ADD CONSTRAINT `fk_blog_id` FOREIGN KEY (`blog_id`) REFERENCES `blogs` (`blog_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_parrent_category_id` FOREIGN KEY (`category_pid`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`topic_id`) ON DELETE SET NULL ON UPDATE RESTRICT;

ALTER TABLE `posts`
    ADD CONSTRAINT `fk_posts_blog_id` FOREIGN KEY (`blog_id`) REFERENCES `blogs` (`blog_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_posts_created_mb_no` FOREIGN KEY (`created_mb_no`) REFERENCES `members` (`mb_no`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_posts_updated_mb_no` FOREIGN KEY (`updated_mb_no`) REFERENCES `members` (`mb_no`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `post_categories_mapping`
    ADD CONSTRAINT `fk_mapping_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_mapping_post_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `post_tags_mapping`
    ADD CONSTRAINT `fk_post_tag_post_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `fk_post_tag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE `topics`
    ADD CONSTRAINT `fk_parent_topic_id` FOREIGN KEY (`topic_pid`) REFERENCES `topics` (`topic_id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
