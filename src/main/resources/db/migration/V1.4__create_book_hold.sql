CREATE TABLE book_hold
(
    book_hold_id     BIGINT AUTO_INCREMENT COMMENT '도서 보유 ID',
    book_id          BIGINT NOT NULL COMMENT '도서 아이템 ID',
    status           VARCHAR(50) NOT NULL COMMENT '보유 상태',
    location         VARCHAR(50) COMMENT '위치',
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_hold_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='도서 보유 테이블';