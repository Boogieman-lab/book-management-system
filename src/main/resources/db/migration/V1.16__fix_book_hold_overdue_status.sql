-- V1.16__fix_book_hold_overdue_status.sql
-- V1.15에서 잘못 삽입된 book_hold.status = 'OVERDUE' 를 'BORROWED'로 복구.
-- BookHoldStatus enum에 OVERDUE가 없어 JPA 매핑 오류가 발생하므로 수정.
-- 연체 여부는 book_borrow.status(OVERDUE)로만 추적한다.

UPDATE book_hold
SET    status = 'BORROWED'
WHERE  status = 'OVERDUE';
