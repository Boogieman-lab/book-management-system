# 🔔 실시간 알림 시스템 시퀀스 (Redis Pub/Sub + SSE)

부기맨의 알림 시스템은 Redis Pub/Sub을 통한 백엔드 이벤트 발행과 Spring SSE(Server-Sent Events)를 통한 클라이언트 실시간 전달로 구성됩니다.

---

## 1. 이벤트 기반 알림 발행 (희망도서 승인 예시)

```mermaid
sequenceDiagram
    actor Admin as 관리자
    participant Client as Frontend
    participant Server as Backend API
    participant Redis as Redis Pub/Sub
    participant DB as Database
    participant Event as EventListener (@Async)

    Admin->>Client: 결재함에서 희망도서 [승인] 버튼 클릭
    Client->>Server: PATCH /api/v1/admin/book-requests/{requestId}/status<br>{ status: 'APPROVED' }

    Server->>DB: UPDATE book_request SET status='APPROVED'
    DB-->>Server: 완료

    Server->>Server: ApplicationEventPublisher.publishEvent(BookRequestApprovedEvent)
    Note over Server: 이벤트 발행 (논블로킹)

    Server-->>Client: HTTP 200 OK (즉시 응답)

    par 비동기 처리 (스레드 풀)
        Event->>Event: @EventListener(BookRequestApprovedEvent)<br>@Async 메서드 실행

        Event->>DB: INSERT INTO notification<br>(user_id, type='BORROW_APPROVED', message='...')
        DB-->>Event: 알림 저장

        Event->>Redis: PUBLISH channel:user:{userId} { type: 'BOOK_REQUEST_APPROVED', ... }
        Redis-->>Event: 발행 완료
    end
```

---

## 2. 클라이언트 SSE 구독 및 실시간 수신

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API (SSE Endpoint)
    participant Redis as Redis Pub/Sub
    participant RedisListener as Spring RedisMessageListenerContainer

    Note over Client,Server: 페이지 로드 또는 로그인 후

    Client->>Server: GET /api/v1/notifications/subscribe<br>(EventSource 연결)

    Server->>Server: SecurityContext에서 userId 확인
    Server->>Server: userId별 SSE 클라이언트 등록<br>(ConcurrentHashMap 관리)
    Server->>Redis: SUBSCRIBE channel:user:{userId}

    RedisListener->>Redis: Redis 메시지 리스너 대기 중

    Note over Server: 연결 유지 (하트비트 전송)
    loop 30초마다
        Server-->>Client: : heartbeat\n\n
    end

    par 알림 발생 시
        RedisListener->>Redis: 메시지 수신<br>channel:user:{userId}

        RedisListener->>Server: onMessage(message)

        Server->>Server: userId별 SSE 클라이언트 조회
        Server-->>Client: data: {...}\n\n

        Client-->>Client: EventSource onmessage 이벤트 발동<br>JSON 파싱 및 UI 업데이트
        Client-->>User: 실시간 알림 팝업 또는 뱃지 표시
    end

    Note over Client,Server: 클라이언트가 탭을 닫거나 연결 종료

    Client->>Server: 연결 끊김
    Server->>Server: userId별 SSE 클라이언트 제거
    Server->>Redis: UNSUBSCRIBE channel:user:{userId}
```

---

## 3. 알림 읽음 처리

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database

    User->>Client: 알림 팝업 또는 알림 함에서 알림 클릭
    Client->>Server: PATCH /api/v1/notifications/{notificationId}/read<br>{ isRead: true }

    Server->>Server: SecurityContext에서 userId 추출

    Server->>DB: SELECT * FROM notification WHERE notification_id = ?
    DB-->>Server: 알림 정보

    alt 다른 사용자의 알림 (IDOR 방어)
        Server-->>Client: HTTP 403 (FORBIDDEN)
    else 본인 알림
        Server->>DB: UPDATE notification<br>SET is_read=true, read_at=now()<br>WHERE notification_id = ?
        DB-->>Server: 완료

        Server-->>Client: HTTP 200 OK
        Client-->>User: 알림 아이콘에서 뱃지 제거
    end
```

---

## 4. 알림 목록 조회 및 미읽음 카운트

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API
    participant DB as Database

    User->>Client: 알림 함 아이콘 클릭 또는 마이페이지 알림 탭
    Client->>Server: GET /api/v1/notifications?page=0&size=20

    Server->>Server: SecurityContext에서 userId 추출

    Server->>DB: SELECT * FROM notification<br>WHERE user_id = ?<br>ORDER BY created_at DESC<br>LIMIT 20

    DB-->>Server: 알림 목록 (읽음/미읽음 혼합)

    Server->>Server: 미읽음 카운트 계산
    Server-->>Client: HTTP 200<br>{ content: [...], unreadCount: 3, totalElements: 15 }

    Client-->>Client: 알림 리스트 표시<br>미읽음 뱃지: unreadCount 표시
    Client-->>User: "알림 3개 읽지 않음" 배지 표시
```

---

## 5. 알림 발송 이벤트 종류

### 희망도서 신청 결과

```mermaid
sequenceDiagram
    participant Admin as 관리자
    participant Event as EventPublisher
    participant Redis as Redis
    participant DB as Database
    participant Client as 사용자 브라우저

    Admin->>Event: 희망도서 [승인]
    Event->>Redis: PUBLISH channel:user:5<br>{ type: 'BOOK_REQUEST_APPROVED',<br>title: '자바의 정석',<br>message: '승인되었습니다' }
    Event->>DB: INSERT notification

    Redis-->>Client: SSE 실시간 전달
    Client-->>Client: 팝업 알림: "자바의 정석이 승인되었습니다"
```

### 반납 예정일 알림

```mermaid
sequenceDiagram
    participant Scheduler as Spring @Scheduled (매일 오전 6시)
    participant Event as EventPublisher
    participant Redis as Redis
    participant DB as Database

    Scheduler->>DB: SELECT * FROM book_borrow<br>WHERE status='ACTIVE'<br>AND due_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY)

    DB-->>Scheduler: 내일 반납 예정인 도서 목록

    loop 각 도서마다
        Scheduler->>Event: publish(ReturnDueSoonEvent)
        Event->>Redis: PUBLISH channel:user:{userId}<br>{ type: 'RETURN_DUE_SOON',<br>title: '스프링 인액션',<br>dueDate: '2026-03-28' }
        Event->>DB: INSERT notification
    end
```

### 예약 도서 도착 알림

```mermaid
sequenceDiagram
    participant User as 사용자 A (반납)
    participant Server as Backend API
    participant Event as EventPublisher
    participant Redis as Redis
    participant DB as Database
    participant User2 as 사용자 B (예약자) 브라우저

    User->>Server: POST /api/v1/borrows/{id}/return (반납)

    Server->>DB: 1순위 예약자 확인
    DB-->>Server: user_id = 10

    Server->>Event: publish(ReservationArrivedEvent)

    Event->>Redis: PUBLISH channel:user:10<br>{ type: 'RESERVATION_ARRIVED',<br>title: '자바의 정석',<br>message: '예약 도서가 도착했습니다. 4일 이내 수령해주세요' }
    Event->>DB: INSERT notification

    Redis-->>User2: SSE 실시간 전달
    User2-->>User2: 팝업 알림: "자바의 정석이 도착했습니다"
```

---

## 6. SSE 연결 구현 상세

### Server-side (Spring Boot)

```java
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final SseEmitterMap emitterMap; // userId -> SseEmitter

    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        Long userId = SecurityUtils.getCurrentUserId();
        SseEmitter emitter = new SseEmitter(300000L); // 5분 타임아웃

        emitterMap.put(userId, emitter);

        try {
            emitter.send(SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name("connected")
                .data("Connection established"));
        } catch (IOException e) {
            emitterMap.remove(userId);
        }

        return emitter;
    }
}

@Component
public class RedisMessageListener {

    private final SseEmitterMap emitterMap;

    @EventListener
    public void onBookRequestApproved(BookRequestApprovedEvent event) {
        Long userId = event.getUserId();
        SseEmitter emitter = emitterMap.get(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name("notification")
                    .data(new NotificationDto(
                        "BORROW_APPROVED",
                        "희망도서 신청이 승인되었습니다",
                        event.getTitle()
                    )));
            } catch (IOException e) {
                emitterMap.remove(userId);
            }
        }
    }
}
```

### Client-side (JavaScript)

```javascript
function subscribeToNotifications() {
    const eventSource = new EventSource('/api/v1/notifications/subscribe');

    eventSource.addEventListener('connected', (e) => {
        console.log('SSE 연결 성공');
    });

    eventSource.addEventListener('notification', (e) => {
        const notification = JSON.parse(e.data);
        showNotificationPopup(notification);
        updateNotificationBadge();
    });

    eventSource.onerror = (e) => {
        console.error('SSE 연결 끊김', e);
        eventSource.close();
        // 재연결 로직
    };
}

function showNotificationPopup(notification) {
    const message = `${notification.type === 'BORROW_APPROVED'
        ? '승인됨'
        : notification.type === 'RESERVATION_ARRIVED'
        ? '도착함'
        : '알림'}: ${notification.message}`;

    // SweetAlert2 또는 Toast 알림
    Swal.fire({
        icon: 'success',
        title: '새 알림',
        text: message,
        timer: 5000
    });
}

function markAsRead(notificationId) {
    fetch(`/api/v1/notifications/${notificationId}/read`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ isRead: true })
    })
    .then(r => r.json())
    .then(() => updateNotificationBadge());
}
```

---

## 알림 타입 및 트리거 정의

| 타입 | 트리거 | 발행자 | 수신자 | 구현 상태 |
|------|--------|--------|--------|---------|
| `BOOK_REQUEST_APPROVED` | 희망도서 신청 승인 | EventListener | 신청 사용자 | ✅ 완료 (UI 전달 대기) |
| `BOOK_REQUEST_REJECTED` | 희망도서 신청 거절 | EventListener | 신청 사용자 | ✅ 완료 (UI 전달 대기) |
| `BOOK_REQUEST_ARRIVED` | 희망도서 실물 입고 완료 | EventListener | 신청 사용자 | ❌ 미구현 |
| `RESERVATION_ARRIVED` | 예약 도서 반납 (1순위) | EventListener | 예약 사용자 | ❌ 미구현 (EventListener 등록 필요) |
| `RETURN_DUE_SOON` | 반납 예정일 1일 전 | @Scheduled | 대출 사용자 | ❌ 미구현 (배치 작업 필요) |
| `OVERDUE_NOTICE` | 연체 발생 | EventListener | 연체 사용자 | ❌ 미구현 (배치 작업 필요) |

---

## 구현 로드맵

### Phase 1: 알림 저장 (완료 ✅)
- Notification 엔티티 및 리포지토리
- 알림 조회 및 읽음 처리 API

### Phase 2: Redis Pub/Sub 통합 (대기 중)
- RedisMessageListener 구현
- 이벤트별 채널 발행 로직

### Phase 3: SSE 연결 (대기 중)
- `/api/v1/notifications/subscribe` 엔드포인트
- SseEmitter 관리 및 브로드캐스트
- 클라이언트 EventSource 구현

### Phase 4: 배치 작업 (대기 중)
- 반납 예정일 1일 전 알림 (@Scheduled)
- 연체 알림 발송
- 4일 자동 승계 스케줄러 (기존 예약 로직)

