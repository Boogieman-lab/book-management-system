CREATE TABLE penalty
(
    penalty_id       BIGINT AUTO_INCREMENT COMMENT '패널티 ID',
    user_id          BIGINT NOT NULL COMMENT '사용자 ID',
    borrow_id        BIGINT NOT NULL COMMENT '대출 ID',
    amount           INT COMMENT '금액',
    status           VARCHAR(50) NOT NULL COMMENT '패널티 상태',
    paid_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '납부일시',
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (penalty_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='패널티 테이블';