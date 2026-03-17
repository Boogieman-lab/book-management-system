-- ============================================================
-- V1.9: Soft Delete 컬럼(is_deleted) 및 누락 컬럼 일괄 추가
-- ============================================================

-- users: Soft Delete
ALTER TABLE users
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부';

-- book: Soft Delete + 판매 상태 컬럼
ALTER TABLE book
    ADD COLUMN status     VARCHAR(50) NULL    COMMENT '판매 상태',
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부';

-- book_hold: Soft Delete
ALTER TABLE book_hold
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부';

-- book_borrow: 대출/반납 일시 컬럼 + Soft Delete
ALTER TABLE book_borrow
    ADD COLUMN borrow_date DATETIME NULL COMMENT '대출일시',
    ADD COLUMN due_date    DATETIME NULL COMMENT '반납예정일',
    ADD COLUMN return_date DATETIME NULL COMMENT '반납일시',
    ADD COLUMN is_deleted  BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부';

-- reservation: 예약 만료 일시 컬럼 + Soft Delete
ALTER TABLE reservation
    ADD COLUMN expire_at  DATETIME NULL COMMENT '예약만료일시',
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부';

-- book_request: Soft Delete
ALTER TABLE book_request
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부';
