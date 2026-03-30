# 🗺️ 부기맨(Boogieman) 고도화 액션 플랜 (Action Plan)

> **작성일**: 2026-03-27
> **최종 업데이트**: 2026-03-30
> **분석 근거**: requirements.md, api_spec.md, erd.md, functional_spec.md, 시퀀스 다이어그램 전체 교차 검증

> **구현 현황**: ✅ 완료 | ⚠️ 부분 완료 (문서만 수정, 코드 미구현) | ❌ 미구현

---

## 📋 발견된 이슈 전체 목록

### [Critical] 설계 불일치 및 논리 오류

| ID | 제목 | 영향 범위 | 관련 문서 | 상태     |
|----|------|-----------|----------|--------|
| C-01 | 예약자 수령 대출 플로우 미설계 — `RESERVE_HOLD` 도서 대출 시 409 반환 | 예약 기능 전체 사용 불가 | book_borrow_sequence, reservation_sequence | ✅ 완료 |
| C-02 | 알림 타입명 오염 — `BORROW_APPROVED/REJECTED` → `BOOK_REQUEST_*` | DB 마이그레이션 + 코드 전파 | erd.md, functional_spec, notification_sequence | ✅ 완료 |
| C-03 | `BOOK_REQUEST` 상태에 `ARRIVED` 누락 — 승인 후 입고 완료 상태 전환 불가 | 희망도서 라이프사이클 단절 | erd.md, functional_spec | ✅ 완료 |
| C-04 | `NOTIFICATION` 테이블에 `related_book_request_id` 컬럼 누락 | 희망도서 알림 클릭 → 상세 이동 불가 | erd.md | ❌ 미구현 (V3.1 migration 없음) |
| C-05 | 예약 취소 시 2순위 즉시 승계 로직 없음 — 배치 스케줄러(최대 24h 지연)에만 의존 | 예약 UX 저하 | reservation_sequence | ✅ 완료 |
| C-06 | 예약 순위(`reservation_order`) 정합성 — 1순위 취소 후 2순위 order 미갱신 | 예약 신청 시 순위 오표시 | reservation_sequence | ⚠️ 문서만 수정, 코드 미구현 |
| C-07 | 토큰 TTL 운영 부적합 — AccessToken 3분, RefreshToken 5분 | 5분 비활성 시 자동 로그아웃 | requirements.md | ✅ 완료 |
| C-08 | `OVERDUE_RECORD` `is_deleted` 컬럼 누락 — Hard Delete 시 대출 제한 우회 보안 취약 | 감사 데이터 보안 | erd.md | ⚠️ 문서만 수정, V3.4 migration 없음 |

### [Update] 기존 기능 수정 제안

| ID | 제목 | 효과 | 난이도 | 상태   |
|----|------|------|--------|------|
| U-01 | 예약 생성 동시성 보강 — `SELECT COUNT FOR UPDATE` 또는 Optimistic Lock | 동시 예약 3명 초과 방지 | 중 | ✅ 완료     |
| U-02 | SSE Scale-out 구조 검증 — Redis 채널 기반 브로드캐스트 확인 | 다중 인스턴스 알림 누락 방지 | 높 | ✅ 완료     |
| U-03 | 반납 시 예약자 조회 쿼리 정밀화 — `WAITING`만 조회 (현재 NOTIFIED 중복 알림 위험) | 알림 중복 발송 방지 | 낮 | ✅ 완료     |
| U-04 | `users.restriction_until` 캐시 컬럼 추가 — 대출 시 overdue_record N+1 제거 | 대출 API 쿼리 수 감소 | 낮 | ✅ 완료 |
| U-05 | 희망도서 승인/입고 알림 단계 분리 — APPROVED와 ARRIVED 각각 별도 알림 | 사용자 구매 진행 상황 투명화 | 낮 | ✅ 완료 |

### [New] 신규 추가 권장 기능

| ID   | 제목 | 우선순위 | 관련 API | 상태    |
|------|------|----------|----------|-------|
| N-01 | SSE Subscribe 엔드포인트 구현 | R1 필수 | `GET /notifications/subscribe` | ✅ 완료  |
| N-02 | 관리자 계정 잠금 해제 API | R1 필수 | `PATCH /admin/users/{id}/unlock` | ✅ 완료  |
| N-03 | 반납 예정일 알림 배치 스케줄러 (`@Scheduled`, 매일 오전 6시) | R1 문서 상 필수 | - | ❌ 미구현 |
| N-04 | 연체 자동 감지 + `BOOK_BORROW.status=OVERDUE` 전환 배치 (매일 자정) | R2 | - | ❌ 미구현 |
| N-05 | 4일 자동 승계 스케줄러 (reservation_sequence 설계 완료, 구현 미완) | R1 문서 상 필수 | - | ❌ 미구현 |
| N-06 | `GET /admin/reservations` — 예약 대기 통계 조회 | R2 | - | ❌ 미구현 |

---

## 🚀 우선순위 로드맵

### Phase 1 — MVP 핵심 버그 수정 (즉시, ~1주)

> **목표**: 예약 기능이 실제로 동작하도록 만들고, 운영에 필수적인 API를 추가

| 순위 | ID | 작업 | 예상 공수 | 담당 |
|------|----|------|-----------|------|
| 🔴 1 | C-01 | `BookBorrowService` — `RESERVE_HOLD` 분기 추가, 예약자 본인 검증 후 `reservation.status=RESERVED` 전환 | 2일 | BE |
| 🔴 2 | N-01 | `GET /notifications/subscribe` SSE 엔드포인트 구현 + `SseEmitterMap` 관리 | 2일 | BE |
| 🔴 3 | C-05 | `ReservationService.cancel()` — NOTIFIED 취소 시 다음 대기자 즉시 승계 로직 추가 | 1일 | BE |
| 🔴 4 | N-02 | `PATCH /admin/users/{id}/unlock` API 구현 | 0.5일 | BE |
| 🟡 5 | C-02 | `BORROW_APPROVED/REJECTED` → `BOOK_REQUEST_APPROVED/REJECTED` 전체 수정 + Flyway 마이그레이션 | 1일 | BE |

**Phase 1 완료 기준**: 예약 → 알림 수신 → 수령 대출 전체 플로우 E2E 동작

---

### Phase 2 — 배치 작업 및 안정화 (~2~3주)

> **목표**: 자동화 알림 및 연체 관리 가동, 설계 누락 항목 보완

| 순위 | ID | 작업 | 예상 공수 |
|------|----|------|-----------|
| 6 | N-03 | 반납 예정일 알림 배치 (`ReturnDueSoonScheduler`) | 1일 |
| 7 | N-05 | 4일 자동 승계 배치 (`ReservationExpiryScheduler`) | 1일 |
| 8 | N-04 | 연체 자동 감지 배치 (`OverdueDetectionScheduler`) + `OVERDUE` 상태 전환 | 1.5일 |
| 9 | C-03 | `BOOK_REQUEST` `ARRIVED` 상태 추가 + 관련 API/알림 처리 + Flyway 마이그레이션 | 1일 |
| 10 | U-01 | 예약 생성 동시성 보강 (`FOR UPDATE`) | 0.5일 |
| 11 | U-03 | 반납 시 예약자 쿼리 `WAITING` 한정 수정 | 0.5일 |
| 12 | U-04 | `users.restriction_until` 컬럼 추가 + 연체 발생 시 동기 갱신 | 1일 |

**Phase 2 완료 기준**: 알림 시스템 4종 모두 동작, 연체 자동 감지, 배치 작업 스케줄 가동

---

### Phase 3 — R2 기능 및 고도화 (~4주~)

> **목표**: 관리자 운영 도구 완성, 성능/보안 강화

| 순위 | ID | 작업 | 예상 공수 |
|------|----|------|-----------|
| 13 | C-07 | 토큰 TTL 운영 기준 조정 (AccessToken 15~30분, RefreshToken 7일) | 0.5일 |
| 14 | C-08 | `OVERDUE_RECORD` `is_deleted` 컬럼 추가 + Flyway 마이그레이션 | 0.5일 |
| 15 | C-04 | `NOTIFICATION` `related_book_request_id` 컬럼 추가 | 0.5일 |
| 16 | C-06 | 예약 순위 동적 계산 전환 또는 취소 시 order 갱신 처리 | 1일 |
| 17 | N-06 | `GET /admin/reservations` 예약 통계 API | 1일 |
| 18 | U-02 | SSE Scale-out 검증 — 다중 인스턴스 환경 Redis 브로드캐스트 테스트 | 1일 |
| 19 | - | R2 통계 대시보드 (`/admin/stats/overview`, 월별 대출, 인기 도서) | 3일 |
| 20 | - | 연장 신청/승인 플로우 (`POST /borrows/{id}/extend`, `PATCH /admin/borrows/{id}/extend/approve`) | 2일 |

---

## 📊 이슈별 임팩트 매트릭스

```
임팩트(Impact)
    ↑
 높음 │ [C-01] ──────────── [N-01]
     │ [C-05]               [N-03][N-04]
     │                      [N-05]
 중간 │ [C-02][C-03]        [U-01][N-06]
     │ [N-02]               [U-03]
 낮음 │ [C-04][C-06]        [C-07][C-08]
     │ [U-04][U-05]         [U-02]
     │
     └────────────────────────────────→ 구현 복잡도(Effort)
            낮음           중간          높음
```

---

## 🔧 Flyway 마이그레이션 필요 항목

| 버전 | 변경 내용 | Phase |
|------|----------|-------|
| V3.0 | `NOTIFICATION.type` ENUM 값 `BORROW_APPROVED→BOOK_REQUEST_APPROVED`, `BORROW_REJECTED→BOOK_REQUEST_REJECTED` | 1 |
| V3.1 | `NOTIFICATION` 테이블 `related_book_request_id BIGINT` 컬럼 추가 | 3 |
| V3.2 | `BOOK_REQUEST.status` ENUM에 `ARRIVED` 추가 | 2 |
| V3.3 | `USERS` 테이블 `restriction_until DATETIME` 컬럼 추가 | 2 |
| V3.4 | `OVERDUE_RECORD` 테이블 `is_deleted BOOLEAN NOT NULL DEFAULT FALSE` 컬럼 추가 | 3 |

> ⚠️ V3.0 알림 타입명 변경은 기존 DB 데이터 UPDATE 쿼리가 필요합니다:
> ```sql
> UPDATE notification SET type = 'BOOK_REQUEST_APPROVED' WHERE type = 'BORROW_APPROVED';
> UPDATE notification SET type = 'BOOK_REQUEST_REJECTED' WHERE type = 'BORROW_REJECTED';
> ```
