CREATE TABLE book_request
(
    book_request_id  BIGINT AUTO_INCREMENT COMMENT '패널티 ID',
    user_id          BIGINT NOT NULL COMMENT '사용자 ID',
    book_id          BIGINT NOT NULL COMMENT '도서 ID',
    reason           VARCHAR(512) COMMENT '신청 사유',
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='도서 희망 테이블';