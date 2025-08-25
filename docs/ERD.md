# 📘 부기맨 도서관리시스템 ERD

```mermaid
erDiagram
    USER {
        BIGINT user_id PK "사용자 고유 ID (Primary Key)"
        VARCHAR email "사용자 이메일 (로그인 ID)"
        VARCHAR password "비밀번호 (OAuth 시 NULL 가능)"
        VARCHAR name "사용자 이름"
        VARCHAR department "부서명"
        VARCHAR profile_image "프로필 이미지 URL"
        VARCHAR role "권한 (USER / ADMIN)"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
    }

    BOOK {
        BIGINT book_id PK "도서 고유 ID (Primary Key)"
        VARCHAR title "도서명"
        VARCHAR author "저자"
        VARCHAR publisher "출판사"
        VARCHAR isbn "국제 표준 도서번호"
        VARCHAR status "상태 (대출 가능 / 대출 중 / 분실 / 폐기)"
        DATETIME created_at "등록일시"
        DATETIME updated_at "수정일시"
    }

    BOOK_REQUEST {
        BIGINT request_id PK "신청 고유 ID (Primary Key)"
        BIGINT user_id FK "신청자 ID (Foreign Key)"
        VARCHAR title "신청 도서명"
        VARCHAR author "저자"
        VARCHAR publisher "출판사"
        VARCHAR isbn "ISBN"
        TEXT reason "신청 사유"
        VARCHAR status "신청 상태 (승인 대기 / 승인 / 거절)"
        DATETIME created_at "신청일"
        DATETIME updated_at "수정일"
    }

    BORROWING {
        BIGINT borrow_id PK "대출 고유 ID (Primary Key)"
        BIGINT book_id FK "대출한 도서 ID (Foreign Key)"
        BIGINT user_id FK "대출자 ID (Foreign Key)"
        DATETIME borrow_date "대출일"
        DATETIME due_date "반납 예정일"
        DATETIME return_date "반납일"
        VARCHAR status "상태 (대출 중 / 반납 / 연체)"
    }

    NOTIFICATION {
        BIGINT noti_id PK "알림 고유 ID (Primary Key)"
        BIGINT user_id FK "수신자 ID (Foreign Key)"
        VARCHAR type "알림 유형 (승인/거절/반납예정/연체 등)"
        TEXT message "알림 내용"
        BOOLEAN is_read "읽음 여부"
        DATETIME created_at "생성일"
    }

    HISTORY {
        BIGINT history_id PK "이력 고유 ID (Primary Key)"
        BIGINT user_id FK "사용자 ID (Foreign Key)"
        BIGINT book_id FK "도서 ID (Foreign Key)"
        VARCHAR action "행위 (대출, 반납, 신청, 승인 등)"
        DATETIME action_date "행위 일시"
        TEXT detail "상세 기록"
    }

    %% 관계 정의
    USER ||--o{ BOOK_REQUEST : "신청"
    USER ||--o{ BORROWING : "대출"
    USER ||--o{ NOTIFICATION : "알림 수신"
    USER ||--o{ HISTORY : "행위 기록"

    BOOK ||--o{ BORROWING : "대출 대상"
    BOOK ||--o{ HISTORY : "관련 기록"

    BOOK_REQUEST }o--|| USER : "신청자"
    BOOK_REQUEST }o--|| BOOK : "승인 시 등록"
