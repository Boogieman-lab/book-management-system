-- V1.10: department 테이블 Soft Delete 컬럼 추가 (V1.9에서 누락)
ALTER TABLE department
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부';
