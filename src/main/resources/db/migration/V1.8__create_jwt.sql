CREATE TABLE jwt
(
    jwt_id         BINARY(16)   NOT NULL COMMENT '토큰 ID (UUID:BINARY(16))',
    email          VARCHAR(255) NOT NULL  COMMENT '사용자 이메일',
    refresh_token  TEXT         NOT NULL COMMENT 'refresh token',
    expired_at     DATETIME(6)  NOT NULL COMMENT '토큰 만료일시',
    issued_at      DATETIME(6)  NOT NULL COMMENT '토큰 발급일시',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (jwt_id)
) ENGINE=InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'JWT 테이블';