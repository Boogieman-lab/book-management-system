# 📘 부기맨 도서관리시스템 ERD

```mermaid
erDiagram
    DEPARTMENT {
        BIGINT department_id PK "부서 고유 ID (Primary Key)"
        VARCHAR name "부서명"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
    }

    USER {
        BIGINT user_id PK "사용자 고유 ID (Primary Key)"
        VARCHAR email "사용자 이메일 (로그인 ID, UNIQUE)"
        VARCHAR password "비밀번호 (OAuth 시 NULL 가능)"
        VARCHAR name "사용자 이름"
        BIGINT department_id FK "부서 ID (Foreign Key)"
        VARCHAR profile_image "프로필 이미지 URL"
        VARCHAR role "권한 (USER / ADMIN)"
        INT login_fail_count "로그인 실패 횟수"
        DATETIME created_at "생성일시"
        DATETIME updated_at "수정일시"
    }

    BOOK {
        BIGINT book_id PK "도서 고유 ID (Primary Key)"
        VARCHAR title "도서명"
        VARCHAR author "저자"
        VARCHAR publisher "출판사"
        VARCHAR isbn "국제 표준 도서번호"
        DATE published_date "출간일"
        VARCHAR genre "장르"
        INT page_count "페이지수"
        VARCHAR language "언어"
        VARCHAR cover_image "도서 이미지 URL"
        DATETIME created_at "등록일시"
        DATETIME updated_at "수정일시"
    }

    BOOK_ITEM {
        BIGINT item_id PK "도서 단위 고유 ID (Primary Key)"
        BIGINT book_id FK "도서 ID (Foreign Key)"
        VARCHAR status "상태 (대출 가능 / 대출 중 / 분실 / 폐기)"
        VARCHAR location "서가 위치"
        DATETIME created_at "등록일시"
        DATETIME updated_at "수정일시"
    }

    BOOK_REQUEST {
        BIGINT request_id PK "신청 고유 ID (Primary Key)"
        BIGINT user_id FK "신청자 ID (Foreign Key)"
        BIGINT admin_id FK "승인/거절 처리 관리자 ID (Foreign Key)"
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
        BIGINT item_id FK "대출한 도서 단위 ID (Foreign Key)"
        BIGINT user_id FK "대출자 ID (Foreign Key)"
        DATETIME borrow_date "대출일"
        DATETIME due_date "반납 예정일"
        DATETIME return_date "반납일"
        INT overdue_days "연체일 수"
        INT extension_count "연장 횟수"
        DATETIME final_due_date "최종 반납 예정일"
        VARCHAR status "상태 (대출 중 / 반납 / 연체)"
    }

    RESERVATION {
        BIGINT reservation_id PK "예약 고유 ID (Primary Key)"
        BIGINT item_id FK "예약 도서 단위 ID (Foreign Key)"
        BIGINT user_id FK "예약자 ID (Foreign Key)"
        DATETIME reserved_at "예약일시"
        VARCHAR status "예약 상태 (예약 중 / 완료 / 취소)"
    }

    PENALTY {
        BIGINT penalty_id PK "페널티 고유 ID (Primary Key)"
        BIGINT user_id FK "사용자 ID (Foreign Key)"
        BIGINT borrow_id FK "연체 대출 ID (Foreign Key)"
        INT amount "벌금 금액"
        VARCHAR reason "사유"
        DATETIME created_at "생성일시"
        DATETIME paid_at "납부일시"
        VARCHAR status "상태 (미납 / 납부)"
    }

    NOTIFICATION {
        BIGINT noti_id PK "알림 고유 ID (Primary Key)"
        BIGINT user_id FK "수신자 ID (Foreign Key)"
        BIGINT template_id FK "알림 템플릿 ID (Foreign Key)"
        VARCHAR type "알림 유형 (승인/거절/반납예정/연체 등)"
        TEXT message "알림 내용"
        BOOLEAN is_read "읽음 여부"
        DATETIME created_at "생성일"
        DATETIME read_at "읽음일시"
    }

    NOTIFICATION_TEMPLATE {
        BIGINT template_id PK "템플릿 고유 ID (Primary Key)"
        VARCHAR type "알림 유형"
        TEXT message_template "알림 내용 템플릿"
        DATETIME created_at "생성일"
        DATETIME updated_at "수정일"
    }

    HISTORY {
        BIGINT history_id PK "이력 고유 ID (Primary Key)"
        BIGINT user_id FK "사용자 ID (Foreign Key)"
        BIGINT item_id FK "도서 단위 ID (Foreign Key)"
        VARCHAR action "행위 (대출, 반납, 신청, 승인, 예약 취소 등)"
        DATETIME action_date "행위 일시"
        TEXT detail "상세 기록"
    }

    AUDIT_LOG {
        BIGINT audit_id PK "로그 고유 ID (Primary Key)"
        BIGINT admin_id FK "관리자 ID (Foreign Key)"
        VARCHAR action_type "행위 유형 (BOOK 등록/삭제, USER 권한 변경 등)"
        TEXT detail "상세 기록"
        DATETIME action_date "행위 일시"
    }

    %% 관계 정의
    DEPARTMENT ||--o{ USER : "소속"
    USER ||--o{ BOOK_REQUEST : "신청"
    USER ||--o{ BORROWING : "대출"
    USER ||--o{ NOTIFICATION : "알림 수신"
    USER ||--o{ HISTORY : "행위 기록"
    USER ||--o{ PENALTY : "벌금/페널티"
    USER ||--o{ RESERVATION : "예약"
    USER ||--o{ AUDIT_LOG : "관리자 작업 기록"

    BOOK ||--o{ BOOK_ITEM : "도서 단위"
    BOOK_ITEM ||--o{ BORROWING : "대출 대상"
    BOOK_ITEM ||--o{ HISTORY : "관련 기록"
    BOOK_ITEM ||--o{ RESERVATION : "예약 대상"

    BOOK_REQUEST }o--|| USER : "신청자"
    BOOK_REQUEST }o--|| USER : "처리 관리자"
    NOTIFICATION }o--|| NOTIFICATION_TEMPLATE : "템플릿 참조"
