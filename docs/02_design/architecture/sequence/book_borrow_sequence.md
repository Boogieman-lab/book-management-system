# 📖 도서 검색, 대출 및 반납 시퀀스 다이어그램 (Borrow & Return Sequence)

재고(Hold)가 남아 있는 책을 조회한 뒤, 선점하여 빌리고 시간이 경과 한 뒤 반납하여 다음 차순위자에게 스위칭 시켜주는 핵심 도서 라이프 사이클입니다.

---

## 1. 도서 검색 및 상세 조회

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database (QueryDSL)

    User->>Client: 도서 검색 (제목/저자/출판사/ISBN)
    Client->>Server: GET /api/v1/books?q=자바&page=0&size=20
    Server->>DB: QueryDSL 동적 쿼리<br>(TITLE LIKE 또는 AUTHOR LIKE 또는 ISBN LIKE)
    DB-->>Server: Book 목록 + BOOK_HOLD 재고 수량 계산
    Server-->>Client: HTTP 200 { content: [...], totalElements: 100 }
    Client-->>User: 검색 결과 카드 그리드 표시

    User->>Client: 특정 도서 클릭
    Client->>Server: GET /api/v1/books/{bookId}
    Server->>DB: SELECT * FROM book WHERE book_id = ?
    DB-->>Server: Book 정보 + 현재 대출 가능 BOOK_HOLD 갯수
    Server-->>Client: HTTP 200 { id, title, author, ..., availableCount }
    Client-->>User: 도서 상세 정보 표시<br>availableCount > 0 이면 [대출하기] 버튼<br>else [예약하기] 버튼
```

---

## 2. 도서 대출 (`POST /api/v1/borrows`)

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database (Pessimistic Lock)
    participant Event as EventPublisher

    User->>Client: [대출하기] 버튼 클릭
    Client->>Server: POST /api/v1/borrows { bookHoldId }

    Server->>Server: SecurityContext에서 userId 추출

    Server->>DB: SELECT * FROM users WHERE user_id = ?<br>(연체 여부, 현재 대출 권수 확인)
    DB-->>Server: 사용자 정보 + login_fail_count, is_locked, overdue_records

    alt 계정 잠금 상태
        Server-->>Client: HTTP 403 (ACCOUNT_LOCKED)
    else 대출 제한 중 (연체 중)
        Server-->>Client: HTTP 409 (OVERDUE_RESTRICTION)
    else 현재 대출 10권 이상
        Server-->>Client: HTTP 409 (BORROW_LIMIT_EXCEEDED)
    else 정상 상태
        Server->>DB: SELECT * FROM book_hold WHERE book_hold_id = ?<br>WITH PESSIMISTIC_WRITE (동시성 제어)
        DB-->>Server: BookHold 레코드 락 획득

        alt 재고 없음 (status != AVAILABLE)
            DB->>Server: 락 해제
            Server-->>Client: HTTP 409 (NOT_AVAILABLE)
        else 재고 있음
            Server->>Server: due_date = now() + 14days (대출 기간)
            Server->>DB: INSERT INTO book_borrow<br>(book_hold_id, user_id, borrow_date, due_date, status='ACTIVE')
            DB-->>Server: borrow_id 반환

            Server->>DB: UPDATE book_hold SET status='BORROWED' WHERE book_hold_id = ?
            DB-->>Server: 업데이트 완료

            DB->>Server: 락 해제 (COMMIT)

            Server->>Event: publish(BorrowCreatedEvent)<br>기존 예약자가 있는 경우 처리

            Server-->>Client: HTTP 201 CREATED { borrowId, dueDate }
            Client-->>User: "대출 완료! 반납 예정일: {dueDate}" 알림
        end
    end
```

---

## 3. 도서 반납 (`POST /api/v1/borrows/{borrowId}/return`)

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database
    participant Event as EventPublisher

    User->>Client: 마이페이지 → 대출 중인 도서 [반납하기] 클릭
    Client->>Server: POST /api/v1/borrows/{borrowId}/return

    Server->>Server: SecurityContext에서 userId 추출

    Server->>DB: SELECT * FROM book_borrow WHERE borrow_id = ?
    DB-->>Server: 대출 기록 반환

    alt borrowId의 user_id ≠ 현재 userId
        Server-->>Client: HTTP 403 (FORBIDDEN - IDOR 방어)
    else 본인 대출 기록
        Server->>Server: now() > due_date 인지 확인 (연체 여부)

        alt 연체된 도서
            Server->>DB: overdue_days = now() - due_date
            Server->>DB: INSERT INTO overdue_record<br>(user_id, book_borrow_id, overdue_days, restriction_days, restriction_until)
            DB-->>Server: 기록 저장
        end

        Server->>DB: UPDATE book_borrow SET return_date=now(), status='RETURNED'
        DB-->>Server: 완료

        Server->>DB: SELECT * FROM book_hold WHERE book_hold_id = ?
        DB-->>Server: BookHold 정보

        Server->>DB: SELECT * FROM reservation<br>WHERE book_id = ? AND status != 'EXPIRED' AND status != 'CANCELLED'<br>ORDER BY reservation_order ASC LIMIT 1
        DB-->>Server: 다음 예약자 정보 (있으면 반환, 없으면 null)

        alt 대기 중인 예약자가 있음
            Server->>DB: UPDATE book_hold SET status='RESERVE_HOLD'
            DB-->>Server: 예약 대기 상태로 전환

            Server->>DB: UPDATE reservation<br>SET status='NOTIFIED', notified_at=now()<br>WHERE reservation_id = ?
            DB-->>Server: 예약자 알림 상태로 변경

            Server->>Event: publish(ReservationArrivedEvent)<br>예약자에게 도서 도착 알림 발송 (비동기)
        else 예약자 없음
            Server->>DB: UPDATE book_hold SET status='AVAILABLE'
            DB-->>Server: 대출 가능 상태로 복구
        end

        Server-->>Client: HTTP 200 OK { status: 'RETURNED' }
        Client-->>User: "반납 완료되었습니다." 알림
    end
```

---

## 4. 연체 및 대출 제한 처리

```mermaid
sequenceDiagram
    participant Scheduler as Spring @Scheduled
    participant Server as Backend API
    participant DB as Database
    participant Event as EventPublisher

    Scheduler->>Server: 매일 자정 실행<br>배치 작업: 연체 여부 확인

    Server->>DB: SELECT * FROM book_borrow<br>WHERE status='ACTIVE' AND due_date < now() AND is_deleted=false

    DB-->>Server: 연체된 대출 목록 반환

    loop 각 연체 건마다
        Server->>DB: overdue_days 계산
        Server->>DB: UPDATE overdue_record SET restriction_until = now() + overdue_days
        DB-->>Server: 대출 제한 기간 갱신

        Server->>Event: publish(OverdueNoticeEvent)
        Server->>Server: 사용자에게 연체 알림 (이메일/SMS)
    end

    Note over Server,DB: 이후 대출 시도 시<br>현재 user의 모든 overdue_record 중<br>max(restriction_until) 까지 대출 금지
```

---

## 5. 상태 다이어그램

### BOOK_HOLD 상태 전이

```
AVAILABLE (대출 가능)
    ↓ POST /api/v1/borrows
BORROWED (대출 중)
    ↓ POST /api/v1/borrows/{id}/return
    ├─→ AVAILABLE (예약자 없음)
    └─→ RESERVE_HOLD (예약자 있음)
        ↓ (4일 경과 또는 사용자 수령)
        ├─→ AVAILABLE
        └─→ BORROWED
```

### BOOK_BORROW 상태

```
ACTIVE (대출 직후)
    ↓ (기한 내 반납)
RETURNED
    또는 (기한 경과)
OVERDUE (배치 작업으로 자동 상태 추적)
```

---

## 동시성 제어 (Pessimistic Lock)

마지막 1권에 대한 동시 대출 요청 시:

```
Time: 10:00:00.000
Thread A: SELECT * FROM book_hold WHERE book_hold_id = 1 WITH PESSIMISTIC_WRITE
  └─ Lock 획득 ✓
  └─ status = 'AVAILABLE' 확인 ✓
  └─ INSERT book_borrow (Thread A)
  └─ UPDATE book_hold SET status='BORROWED'
  └─ COMMIT ✓ (Lock 해제)

Thread B: SELECT * FROM book_hold WHERE book_hold_id = 1 WITH PESSIMISTIC_WRITE
  └─ Lock 대기... (Thread A가 보유 중)
  └─ Lock 획득 ✓
  └─ status = 'BORROWED' 확인 ✗
  └─ HTTP 409 CONFLICT 반환
```

**결과**: 1권은 1명에게만 할당, 나머지는 HTTP 409 거절

