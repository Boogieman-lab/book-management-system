CREATE TABLE users
(
    user_id          BIGINT AUTO_INCREMENT COMMENT '사용자 ID',
    email            VARCHAR(255) NOT NULL UNIQUE COMMENT '사용자 이메일',
    password         VARCHAR(255) NULL COMMENT '비밀번호',
    name             VARCHAR(50) NOT NULL COMMENT '사용자 이름',
    department_id    BIGINT COMMENT '부서 ID',
    profile_image    VARCHAR(500) COMMENT '프로필 이미지 URL',
    role             VARCHAR(20)  NOT NULL COMMENT '권한',
    login_fail_count INT          NOT NULL DEFAULT 0 COMMENT '로그인 실패 횟수',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='사용자 테이블';