-- ============================================================
-- V5.0: overdue_record (연체 기록 테이블) 생성
-- ERD에 정의된 테이블을 Flyway에 반영
--
-- 목적: 연체 발생 시 패널티(대출 제한 일수, 제한 종료일)를 기록하고
--       users.restriction_until 갱신의 근거 데이터를 보관합니다.
--
-- is_deleted 설계 결정:
--   Hard Delete 금지 — 연체 기록 삭제 시 대출 제한 우회 가능성이 있으므로
--   감사 목적의 소프트 삭제만 허용합니다.
-- ============================================================

CREATE TABLE overdue_record
(
    overdue_record_id BIGINT   AUTO_INCREMENT                                    COMMENT '연체 기록 ID',
    user_id           BIGINT   NOT NULL                                          COMMENT '사용자 ID (NOT NULL)',
    book_borrow_id    BIGINT   NOT NULL                                          COMMENT '대출 ID (NOT NULL)',
    overdue_days      INT      NOT NULL DEFAULT 0                                COMMENT '연체 일수',
    restriction_days  INT      NOT NULL DEFAULT 0                                COMMENT '대출 제한 일수',
    restriction_until DATETIME NULL                                              COMMENT '제한 종료일',
    is_deleted        BOOLEAN  NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 플래그 (감사 데이터 보호 — 삭제 시 대출 제한 우회 방지)',
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at        DATETIME NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (overdue_record_id),
    CONSTRAINT fk_overdue_record_user        FOREIGN KEY (user_id)        REFERENCES users (user_id),
    CONSTRAINT fk_overdue_record_book_borrow FOREIGN KEY (book_borrow_id) REFERENCES book_borrow (book_borrow_id),
    INDEX idx_overdue_record_user (user_id, created_at DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '연체 기록 테이블';
