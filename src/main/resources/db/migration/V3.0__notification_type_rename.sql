-- ============================================================
-- V3.0: notification.type ENUM 값 정정
-- BORROW_APPROVED  → BOOK_REQUEST_APPROVED
-- BORROW_REJECTED  → BOOK_REQUEST_REJECTED
-- 기존 데이터가 없어도 안전하게 실행됩니다.
-- ============================================================

UPDATE notification SET type = 'BOOK_REQUEST_APPROVED' WHERE type = 'BORROW_APPROVED';
UPDATE notification SET type = 'BOOK_REQUEST_REJECTED' WHERE type = 'BORROW_REJECTED';
