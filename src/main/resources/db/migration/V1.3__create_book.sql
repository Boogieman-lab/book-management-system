CREATE TABLE book
(
    book_id        BIGINT AUTO_INCREMENT COMMENT '도서 ID',
    title          VARCHAR(512) NOT NULL COMMENT '도서 제목',
    contents       TEXT NULL COMMENT '도서 소개',
    url            VARCHAR(1024) NULL COMMENT '도서 상세 URL',
    isbn           VARCHAR(50) NULL COMMENT 'ISBN10 또는 ISBN13 (공백 구분)',
    published_at       DATETIME NULL COMMENT '출판일',
    authors        JSON NULL COMMENT '저자 리스트',
    translators    JSON NULL COMMENT '번역자 리스트',
    publisher      VARCHAR(255) NULL COMMENT '출판사',
    price          INT NOT NULL DEFAULT 0 COMMENT '정가',
    sale_price     INT NOT NULL DEFAULT 0 COMMENT '판매가',
    thumbnail      VARCHAR(512) NULL COMMENT '표지 이미지 URL',
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at     DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='도서 테이블';
