CREATE TABLE reservation
(
    reservation_id   BIGINT AUTO_INCREMENT COMMENT '예약 ID',
    book_hold_id     BIGINT NOT NULL COMMENT '도서 보유 ID',
    user_id          BIGINT NOT NULL COMMENT '사용자 ID',
    status           VARCHAR(50) NOT NULL COMMENT '예약상태',
    reserved_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '예약일시',
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='예약 테이블';