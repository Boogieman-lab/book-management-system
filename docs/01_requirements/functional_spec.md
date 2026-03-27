# 📘 부기맨(Boogieman) 도서 관리 시스템 기능 명세서

## 개요

본 문서는 부기맨 MVP의 모든 기능을 상세히 명세하며, 각 기능별 입력/출력/동작, 예외 처리, 데이터베이스 힌트를 포함합니다.

---

## 기능별 명세

| 기능 구분 | 주요 기능명 | 기능 및 정책 상세 내용 (예외 상황 처리 및 보안/DB 룰) | 우선순위 |
| --- | --- | --- | --- |
| **인증(유저)** | 회원가입/로그인 | • 일반 이메일 인증 절차 ✅<br>• 카카오 OAuth 로그인 ✅ (기존 구현)<br>• [예외] 로그인 5회 실패 시 방어: `login_fail_count` 누적 후 계정 잠금 (`is_locked=true`) ✅<br>• [예외] 잠긴 계정 로그인 시도 시 HTTP 403 `ACCOUNT_LOCKED` 반환 ✅<br>• [예외] 이메일/비밀번호 불일치 시 HTTP 401 `INVALID_CREDENTIALS` 반환 (이메일 존재 여부 비노출) ✅<br>• **[보안]** JWT 기반 인증 도입 (Access/Refresh Token) ✅<br>• **[보안]** 로그아웃 시 Access Token Redis 블랙리스트(`bl:access:{jti}`) 등록 ✅<br>• **[보안]** Refresh Token Rotation: `/auth/refresh` 호출 시 Access Token + Refresh Token 동시 재발급, 이전 토큰 즉시 무효화 ✅<br>• **구현 경로**: `POST /api/auth/signup`, `POST /api/auth/login`, `POST /api/auth/logout`, `POST /api/auth/refresh` | **R1 (MVP 필수)** |
| | 인가(Authorization) 및 권한 | • **[NEW]** 권한 분리(RBAC): `/api/v1/admin/**` 경로는 JWT 내 `ROLE_ADMIN` 필수 검증<br>• 모든 API(조회 포함)는 JWT 유효성 검증<br>• `JwtAuthenticationFilter`: 토큰 서명 검증, JTI 존재 확인, 블랙리스트 대조 ✅<br>• `ErrorCode.FORBIDDEN(403)` 추가 ✅<br>• ⚠️ 현재 UI 개발 임시 정책으로 `/api/**` 전체 `permitAll()` 적용 중 — UI 개발 완료 후 Admin 경로 보호 복원 필요 | **R1 (MVP 필수)** |
| | 마이페이지 | • 정보 수정 및 대출/예약 이력 반환<br>• **[보안-IDOR]** 토큰에 담긴 `userId`와 요청 정보 매핑 철저 검증 (타인 이력 조회 불가)<br>• **구현 경로**: `GET /api/v1/users/me`, `PATCH /api/v1/users/me`, `GET /api/v1/users/me/borrows`, `GET /api/v1/users/me/requests`, `GET /api/v1/users/me/reservations` | **R1 (MVP 필수)** |
| **도서/검색** | 복합 검색/상세보기 | • 텍스트(제목, 저자, ISBN)를 활용한 플랫폼 도서 단위 식별 정보 획득<br>• 도서 상세 탭에서 현재 실재고(Hold) 권수 확인 제공<br>• **구현 경로**: `GET /api/v1/books`, `GET /api/v1/books/{bookId}`<br>• QueryDSL 동적 쿼리 (TITLE/AUTHOR/ISBN/PUBLISHER + 통합 검색), 페이지네이션, 가나다 정렬 | **R1 (MVP 필수)** |
| | 외부 도서 검색 (Aladin Proxy) | • **알라딘 ItemSearch API 연동**: 외부 도서 메타데이터(ISBN13, 고해상도 표지, 저자, 출판일, 분야, 목차 등) 조회를 위한 검색 API<br>• **[보안]** 클라이언트에 `TTBKey`가 노출되지 않도록 백엔드에서 API를 대리 호출(Proxy)하여 정규화된 결과를 반환<br>• **[이미지 고화질화]** cover URL의 `/coversum/` → `/cover500/` 자동 치환으로 고해상도 표지 제공<br>• **[ISBN 정규화]** isbn13(13자리) 우선 추출 및 정규화하여 저장<br>• 신규 도서 입고(관리자) 및 희망 도서 신청(사용자) 시 폼(Form) 자동 완성을 위해 활용<br>• **구현 경로**: `GET /api/v1/external/aladin/books?query=자바&target=title&page=1&size=10` | **R1 (MVP 필수)** |
| **희망 도서** | 신규 도서 신청 | • 사내 미보유 도서명/ISBN 건의<br>• **[UX 개선]** 알라딘 도서 검색 API 연동을 통해 정확한 ISBN13 및 도서 정보(분야, 고해상도 표지) 자동 채움 지원<br>• [예외] 요청한 ISBN이 보유 목록에 실존하거나 중복 대기 중이면 반려 처리<br>• **구현 경로**: `POST /api/v1/book-requests`, `GET /api/v1/book-requests` | **R1 (MVP 필수)** |
| | 희망도서 라이프사이클 | • 신청(PENDING) → 승인(APPROVED)/반려(REJECTED) → 입고(ARRIVED) → 도서 정식 등록<br>• 관리자 결재함에서 승인/반려 처리<br>• 승인 후 입고 시 Book 엔티티 자동 등록 (알라딘 API 연동)<br>• 사용자에게 승인/반려 알림 발송 (Redis Pub/Sub + SSE) | **R1 (MVP 필수)** |
| **대출/반납/연장** | 도서 실물 대출 | • **[제限]** 1인 최대 10권 누적, 14일 대출<br>• **[UPDATE] 연체 정지 정책**: 여러 권 연체 시, 가장 오래 연체된 도서의 **'최대 지연일(Max)'**을 기준으로 정지 기간 산정<br>• **[보안-동시성]** 1권의 재고 동시 클릭 시 비관적 락(`Pessimistic Lock`) 적용<br>• **구현 경로**: `POST /api/v1/borrows` | **R1 (MVP 필수)** |
| | 도서 대출 연장 | • **[NEW]** 반납 예정일 연장 (1회 한정, 7일 추가)<br>• **[제약]** 단, 해당 도서(`BOOK` 단위)에 대기 중인 예약자가 **1명이라도 존재**하면 연장 불가 차단 조치<br>• **[프로세스]** 사용자 연장 요청 → 관리자가 `PATCH /admin/borrows/{borrowId}/extend/approve`로 최종 승인 | **R2 (선택)** |
| | 동일 도서 재대출 | • **[R2]** 반납 후 7일 경과 시 재대출 가능<br>• **[예외]** 해당 도서 예약자가 없을 경우에 한해 반납 즉시 1회 재대출 허용<br>• **[제약]** 동일 도서 연속 재대출 최대 1회 제한 | **R2 (선택)** |
| | 도서 즉시 반납 (상태 전이) | • **[UPDATE] 반납 시 상태 전이 로직**:<br>  1. 대기 예약자가 없으면 ➔ `AVAILABLE`(대출 가능)<br>  2. 예약자가 대기 중이면 ➔ `RESERVE_HOLD`(예약 대기/보관 중)으로 전환<br>• **[보안-IDOR]** 내가 빌린 기록(`borrowId`)이 맞는지 토큰과 대조하여 강제 반납 방어<br>• **구현 경로**: `POST /api/v1/borrows/{borrowId}/return` | **R1 (MVP 필수)** |
| **도서 예약** | 대출 예약 선점 | • 현재 재고가 0인 권에 한하여 대기열 신청<br>• 1인 2권 제한, 도서별 최대 2명 대기열 제한 방어<br>• **구현 경로**: `POST /api/v1/reservations`, `DELETE /api/v1/reservations/{reservationId}` | **R1 (MVP 필수)** |
| **자동 보관/승계** | 예약 승계 스케줄러 (4일 룰) | • **[UPDATE]** `RESERVE_HOLD` 상태인 책을 예약자가 수령하지 않고 **4일(96시간)** 초과 시:<br>  1. 1순위 예약 기록을 `EXPIRED` 철회<br>  2. 2순위 대기자가 있으면 알림 발송 후 4일 다시 카운트<br>  3. 남은 대기자 없으면 `AVAILABLE` 전환 (스케줄러/Batch 작동)<br>• **구현 경로**: Spring `@Scheduled` 배치 작업 | **R1 (MVP 필수)** |
| **알림 시스템** | 알림 발송 | • **[NEW] Redis Pub/Sub + SSE 기반 실시간 알림**:<br>  - 대출 승인, 반납 완료, 예약 순번 도래, 희망도서 승인/반려 시 Redis 채널로 메시지 발행<br>  - Spring SSE를 통해 클라이언트로 실시간 전달<br>• **인앱 알림 엔티티·API 구현**: `GET /api/v1/notifications`, `PATCH /api/v1/notifications/{id}/read`<br>• 알림 읽음 처리, 미읽음 카운트 배지 표시<br>• ⚠️ 실제 외부 발송(문자·이메일) 로직은 R2에서 구현 예정 | **R1 (MVP 필수)** |
| **시스템/부가** | 도서 검색 엔진 고도화 | • **[NEW] 데이터 검색 최적화**: 일반 RDBMS `LIKE` 쿼리 한계를 대비하여 ISBN 정합성 인덱싱(Index) 추가 구현<br>• 장기적으로 전문 검색 엔진(Elasticsearch) 도입 고려 기반 마련 | R3 (확장) |
| | Soft Delete 데이터 보호 | • **[NEW] 데이터 무결성 보호(Soft Delete)**: 유저 탈퇴, 도서 폐기 내역, 예약 거절 등을 `DELETE` 쿼리로 날리지 않고 `is_deleted = true` 기반 논리적 삭제 및 이력 보존<br>• `@SQLDelete` + `@Where(clause = "is_deleted = false")` 적용 완료 (`users`, `book`, `book_hold`, `book_borrow`, `book_request`, `reservation`, `department`) | **R1 (MVP 필수)** |
| **관리자(Admin)** | 플랫폼 자원(도서) 관리 | • 재고 신규 등록 / 도서 정보 수동 수정 및 삭제(비활성화)<br>• **[데이터 보호]** 도서 삭제 시 실 DB 삭제가 아닌 Soft Delete(`is_deleted=true`) 처리<br>• **[UX 개선]** 알라딘 도서 검색 API를 활용한 신규 도서 메타데이터 원클릭 등록 지원 (ISBN13 기준, 분야/목차/책소개 포함)<br>• **[예외]** 대출 중인 서적(`BORROWED`)을 관리자가 인위적으로 `LOST`(분실), `DISCARDED`(폐기)으로 변경 시도 시 HTTP 400 예외 반환<br>• **구현 경로**: `POST /api/v1/admin/books`, `PUT /api/v1/admin/books/{bookId}`, `POST /api/v1/admin/books/{bookId}/holds`, `PATCH /api/v1/admin/book-holds/{bookHoldId}/status` | **R1 (MVP 필수)** |
| | 희망 도서 신청 승인 | • 관리자의 검토를 통한 `APPROVED`/`REJECTED` 처리 (사유 첨부)<br>• **구현 경로**: `PATCH /api/v1/admin/book-requests/{requestId}/status` | **R1 (MVP 필수)** |
| | 구성원/연체자 관리 | • 전체 회원 강제 열람, 연체 로그 추적, 유저에게 `ROLE_ADMIN` 관리자 권한 이양 | R2 (선택) |

---

## 알림 시스템 (Redis Pub/Sub + SSE)

### 개요
부기맨의 알림 시스템은 두 계층으로 구성됩니다:
1. **백엔드 이벤트 발행**: 도서 대출/반납/예약 등의 비즈니스 이벤트 발생 시 Redis Pub/Sub으로 메시지 발행
2. **실시간 클라이언트 전달**: Spring SSE를 통해 클라이언트에게 실시간 알림 푸시

### 알림 트리거 (trigger events)

| 이벤트 | 발행 조건 | 알림 대상 |
|--------|---------|---------|
| `BOOK_REQUEST_APPROVED` | 희망도서 신청 승인 | 신청한 사용자 |
| `BOOK_REQUEST_REJECTED` | 희망도서 신청 반려 | 신청한 사용자 |
| `BOOK_REQUEST_ARRIVED` | 희망도서 실물 입고 완료 (ARRIVED 상태 전환) | 신청한 사용자 |
| `RESERVATION_ARRIVED` | 예약 도서 반납됨 (차순위자) | 예약 대기 중인 사용자 |
| `RETURN_DUE_SOON` | 반납 예정일 1일 전 | 대출 중인 사용자 |
| `OVERDUE_NOTICE` | 연체 발생 | 연체 사용자 |

> ⚠️ **타입명 주의**: `BORROW_APPROVED` / `BORROW_REJECTED` 명칭은 도서 대출(Borrow)과 혼동되므로 `BOOK_REQUEST_*` 접두어로 변경. DB 마이그레이션 및 코드 전체 일괄 수정 필요.

---

## 희망 도서 라이프사이클 (상세)

```
신청 (PENDING)
    ↓
[관리자 결재]
    ↓
승인 (APPROVED) ← 또는 → 반려 (REJECTED) [이력 보관]
    ↓
[입고 처리]
    ↓
도서 정식 등록 (Book + BookHold 생성)
    ↓
[사용자 대출 가능]
```

### 상세 프로세스

1. **신청 단계 (PENDING)**
   - 사용자가 `POST /api/v1/book-requests`로 도서 신청
   - 알라딘 API를 통해 ISBN13, 표지, 분야 등 자동 채움
   - 중복 검사: 보유 도서 또는 진행 중인 신청이 있으면 HTTP 400 반환

2. **관리자 결재 (APPROVED / REJECTED)**
   - 관리자가 `/admin/requests` 페이지에서 신청 목록 조회
   - `PATCH /api/v1/admin/book-requests/{requestId}/status` 호출
   - 승인 시: 이벤트 발행 → 사용자 알림 (`NotificationEvent`)
   - 반려 시: 사유 저장 후 사용자 알림

3. **도서 정식 등록**
   - 관리자가 실제 도서를 구입했을 때, `/admin/books`에서 신규 도서 등록
   - 기존 승인된 신청과 ISBN 매칭 → 자동으로 BookRequest 상태 갱신 (선택)

---

## 데이터베이스 제약사항

### Soft Delete (논리적 삭제)

다음 엔티티들은 **Hard Delete를 사용하지 않고** `is_deleted` 컬럼으로 관리합니다:

- `users`
- `book`
- `book_hold`
- `book_borrow`
- `book_request`
- `reservation`
- `department`

### 동시성 제어

**대출 기능 (`POST /api/v1/borrows`)**에서 마지막 1권에 대한 다중 요청 시:
- `@Lock(LockModeType.PESSIMISTIC_WRITE)` 적용
- 1인에게만 도서 할당, 나머지는 HTTP 409 `CONFLICT` 반환

---

## 보안 체크리스트

- ✅ IDOR 방어: 모든 개인 리소스 조회/수정 시 토큰 사용자 ID와 대조
- ✅ XSS 필터링: 사용자 입력 텍스트 (신청 사유 등) 이스케이프
- ✅ CSRF 방어: (Thymeleaf 자동 CSRF 토큰 적용)
- ✅ Soft Delete: Hard Delete 금지
- ✅ 외부 API 키: Proxy 패턴으로 TTBKey 노출 방지
- ✅ 토큰 관리: Access/Refresh Token Rotation + 블랙리스트

