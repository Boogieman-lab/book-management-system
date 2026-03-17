-- =========================================
-- USERS
-- =========================================
INSERT INTO users (email, password, name, department_id, profile_image, role, login_fail_count, created_at, updated_at)
VALUES
    ('testuser@example.com', 'encrypted_pw', '김수현', 1, NULL, 'USER', 0, NOW(), NOW());

INSERT INTO users (email, password, name, department_id, profile_image, role, login_fail_count, created_at, updated_at)
VALUES
    ('testuser2@example.com', 'encrypted_pw', '이승원', 1, NULL, 'USER', 0, NOW(), NOW());

select * from users;
-- =========================================
-- BOOK_HOLD (도서 보유 2권)
-- book_id = 11 기준
-- =========================================
INSERT INTO book_hold (book_hold_id, book_id, location, status)
VALUES
    (1, 11, 'A-01', 'AVAILABLE'),
    (2, 11, 'A-02', 'AVAILABLE'),
#     (3, 11, 'A-03', 'BORROWED'),   -- 실제로는 BORROWED가 book_borrow에서 관리되지만 화면 표시용 예제
    (4, 11, 'A-04', 'LOST'),
    (5, 11, 'A-05', 'DISCARDED');

select * from book_hold;
# delete from book_borrow;
-- =========================================
-- BOOK_BORROW (대출 기록)
-- book_hold_id = 3 → 대출 중
-- =========================================
INSERT INTO book_borrow (book_hold_id, book_id, user_id, reason, status, created_at, updated_at)
VALUES
    (3, 11, 1, '업무 참고용', 'BORROWED', NOW(), NOW()),                       -- 대출 중
    (4, 11, 2, '개인 학습', 'OVERDUE', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 10 DAY); -- 연체

select * from book_borrow;
-- =========================================
-- RESERVATION (예약 기록)
-- book_hold_id = 102 → 예약 신청
-- =========================================
-- reservation: 예약
# delete from reservation
INSERT INTO reservation (reservation_id, book_hold_id, user_id, status, reserved_at, created_at, updated_at)
VALUES
    (1, 2, 3, 'REQUESTED', NOW(), NOW(), NOW()),  -- 예약 있음
    (2, 3, 4, 'REQUESTED', NOW(), NOW(), NOW());  -- 대출 중이지만 예약자 있음

SELECT * FROM reservation;