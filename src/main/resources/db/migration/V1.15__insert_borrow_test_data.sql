-- V1.15__insert_borrow_test_data.sql
-- book_hold 상태가 BORROWED인 도서에 대한 book_borrow 테스트 데이터 삽입
--
-- ※ 전제 조건: user_id 1, 2 가 DB에 존재해야 합니다.
--   존재하지 않을 경우 실제 users 테이블의 ID로 값을 교체하세요.
--
-- 기준일: 2026-03-23
--
-- ─────────────────────────────────────────────────────────────────────
-- BORROWED book_hold_id 매핑 (V1.13 + V1.14 AUTO_INCREMENT 삽입 순서 기준)
--
--   [V1.13]
--   book_hold_id 25  ← book_id 21  Java (Turner Peter)
--   book_hold_id 27  ← book_id 22  Java (Adams Joel 외)
--   book_hold_id 29  ← book_id 23  Bible (Chongqing)
--   book_hold_id 31  ← book_id 24  Java (Herbert Schildt, Danny Coward)
--   book_hold_id 33  ← book_id 25  Java
--   book_hold_id 34  ← book_id 26  Java
--   book_hold_id 35  ← book_id 27  Java
--   book_hold_id 36  ← book_id 28  Java
--   book_hold_id 37  ← book_id 29  Java
--   book_hold_id 38  ← book_id 30  Java
--
--   [V1.14]
--   book_hold_id 50  ← book_id 39  AI 자율학습 클로드 코드·코덱스 CLI·제미나이 CLI 완전 활용법
--   book_hold_id 52  ← book_id 40  요즘 바이브 코딩 커서 AI 30가지 프로그램 만들기
-- ─────────────────────────────────────────────────────────────────────

INSERT INTO book_borrow
    (book_hold_id, book_id, user_id, reason, status, borrow_date, due_date, return_date)
VALUES
    -- ── 정상 대출중 (반납예정일 ≥ 2026-03-23) ────────────────────────
    (25, 21, 1, '업무 참고용',        'BORROWED', '2026-03-15 09:00:00', '2026-03-29 23:59:59', NULL),
    (27, 22, 2, '자기계발',           'BORROWED', '2026-03-10 14:30:00', '2026-03-24 23:59:59', NULL),
    (33, 25, 1, '스터디 자료',        'BORROWED', '2026-03-18 11:00:00', '2026-04-01 23:59:59', NULL),
    (34, 26, 2, '업무 참고용',        'BORROWED', '2026-03-20 10:00:00', '2026-04-03 23:59:59', NULL),
    (36, 28, 1, '자기계발',           'BORROWED', '2026-03-16 16:00:00', '2026-03-30 23:59:59', NULL),
    (37, 29, 2, '스터디 자료',        'BORROWED', '2026-03-12 09:30:00', '2026-03-26 23:59:59', NULL),
    (50, 39, 1, 'AI 코딩 도구 학습',  'BORROWED', '2026-03-21 13:00:00', '2026-04-04 23:59:59', NULL),
    (52, 40, 2, '바이브 코딩 실습',   'BORROWED', '2026-03-22 10:00:00', '2026-04-05 23:59:59', NULL),

    -- ── 연체중 (반납예정일 < 2026-03-23, 미반납) ──────────────────────
    (29, 23, 2, '업무 참고용',        'OVERDUE',  '2026-03-01 10:00:00', '2026-03-15 23:59:59', NULL),
    (31, 24, 1, '자기계발',           'OVERDUE',  '2026-02-20 09:00:00', '2026-03-06 23:59:59', NULL),
    (35, 27, 2, '스터디 자료',        'OVERDUE',  '2026-03-05 14:00:00', '2026-03-19 23:59:59', NULL),
    (38, 30, 1, '업무 참고용',        'OVERDUE',  '2026-02-28 11:00:00', '2026-03-14 23:59:59', NULL);

-- book_hold 상태는 BookHoldStatus enum 범위(AVAILABLE/BORROWED/RESERVE_HOLD/LOST/DISCARDED)만 사용.
-- 연체 여부는 book_borrow.status(OVERDUE)로만 추적하며, book_hold는 BORROWED 유지.
