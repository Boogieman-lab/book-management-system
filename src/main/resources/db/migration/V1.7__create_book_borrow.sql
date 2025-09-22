CREATE TABLE book_borrow
(
    book_borrow_id   BIGINT AUTO_INCREMENT COMMENT '대출 ID',
    book_hold_id     BIGINT NOT NULL COMMENT '도서 보유 ID',
    book_id          BIGINT NOT NULL COMMENT '도서 ID',
    user_id          BIGINT NOT NULL COMMENT '사용자 ID',
    reason           VARCHAR(512) COMMENT '대출 사유',
    status           VARCHAR(50) NOT NULL COMMENT '대출 상태',
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_borrow_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='도서 대출 테이블';