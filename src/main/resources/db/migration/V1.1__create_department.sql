CREATE TABLE department
(
    department_id    BIGINT AUTO_INCREMENT COMMENT '부서 ID',
    name             VARCHAR(50) COMMENT '부서명',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at       DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='부서 테이블';