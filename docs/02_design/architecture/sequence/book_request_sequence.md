# 📝 희망도서 신청 라이프사이클 시퀀스 다이어그램 (Book Request Lifecycle)

회사 내부에 보유되지 않은 도서를 일반 사원(User)이 관리자에게 요청하고, 관리자(Admin)가 이를 승인하여 시스템에 도서를 추가하는 전체 프로세스입니다.

---

## 1. 사용자의 희망도서 신청 단계

```mermaid
sequenceDiagram
    actor User as 사용자 (User)
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database

    User->>Client: 희망도서 신청 페이지 진입
    Client->>Server: GET /api/v1/external/aladin/books?query=자바&target=title
    Server-->>Client: HTTP 200 { documents: [...] }
    Client-->>User: 알라딘 검색 결과 모달 표시

    User->>Client: 원하는 도서 선택
    Client-->>Client: 도서 정보 폼에 자동 채우기<br>(title, author, isbn13, cover_url)

    User->>Client: 신청 사유 입력 후 [신청] 버튼 클릭
    Client->>Server: POST /api/v1/book-requests { title, author, isbn13, reason }

    Server->>DB: SELECT * FROM book WHERE isbn13 = ?<br>(기존 보유 도서 확인)
    alt 기존 보유 도서 존재
        DB-->>Server: 도서 정보 반환
        Server-->>Client: HTTP 409 (DUPLICATE_BOOK)<br>"이미 보유 중인 도서입니다"
        Client-->>User: 에러 알림 표시
    else 기존 신청 건 확인
        Server->>DB: SELECT * FROM book_request<br>WHERE isbn13 = ? AND status != 'REJECTED'<br>(진행 중인 신청 확인)
        alt 진행 중인 신청 존재
            DB-->>Server: 신청 정보 반환
            Server-->>Client: HTTP 409 (PENDING_REQUEST)<br>"이미 신청 대기 중입니다"
            Client-->>User: 에러 알림 표시
        else 신규 신청
            Server->>DB: INSERT INTO book_request<br>(user_id, title, author, isbn13, reason, status='PENDING')
            DB-->>Server: book_request_id 반환
            Server-->>Client: HTTP 201 CREATED { requestId, status: 'PENDING' }
            Client-->>User: "신청이 접수되었습니다" 성공 알림
        end
    end
```

---

## 2. 신청 현황 조회

```mermaid
sequenceDiagram
    actor User as 사용자 (User)
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database

    User->>Client: 희망도서 → 내 신청 내역 탭 클릭
    Client->>Server: GET /api/v1/book-requests

    Server->>Server: SecurityContext에서 userId 추출
    Server->>DB: SELECT * FROM book_request<br>WHERE user_id = ? AND is_deleted = false<br>ORDER BY created_at DESC

    DB-->>Server: 신청 목록 (PENDING, APPROVED, REJECTED 혼합)

    Server-->>Client: HTTP 200 { content: [...], totalElements: 5 }

    Client-->>User: 신청 리스트 표시<br>상태별 배지: PENDING(파란색), APPROVED(초록색), REJECTED(빨간색)<br>각 신청의 신청일, 상태, 거절 사유(있으면) 표시
```

---

## 3. 관리자의 신청 승인/거절 단계

```mermaid
sequenceDiagram
    actor Admin as 관리자 (Admin)
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database
    participant Event as EventPublisher

    Admin->>Client: 관리자 → 결재함 페이지 진입
    Client->>Server: GET /api/v1/book-requests?status=PENDING&page=0&size=20

    Server->>Server: JWT Token에서 ROLE_ADMIN 검증
    Server->>DB: SELECT * FROM book_request<br>WHERE status = 'PENDING'<br>ORDER BY created_at ASC

    DB-->>Server: 대기 중인 신청 목록
    Server-->>Client: HTTP 200 { content: [...], totalElements: 12 }
    Client-->>Admin: 신청 리스트 테이블 표시

    Admin->>Client: 특정 신청 건 [승인] 또는 [거절] 버튼 클릭

    Client->>Server: PATCH /api/v1/admin/book-requests/{requestId}/status<br>{ status: 'APPROVED' | 'REJECTED', reason: '...' }

    Server->>DB: SELECT * FROM book_request WHERE request_id = ?
    DB-->>Server: 신청 정보 반환

    alt 이미 처리된 신청
        Server-->>Client: HTTP 409 (ALREADY_PROCESSED)
    else 미처리 신청
        Server->>DB: UPDATE book_request SET status='APPROVED', updated_at=now()<br>WHERE request_id = ?
        DB-->>Server: 업데이트 완료

        Server->>Event: publish(BookRequestApprovedEvent)<br>{ userId, requestId, title }

        Server-->>Client: HTTP 200 OK { status: 'APPROVED' }
        Client-->>Admin: "승인 처리되었습니다" 완료 알림

        Event->>DB: 비동기) INSERT INTO notification<br>(user_id, type='BOOK_REQUEST_APPROVED', message='...', is_read=false)
        DB-->>Event: 알림 저장 완료

        Event-->>User: (향후) SSE로 실시간 알림 전송
    end
```

---

## 4. 거절 처리

```mermaid
sequenceDiagram
    actor Admin as 관리자 (Admin)
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database
    participant Event as EventPublisher

    Admin->>Client: [거절] 버튼 클릭 후 사유 입력

    Client->>Server: PATCH /api/v1/admin/book-requests/{requestId}/status<br>{ status: 'REJECTED', reason: '품절' }

    Server->>DB: UPDATE book_request<br>SET status='REJECTED', rejection_reason='품절', updated_at=now()
    DB-->>Server: 업데이트 완료

    Server->>Event: publish(BookRequestRejectedEvent)<br>{ userId, requestId, reason }

    Server-->>Client: HTTP 200 OK { status: 'REJECTED' }

    Event->>DB: 비동기) INSERT INTO notification<br>(user_id, type='BOOK_REQUEST_REJECTED', message='희망도서 신청이 거절되었습니다. 사유: 품절')
    DB-->>Event: 알림 저장 완료

    Event-->>User: (향후) SSE로 실시간 알림 전송
```

---

## 5. 도서 정식 등록 (승인 후 입고)

```mermaid
sequenceDiagram
    actor Admin as 관리자 (Admin)
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database
    participant Aladin as Aladin API (선택)

    Admin->>Client: 관리자 → 도서 센터 페이지
    Client->>Server: GET /api/v1/external/aladin/books?query=자바&target=title

    Server->>Aladin: 알라딘 API 호출
    Aladin-->>Server: 도서 정보 반환
    Server-->>Client: HTTP 200 { documents: [...] }

    Client-->>Admin: 검색 결과 표시

    Admin->>Client: 도서 선택 후 [등록] 버튼
    Client->>Server: POST /api/v1/admin/books<br>{ isbn13, title, author, publisher, coverUrl, categoryId, categoryName, ... }

    Server->>DB: SELECT * FROM book WHERE isbn13 = ?
    DB-->>Server: 이미 존재 여부 확인

    alt 이미 등록된 도서
        Server-->>Client: HTTP 409 (DUPLICATE_ISBN)
    else 신규 도서
        Server->>DB: INSERT INTO book<br>(isbn13, title, author, ..., created_at)
        DB-->>Server: book_id 반환

        Server->>DB: INSERT INTO book_hold<br>(book_id, status='AVAILABLE', created_at)
        DB-->>Server: 초기 재고(1권) 추가

        Server->>DB: SELECT * FROM book_request<br>WHERE isbn13 = ? AND status='APPROVED'
        DB-->>Server: 관련 신청 건 조회

        alt 연계된 승인 신청 존재
            Server->>DB: UPDATE book_request SET status='ARRIVED', updated_at=now()<br>WHERE isbn13 = ? AND status='APPROVED'
            DB-->>Server: 상태 갱신 완료

            Server->>Event: publish(BookRequestArrivedEvent)<br>{ userId, requestId, bookId, title }
            Note over Event: 비동기 알림: "신청하신 도서가 입고되었습니다. 대출 가능합니다"
        end

        Server-->>Client: HTTP 201 CREATED { bookId, isbn13, title }
        Client-->>Admin: "도서가 등록되었습니다" 완료 알림
    end
```

---

## 상태 다이어그램

### BOOK_REQUEST 상태 전이

```
신청 (PENDING)
   ↓
[관리자 결재]
   ├─→ 승인 (APPROVED) [알림: BOOK_REQUEST_APPROVED]
   │      ↓
   │   [관리자 도서 등록 — POST /admin/books]
   │      ↓
   │   입고 완료 (ARRIVED) [알림: BOOK_REQUEST_ARRIVED]
   │      ↓
   │   사용자 대출 가능
   │
   └─→ 거절 (REJECTED) [이력 보관, 사유 저장, 알림: BOOK_REQUEST_REJECTED]
```

### 전체 라이프사이클

```
1. 사용자 신청 (PENDING)
   ↓
2. 관리자 승인 (APPROVED) [알림 발송: BOOK_REQUEST_APPROVED]
   ↓
3. 실제 도서 구입 후 도서 센터에서 등록 (POST /admin/books)
   ↓
4. Book + BookHold 엔티티 생성
   ↓
5. book_request 상태 → ARRIVED 갱신 [알림 발송: BOOK_REQUEST_ARRIVED]
   ↓
6. 사용자 대출 가능
```

---

## API 응답 예시

### 신청 목록 조회 (사용자)

```json
{
  "content": [
    {
      "id": 1,
      "title": "자바의 정석",
      "author": "남궁성",
      "isbn13": "9788994492032",
      "reason": "Java 입문서로 필독서 같습니다",
      "status": "APPROVED",
      "rejectionReason": null,
      "createdAt": "2026-03-20T10:30:00Z",
      "updatedAt": "2026-03-21T15:45:00Z"
    },
    {
      "id": 2,
      "title": "클린 코드",
      "author": "로버트 마틴",
      "isbn13": "9788966261031",
      "reason": "코드 품질 개선을 위해",
      "status": "REJECTED",
      "rejectionReason": "품절",
      "createdAt": "2026-03-15T09:00:00Z",
      "updatedAt": "2026-03-18T14:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2
}
```

### 신청 목록 조회 (관리자)

```json
{
  "content": [
    {
      "id": 1,
      "userId": 5,
      "userName": "김철수",
      "title": "자바의 정석",
      "author": "남궁성",
      "isbn13": "9788994492032",
      "reason": "Java 입문서로 필독서 같습니다",
      "status": "PENDING",
      "createdAt": "2026-03-20T10:30:00Z"
    },
    {
      "id": 3,
      "userId": 7,
      "userName": "이영희",
      "title": "스프링 인액션",
      "author": "크레이그 월스",
      "isbn13": "9788960776456",
      "reason": "Spring Framework 학습",
      "status": "PENDING",
      "createdAt": "2026-03-19T14:15:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2
}
```

