CREATE TABLE book_request
(
    book_request_id  BIGINT AUTO_INCREMENT COMMENT '패널티 ID',
    user_id         BIGINT       NOT NULL COMMENT '사용자 ID',
    title           VARCHAR(512) NOT NULL COMMENT '도서 제목',
    authors         VARCHAR(255)          COMMENT '저자 리스트',
    publisher       VARCHAR(255)          COMMENT '출판사',
    isbn            VARCHAR(50)           COMMENT 'ISBN10 또는 ISBN13 (공백 구분)',
    reason          VARCHAR(512)          COMMENT '신청 사유',
    status          VARCHAR(20)  NOT NULL COMMENT '책 신청 상태 (PENDING/APPROVED/REJECTED)',
    created_at DATETIME NOT NULL COMMENT '생성일시',
    updated_at DATETIME NOT NULL COMMENT '수정일시',
    PRIMARY KEY (book_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='도서 희망 테이블';