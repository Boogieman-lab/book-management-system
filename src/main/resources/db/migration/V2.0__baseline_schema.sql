-- ============================================================
-- V2.0 기준 스키마 (V1.0 ~ V1.19 통합)
-- V1.x 마이그레이션 전체를 이 파일 하나로 대체
-- ============================================================
-- 실행 전: DB를 초기화하거나 flyway_schema_history 테이블을 truncate 해야 합니다.
-- 테이블 생성 순서: FK 의존성 기준
--   1. department
--   2. users           (→ department)
--   3. book
--   4. book_hold       (→ book)
--   5. reservation     (→ book_hold, users)
--   6. book_borrow     (→ book_hold, book, users)
--   7. book_request    (→ users)
--   8. notification    (→ users)
-- ============================================================


-- ============================================================
-- 1. department (부서 테이블)
-- ============================================================
CREATE TABLE department
(
    department_id BIGINT      AUTO_INCREMENT                                    COMMENT '부서 ID',
    name          VARCHAR(50) NULL                                              COMMENT '부서명',
    is_deleted    BOOLEAN     NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 여부',
    created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at    DATETIME    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (department_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '부서 테이블';


-- ============================================================
-- 2. users (사용자 테이블)
-- ============================================================
CREATE TABLE users
(
    user_id          BIGINT       AUTO_INCREMENT                                    COMMENT '사용자 ID',
    email            VARCHAR(255) NOT NULL                                          COMMENT '사용자 이메일',
    password         VARCHAR(255) NULL                                              COMMENT '비밀번호 (소셜 로그인 시 NULL)',
    name             VARCHAR(50)  NOT NULL                                          COMMENT '사용자 이름',
    department_id    BIGINT       NULL                                              COMMENT '부서 ID',
    profile_image    VARCHAR(500) NULL                                              COMMENT '프로필 이미지 URL',
    role             VARCHAR(20)  NOT NULL                                          COMMENT '권한 (USER, ADMIN 등)',
    login_fail_count INT          NOT NULL DEFAULT 0                                COMMENT '로그인 실패 횟수',
    is_locked        BOOLEAN      NOT NULL DEFAULT FALSE                            COMMENT '계정 잠금 여부',
    is_deleted       BOOLEAN      NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 여부',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at       DATETIME     NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (user_id),
    UNIQUE INDEX uq_users_email (email),
    CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES department (department_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '사용자 테이블';


-- ============================================================
-- 3. book (도서 테이블) — 알라딘 API 기반 스키마
-- ============================================================
CREATE TABLE book
(
    book_id              BIGINT        AUTO_INCREMENT                                    COMMENT '도서 ID',
    title                VARCHAR(512)  NOT NULL                                          COMMENT '도서 제목',
    author               VARCHAR(512)  NULL                                              COMMENT '저자/아티스트 정보',
    isbn13               VARCHAR(13)   NOT NULL                                          COMMENT 'ISBN13 (숫자 13자리, 하이픈 제거)',
    isbn10               VARCHAR(10)   NULL                                              COMMENT 'ISBN10',
    description          TEXT          NULL                                              COMMENT '상품 설명 (요약)',
    publisher            VARCHAR(255)  NULL                                              COMMENT '출판사',
    pub_date             DATE          NULL                                              COMMENT '출간일',
    cover_url            VARCHAR(1024) NULL                                              COMMENT '표지 이미지 URL',
    stock_status         VARCHAR(50)   NULL                                              COMMENT '재고 상태 (품절, 절판 등)',
    category_name        VARCHAR(255)  NULL                                              COMMENT '분야명',
    category_id          INT           NULL                                              COMMENT '알라딘 카테고리 ID',
    price_standard       INT           NOT NULL DEFAULT 0                                COMMENT '정가',
    price_sales          INT           NOT NULL DEFAULT 0                                COMMENT '판매가',
    customer_review_rank INT           NOT NULL DEFAULT 0                                COMMENT '회원 리뷰 평점 (0~10)',
    series_id            INT           NULL                                              COMMENT '시리즈 ID',
    series_name          VARCHAR(255)  NULL                                              COMMENT '시리즈 이름',
    mall_type            VARCHAR(20)   NOT NULL DEFAULT 'BOOK'                           COMMENT '상품 타입 (BOOK, MUSIC 등)',
    adult_yn             VARCHAR(1)    NOT NULL DEFAULT 'N'                              COMMENT '성인 등급 여부 (Y/N)',
    is_deleted           BOOLEAN       NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 여부',
    created_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at           DATETIME      NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_id),
    UNIQUE INDEX uq_book_isbn13 (isbn13),
    INDEX idx_book_title (title)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '도서 테이블';


-- ============================================================
-- 4. book_hold (도서 보유 테이블)
-- ============================================================
CREATE TABLE book_hold
(
    book_hold_id BIGINT      AUTO_INCREMENT                                    COMMENT '도서 보유 ID',
    book_id      BIGINT      NOT NULL                                          COMMENT '도서 ID',
    status       VARCHAR(50) NOT NULL                                          COMMENT '보유 상태 (AVAILABLE, BORROWED, RESERVE_HOLD, LOST, DISCARDED)',
    location     VARCHAR(50) NULL                                              COMMENT '도서 위치',
    is_deleted   BOOLEAN     NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 여부',
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at   DATETIME    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_hold_id),
    CONSTRAINT fk_book_hold_book FOREIGN KEY (book_id) REFERENCES book (book_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '도서 보유 테이블';


-- ============================================================
-- 5. reservation (예약 테이블)
-- ============================================================
CREATE TABLE reservation
(
    reservation_id BIGINT      AUTO_INCREMENT                                    COMMENT '예약 ID',
    book_hold_id   BIGINT      NOT NULL                                          COMMENT '도서 보유 ID',
    user_id        BIGINT      NOT NULL                                          COMMENT '사용자 ID',
    status         VARCHAR(50) NOT NULL                                          COMMENT '예약 상태 (WAITING, CONFIRMED, CANCELLED 등)',
    reserved_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '예약일시',
    expire_at      DATETIME    NULL                                              COMMENT '예약 만료일시',
    is_deleted     BOOLEAN     NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 여부',
    created_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at     DATETIME    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (reservation_id),
    CONSTRAINT fk_reservation_book_hold FOREIGN KEY (book_hold_id) REFERENCES book_hold (book_hold_id),
    CONSTRAINT fk_reservation_user      FOREIGN KEY (user_id)      REFERENCES users (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '예약 테이블';


-- ============================================================
-- 6. book_borrow (도서 대출 테이블)
-- ============================================================
CREATE TABLE book_borrow
(
    book_borrow_id BIGINT       AUTO_INCREMENT                                    COMMENT '대출 ID',
    book_hold_id   BIGINT       NOT NULL                                          COMMENT '도서 보유 ID',
    book_id        BIGINT       NOT NULL                                          COMMENT '도서 ID',
    user_id        BIGINT       NOT NULL                                          COMMENT '사용자 ID',
    reason         VARCHAR(512) NULL                                              COMMENT '대출 사유',
    status         VARCHAR(50)  NOT NULL                                          COMMENT '대출 상태 (BORROWED, RETURNED, OVERDUE 등)',
    borrow_date    DATETIME     NULL                                              COMMENT '대출일시',
    due_date       DATETIME     NULL                                              COMMENT '반납 예정일시',
    return_date    DATETIME     NULL                                              COMMENT '실제 반납일시',
    is_deleted     BOOLEAN      NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 여부',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at     DATETIME     NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (book_borrow_id),
    CONSTRAINT fk_book_borrow_book_hold FOREIGN KEY (book_hold_id) REFERENCES book_hold (book_hold_id),
    CONSTRAINT fk_book_borrow_book      FOREIGN KEY (book_id)      REFERENCES book (book_id),
    CONSTRAINT fk_book_borrow_user      FOREIGN KEY (user_id)      REFERENCES users (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '도서 대출 테이블';


-- ============================================================
-- 7. book_request (도서 희망 신청 테이블)
-- ============================================================
CREATE TABLE book_request
(
    book_request_id BIGINT       AUTO_INCREMENT                                    COMMENT '희망 신청 ID',
    user_id         BIGINT       NOT NULL                                          COMMENT '신청자 ID',
    title           VARCHAR(512) NOT NULL                                          COMMENT '도서 제목',
    authors         VARCHAR(255) NULL                                              COMMENT '저자',
    publisher       VARCHAR(255) NULL                                              COMMENT '출판사',
    isbn            VARCHAR(50)  NULL                                              COMMENT 'ISBN',
    reason          VARCHAR(512) NULL                                              COMMENT '신청 사유',
    status          VARCHAR(20)  NOT NULL                                          COMMENT '신청 상태 (PENDING, APPROVED, REJECTED)',
    reject_reason   VARCHAR(512) NULL                                              COMMENT '거절 사유',
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE                            COMMENT '소프트 삭제 여부',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                COMMENT '생성일시',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                         ON UPDATE CURRENT_TIMESTAMP              COMMENT '수정일시',
    PRIMARY KEY (book_request_id),
    CONSTRAINT fk_book_request_user FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '도서 희망 신청 테이블';


-- ============================================================
-- 8. notification (알림 테이블)
-- ============================================================
CREATE TABLE notification
(
    notification_id BIGINT       AUTO_INCREMENT COMMENT '알림 ID',
    user_id         BIGINT       NOT NULL       COMMENT '수신자 ID',
    type            VARCHAR(50)  NOT NULL       COMMENT '알림 유형',
    message         VARCHAR(512) NOT NULL       COMMENT '알림 내용',
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '읽음 여부',
    related_id      BIGINT       NULL           COMMENT '관련 엔티티 ID',
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부',
    created_at      DATETIME(6)  NULL           COMMENT '생성일시',
    updated_at      DATETIME(6)  NULL           COMMENT '수정일시',
    PRIMARY KEY (notification_id),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '알림 테이블';
