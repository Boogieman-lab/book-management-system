CREATE TABLE book
(
    book_id         BIGINT AUTO_INCREMENT COMMENT '도서 ID',
    title           VARCHAR(512) NOT NULL COMMENT '도서명',
    authors         VARCHAR(512) NULL COMMENT '저자',
    publisher       VARCHAR(255) NULL COMMENT '출판사',
    isbn            VARCHAR(50) NULL COMMENT '국제 표준 도서번호',
    pub_date        DATE NULL COMMENT '출간일',
    page_count      INT NULL COMMENT '페이지 수',
    genre           VARCHAR(50) COMMENT '장르',
    image_url       VARCHAR(512) NULL COMMENT '표지 이미지',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='도서 테이블';