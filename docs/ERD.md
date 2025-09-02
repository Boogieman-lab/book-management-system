# 📘 부기맨 도서관리시스템 ERD

```mermaid
erDiagram
    USERS {
        BIGINT user_id PK "사용자 ID"
        VARCHAR(255) email "사용자 이메일 (UNIQUE, NOT NULL)"
        VARCHAR(255) password "비밀번호"
        VARCHAR(50)  name "사용자 이름 (NOT NULL)"
        BIGINT       department_id "부서 ID"
        VARCHAR(500) profile_image "프로필 이미지 URL"
        VARCHAR(20)  role "권한 (NOT NULL)"
        INT          login_fail_count "로그인 실패 횟수"
        DATETIME     created_at "생성일시"
        DATETIME     updated_at "수정일시"
    }

    DEPARTMENT {
        BIGINT    department_id PK "부서 ID"
        VARCHAR(50) name "부서명"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
    }

    BOOK {
        BIGINT       book_id PK "도서 ID"
        VARCHAR(512) title "도서명 (NOT NULL)"
        VARCHAR(512) authors "저자"
        VARCHAR(255) publisher "출판사"
        VARCHAR(50)  isbn "국제 표준 도서번호"
        DATE         pub_date "출간일"
        INT          page_count "페이지 수"
        VARCHAR(50)  genre "장르"
        VARCHAR(512) image_url "표지 이미지"
        DATETIME     created_at "생성일시"
        DATETIME     updated_at "수정일시"
    }

    BOOK_HOLD {
        BIGINT      book_hold_id PK "도서 보유 ID"
        BIGINT      book_id "도서 ID (NOT NULL)"
        VARCHAR(50) status "보유 상태 (NOT NULL)"
        VARCHAR(50) location "위치"
        DATETIME    created_at "생성일시"
        DATETIME    updated_at "수정일시"
    }

    RESERVATION {
        BIGINT      reservation_id PK "예약 ID"
        BIGINT      book_hold_id "도서 보유 ID (NOT NULL)"
        BIGINT      user_id "사용자 ID (NOT NULL)"
        VARCHAR(50) status "예약 상태 (NOT NULL)"
        DATETIME    reserved_at "예약일시"
        DATETIME    created_at "생성일시"
        DATETIME    updated_at "수정일시"
    }

    BOOK_BORROW {
        BIGINT      book_borrow_id PK "대출 ID"
        BIGINT      book_hold_id "도서 보유 ID (NOT NULL)"
        BIGINT      book_id "도서 ID (NOT NULL)"
        BIGINT      user_id "사용자 ID (NOT NULL)"
        VARCHAR(512) reason "대출 사유"
        VARCHAR(50) status "대출 상태 (NOT NULL)"
        DATETIME    created_at "생성일시"
        DATETIME    updated_at "수정일시"
    }

    BOOK_REQUEST {
        BIGINT      book_request_id PK "도서 희망 ID"
        BIGINT      user_id "사용자 ID (NOT NULL)"
        BIGINT      book_id "도서 ID (NOT NULL)"
        VARCHAR(512) reason "신청 사유"
        DATETIME    created_at "생성일시"
        DATETIME    updated_at "수정일시"
    }

    PENALTY {
        BIGINT      penalty_id PK "패널티 ID"
        BIGINT      user_id "사용자 ID (NOT NULL)"
        BIGINT      borrow_id "대출 ID (NOT NULL)"
        INT         amount "금액"
        VARCHAR(50) status "패널티 상태 (NOT NULL)"
        DATETIME    paid_at "납부일시"
        DATETIME    created_at "생성일시"
        DATETIME    updated_at "수정일시"
    }

    %% 관계(논리적 FK)
    
    USERS ||--o{ RESERVATION : "예약"
    USERS ||--o{ PENALTY : "대상자"
    USERS ||--o{ BOOK_REQUEST : "희망 신청"
    USERS ||--o{ BOOK_BORROW : "대출자"
    
    BOOK ||--o{ BOOK_HOLD : "보유"
    BOOK ||--o{ BOOK_BORROW : "원본 도서"
    BOOK  ||--o{ BOOK_REQUEST : "희망 도서"

    DEPARTMENT ||--o{ USERS : "소속"
    
    BOOK_HOLD ||--o{ RESERVATION : "예약 대상"
    BOOK_HOLD ||--o{ BOOK_BORROW : "대출 대상"

    BOOK_BORROW ||--o{ PENALTY : "연체/분실 등"
