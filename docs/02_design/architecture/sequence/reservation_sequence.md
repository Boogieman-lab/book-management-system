# 📅 도서 예약 대기 및 4일 자동 승계 시퀀스 (Reservation Sequence)

회사 내 남은 수량이 없어서 모두 타인이 보관하고 있는 책(재고 0권)에 대하여 대기표를 먼저 선점하는 시퀀스와 그 만료 자동화 구조입니다.

---

## 1. 도서 예약 신청 프로세스

```mermaid
sequenceDiagram
    actor User as 일반 사원 사용자
    participant Client as Frontend 웹화면
    participant Server as Backend API 프로그램
    participant DB as 내부 Database

    User->>Client: 이미 전체 대출 나가 있는 인기 도서의 개별 화면에서 [도서 예약 신청] 버튼 액션
    Client->>Server: 도서 예약 할당 요청 `POST /api/v1/reservations`

    Server->>Server: SecurityContext에서 userId 추출

    Server->>DB: SELECT COUNT(*) FROM reservation<br>WHERE user_id = ? AND status IN ('WAITING', 'NOTIFIED', 'RESERVED')

    alt 사용자의 예약이 2개 이상
        Server-->>Client: HTTP 409 (RESERVATION_LIMIT_EXCEEDED)<br>"최대 2권까지 예약 가능합니다"
        Client-->>User: 에러 알림 표시
    else 예약 가능 상태
        Server->>DB: SELECT COUNT(*) FROM reservation<br>WHERE book_id = ? AND status NOT IN ('EXPIRED', 'CANCELLED')

        alt 해당 도서의 예약이 2명 이상
            Server-->>Client: HTTP 409 (BOOK_RESERVATION_FULL)<br>"더 이상 예약할 수 없습니다"
            Client-->>User: 에러 알림 표시
        else 예약 가능
            Server->>DB: SELECT COUNT(*) FROM book_hold<br>WHERE book_id = ? AND status = 'AVAILABLE'

            alt 대출 가능 재고 있음
                Server-->>Client: HTTP 409 (BOOK_AVAILABLE)<br>"현재 대출 가능한 도서입니다"
                Client-->>User: 에러 알림 (예약 불필요)
            else 재고 없음 (정상 예약 신청)
                Server->>DB: SELECT MAX(reservation_order) FROM reservation<br>WHERE book_id = ? AND status != 'EXPIRED'

                Server->>Server: next_order = (max_order ?? 0) + 1

                Server->>DB: INSERT INTO reservation<br>(book_id, user_id, reservation_order, status='WAITING')
                DB-->>Server: reservation_id 반환

                Server-->>Client: HTTP 201 CREATED<br>{ reservationId, order: 1 | 2, status: 'WAITING' }
                Client-->>User: "예약 신청이 접수되었습니다. 순위: {order}" 성공 알림
            end
        end
    end
```

---

## 2. 예약 취소

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database

    User->>Client: 마이페이지 → 예약 현황 탭에서 [취소] 버튼 클릭
    Client->>Server: DELETE /api/v1/reservations/{reservationId}

    Server->>Server: SecurityContext에서 userId 추출

    Server->>DB: SELECT * FROM reservation WHERE reservation_id = ?
    DB-->>Server: 예약 정보

    alt 다른 사용자의 예약 (IDOR 방어)
        Server-->>Client: HTTP 403 (FORBIDDEN)
    else 본인 예약
        alt 이미 취소됨 또는 만료됨
            Server-->>Client: HTTP 409 (ALREADY_CANCELLED)
        else 활성 예약
            Server->>DB: UPDATE reservation<br>SET status='CANCELLED', updated_at=now()
            DB-->>Server: 완료

            Note over Server,DB: 취소한 예약이 1순위(NOTIFIED)이고<br>book_hold가 RESERVE_HOLD 상태인 경우 즉시 승계

            Server->>DB: SELECT * FROM book_hold<br>WHERE book_id = ? AND status = 'RESERVE_HOLD'
            DB-->>Server: 보관 중인 도서 정보

            alt RESERVE_HOLD 도서 존재 (1순위가 NOTIFIED 상태였던 경우)
                Server->>DB: SELECT * FROM reservation<br>WHERE book_id = ? AND status = 'WAITING'<br>ORDER BY reservation_order ASC LIMIT 1
                DB-->>Server: 다음 대기자 (없으면 null)

                alt 다음 대기자 존재 (2순위 → 1순위 승계)
                    Server->>DB: UPDATE reservation<br>SET status='NOTIFIED', notified_at=now(),<br>reservation_order=1<br>WHERE reservation_id = ?
                    DB-->>Server: 승계 완료

                    Server->>Event: publish(ReservationArrivedEvent)<br>{ userId: 승계된_user_id }
                    Note over Event: 비동기 알림: "예약 도서가 도착했습니다. 4일 이내 수령해주세요"

                else 대기자 없음 → 도서 AVAILABLE 전환
                    Server->>DB: UPDATE book_hold SET status='AVAILABLE'
                    DB-->>Server: 완료
                end
            end

            Server-->>Client: HTTP 204 NO CONTENT
            Client-->>User: "예약이 취소되었습니다"
        end
    end
```

---

## 3. 4일 자동 승계 스케줄러 (배치 작업)

```mermaid
sequenceDiagram
    participant Scheduler as Spring @Scheduled (매일 자정)
    participant Server as Backend API
    participant DB as Database
    participant Event as EventPublisher

    Scheduler->>Server: 배치 작업 시작: 예약 만료 처리

    Server->>DB: SELECT * FROM reservation<br>WHERE status = 'NOTIFIED'<br>AND DATE_ADD(updated_at, INTERVAL 4 DAY) < NOW()

    DB-->>Server: 4일 이상 경과된 예약 목록

    loop 각 만료된 예약마다

        Server->>Server: 해당 reservation의 book_id 확인

        Server->>DB: SELECT * FROM book_hold<br>WHERE book_id = ? AND status = 'RESERVE_HOLD'
        DB-->>Server: 보관 중인 도서 정보

        Server->>DB: UPDATE reservation<br>SET status='EXPIRED', updated_at=now()<br>WHERE reservation_id = ?
        DB-->>Server: 1순위 예약 취소 완료

        Server->>DB: SELECT * FROM reservation<br>WHERE book_id = ? AND status = 'WAITING'<br>ORDER BY reservation_order ASC LIMIT 1
        DB-->>Server: 2순위 예약자 정보

        alt 2순위 예약자 존재
            Server->>DB: UPDATE reservation<br>SET status='NOTIFIED', notified_at=now(),<br>reservation_order=1<br>WHERE reservation_id = ?
            DB-->>Server: 2순위를 NOTIFIED(1순위)로 전환

            Note over Server,DB: book_hold는 여전히 RESERVE_HOLD 상태 유지<br>(4일 다시 카운트)

            Server->>Event: publish(ReservationEscalatedEvent)<br>{ userId: 2순위_user_id, bookId, ... }
            Event-->>User: (비동기) "예약 도서가 도착했습니다" 알림 발송

        else 예약자 없음
            Server->>DB: UPDATE book_hold<br>SET status='AVAILABLE'<br>WHERE book_id = ?
            DB-->>Server: 도서를 대출 가능 상태로 복구

            Note over Server,DB: book_hold 상태가 RESERVE_HOLD에서 AVAILABLE로 변경
        end

    end

    Scheduler-->>Server: 배치 작업 완료
```

---

## 4. 예약 현황 조회

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database

    User->>Client: 마이페이지 → 예약 현황 탭
    Client->>Server: GET /api/v1/users/me/reservations

    Server->>Server: SecurityContext에서 userId 추출

    Server->>DB: SELECT r.*, b.title, b.author, b.cover_url, bh.status<br>FROM reservation r<br>JOIN book b ON r.book_id = b.book_id<br>JOIN book_hold bh ON b.book_id = bh.book_id AND bh.status IN ('RESERVE_HOLD', 'BORROWED')<br>WHERE r.user_id = ? AND r.status NOT IN ('EXPIRED', 'CANCELLED')<br>ORDER BY r.reservation_order ASC

    DB-->>Server: 예약 목록 (WAITING 또는 NOTIFIED)

    Server-->>Client: HTTP 200 { content: [...] }

    Client-->>User: 예약 리스트 표시<br>- 도서명, 저자, 표지<br>- 예약 순위 (1순위 또는 2순위)<br>- 상태 배지 (대기중 / 도착)<br>- 만료일 (있으면 표시)<br>- 취소 버튼
```

---

## 상태 다이어그램

### RESERVATION 상태 전이

```
신청 (WAITING)
  ↓ (도서 반납 시)
알림 전송 (NOTIFIED)
  ├─→ (4일 경과)
  │   ├─→ 만료 (EXPIRED) [2순위 없음]
  │   └─→ 승계 (WAITING → NOTIFIED)
  │
  └─→ (사용자 수령)
      확정 (RESERVED)

또는 사용자 취소 → 취소됨 (CANCELLED)
```

### BOOK_HOLD 상태 전이 (예약 관점)

```
BORROWED (도서 대출 중)
  ↓ (반납)
RESERVE_HOLD (예약자가 있음)
  ├─→ (4일 경과)
  │   ├─→ AVAILABLE (2순위 예약자 없음)
  │   └─→ BORROWED (2순위 있음, 대출 진행)
  │
  └─→ (사용자가 4일 이내 수령)
      BORROWED (또는 AVAILABLE)
```

---

## 예약 순서 관리 (order 필드의 역할)

| 상황 | reservation_order | 상태 | 설명 |
|------|-------------------|------|------|
| 첫 번째 예약 | 1 | WAITING | 차순위 예약자 (즉시 도서 도착 시 수령 가능) |
| 두 번째 예약 | 2 | WAITING | 차차순위 (1순위 만료 후 승계) |
| 세 번째 예약 신청 | N/A | 거절 | 최대 2명까지만 허용 |

---

## 알림 발송 시점

| 이벤트 | 트리거 | 수신자 | 메시지 |
|--------|--------|--------|--------|
| 예약 신청 | POST /reservations | 사용자 | "예약이 접수되었습니다. 순위: 1순위" |
| 도서 도착 | POST /borrows/{id}/return (반납) | 1순위 예약자 | "예약 도서가 도착했습니다. 4일 이내 수령해주세요" |
| 4일 경과 후 승계 | @Scheduled (배치) | 2순위 예약자 | "예약 도서가 도착했습니다. 4일 이내 수령해주세요" |
| 예약 취소 | DELETE /reservations/{id} | 사용자 | "예약이 취소되었습니다" |

