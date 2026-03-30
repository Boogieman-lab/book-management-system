-- ============================================================
-- V4.0: users 테이블에 restriction_until 컬럼 추가 (U-04)
-- 연체 대출 제한 종료일 캐시 — overdue_record N+1 제거 목적
-- NULL이면 대출 제한 없음
-- ============================================================

ALTER TABLE users
    ADD COLUMN restriction_until DATETIME NULL COMMENT '연체 대출 제한 종료일 캐시 — NULL이면 제한 없음';
