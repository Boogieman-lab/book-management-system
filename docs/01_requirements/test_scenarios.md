# 🧪 부기맨(Boogieman) MVP 1 기능별 테스트 시나리오

> **작성일**: 2026-03-30
> **기준**: requirements.md, functional_spec.md R1(필수) 기능 전체
> **범위**: 총 60개 시나리오 / 10개 기능 영역

---

## 목차

1. [인증 (Auth)](#1-인증-auth)
2. [도서 검색](#2-도서-검색)
3. [희망도서 신청](#3-희망도서-신청)
4. [도서 대출](#4-도서-대출)
5. [도서 반납](#5-도서-반납)
6. [도서 예약](#6-도서-예약)
7. [예약 승계 스케줄러 (4일 룰)](#7-예약-승계-스케줄러-4일-룰)
8. [알림 시스템](#8-알림-시스템)
9. [관리자 도서 관리](#9-관리자-도서-관리)
10. [보안 공통](#10-보안-공통)

---

## 1. 인증 (Auth)

### 1-1. 회원가입

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|---------|
| T01 | 정상 이메일 회원가입 | 유효한 이메일/비밀번호/이름 | 201, 사용자 생성, 기본 `ROLE_USER` 부여 |
| T02 | 이메일 중복 가입 | 이미 존재하는 이메일 | 400 `DUPLICATE_EMAIL` |
| T03 | 비밀번호 형식 불충족 | 너무 짧은 비밀번호 | 400 유효성 오류 |

### 1-2. 로그인

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|---------|
| T04 | 정상 로그인 | 올바른 이메일/비밀번호 | 200, `AccessToken` + `RefreshToken` 반환 |
| T05 | 비밀번호 불일치 | 잘못된 비밀번호 | 401 `INVALID_CREDENTIALS` (이메일 존재 여부 비노출) |
| T06 | 연속 5회 로그인 실패 | 잘못된 비밀번호 5회 | `is_locked=true` 전환, 이후 시도 시 403 `ACCOUNT_LOCKED` |
| T07 | 잠긴 계정 로그인 | `is_locked=true` 계정 | 403 `ACCOUNT_LOCKED` |

### 1-3. 토큰 관리

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|---------|
| T08 | Refresh Token으로 재발급 | 유효한 `RefreshToken` | 200, 새 `AccessToken` + 새 `RefreshToken` 발급, 기존 `RefreshToken` 무효화 |
| T09 | 만료된 RefreshToken 재발급 시도 | 만료된 `RefreshToken` | 401 |
| T10 | 로그아웃 후 AccessToken 재사용 | 로그아웃된 `AccessToken` | 401 (Redis 블랙리스트 차단) |

---

## 2. 도서 검색

### 2-1. 내부 도서 검색

| # | 시나리오 | 입력 | 기대 결과 |
|---|---------|------|---------|
| T11 | 제목으로 검색 | `field=TITLE&keyword=자바` | 200, 제목에 "자바" 포함된 도서 목록 반환 |
| T12 | 저자로 검색 | `field=AUTHOR&keyword=김영한` | 200, 해당 저자 도서 목록 반환 |
| T13 | ISBN으로 검색 | `field=ISBN&keyword=9791...` | 200, 해당 ISBN 도서 반환 |
| T14 | 출판사로 검색 | `field=PUBLISHER&keyword=인프런` | 200, 해당 출판사 도서 목록 반환 |
| T15 | 검색 결과 없음 | 존재하지 않는 키워드 | 200, 빈 배열 반환 |
| T16 | 도서 상세 조회 | 유효한 `bookId` | 200, 도서 정보 + 실재고(Hold) 권수 포함 |
| T17 | 존재하지 않는 도서 상세 조회 | 없는 `bookId` | 404 `BOOK_NOT_FOUND` |

### 2-2. 알라딘 외부 검색 (Proxy)

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T18 | 알라딘 API 프록시 검색 | 200, ISBN13 정규화된 결과 반환, 표지 URL에 `/cover500/` 포함 (고해상도) |
| T19 | 응답 내 TTBKey 미포함 확인 | 클라이언트 응답 JSON에 `TTBKey` 노출 없음 |

---

## 3. 희망도서 신청

### 3-1. 사용자 신청

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T20 | 정상 희망도서 신청 | 201, `status=PENDING` |
| T21 | 이미 보유 중인 ISBN 신청 | 400 `DUPLICATE_BOOK` |
| T22 | 동일 ISBN 중복 신청 (진행 중인 건 존재) | 400 중복 신청 반려 |
| T23 | 내 신청 내역 조회 | 200, `PENDING` / `APPROVED` / `REJECTED` 목록 반환 |

### 3-2. 관리자 승인/반려

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T24 | 관리자 신청 승인 | 200, `status=APPROVED`, 신청자에게 `BOOK_REQUEST_APPROVED` 알림 발송 |
| T25 | 관리자 신청 반려 | 200, `status=REJECTED`, 사유 저장, 신청자에게 `BOOK_REQUEST_REJECTED` 알림 발송 |
| T26 | 일반 유저가 승인 API 호출 | 403 `FORBIDDEN` |

### 3-3. 도서 입고 (ARRIVED)

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T27 | 승인된 신청 도서 입고 처리 | `status=ARRIVED`, 신청자에게 `BOOK_REQUEST_ARRIVED` 알림 발송 |

---

## 4. 도서 대출

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T28 | 정상 대출 (`AVAILABLE` 도서) | 201, `BookHold.status=BORROWED`, 반납 예정일 = 오늘 + 14일 |
| T29 | 대출 한도 초과 (10권) | 409 `BORROW_LIMIT_EXCEEDED` |
| T30 | 이미 대출 중인 `BookHold` 대출 시도 | 409 `BOOK_NOT_AVAILABLE` |
| T31 | 연체 정지 사용자 대출 시도 | 403 `BORROW_RESTRICTED` |
| T32 | 예약자 본인의 `RESERVE_HOLD` 도서 대출 | 201, `reservation.status=RESERVED` 전환 |
| T33 | 예약자가 아닌 사용자가 `RESERVE_HOLD` 도서 대출 시도 | 409 `BOOK_NOT_AVAILABLE` |
| T34 | 동시 대출 요청 (비관적 락 검증) | 1건만 성공, 나머지 409 반환 |

---

## 5. 도서 반납

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T35 | 정상 반납 (예약 대기자 없음) | 200, `BookHold.status=AVAILABLE` 전환 |
| T36 | 반납 시 예약 대기자 있음 | 200, `BookHold.status=RESERVE_HOLD` 전환, 예약자에게 `RESERVATION_ARRIVED` 알림 발송 |
| T37 | 타인의 대출 반납 시도 (IDOR) | 403 `FORBIDDEN` |
| T38 | 존재하지 않는 `borrowId` 반납 시도 | 404 |

---

## 6. 도서 예약

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T39 | 대출 중인 도서 예약 | 201, `status=WAITING` |
| T40 | 대출 가능(`AVAILABLE`) 도서 예약 시도 | 400 `RESERVATION_NOT_ALLOWED` |
| T41 | 동일 도서 중복 예약 시도 | 409 `RESERVATION_ALREADY_EXISTS` |
| T42 | 1인 예약 한도 초과 (2권 초과) | 409 `RESERVATION_LIMIT_EXCEEDED` |
| T43 | 도서 1권당 예약자 3명 시도 | 409 (최대 2명 초과) |
| T44 | 예약 취소 | 200, `status=CANCELLED` |
| T45 | 1순위 예약 취소 시 2순위 즉시 승계 | 2순위에게 `RESERVATION_ARRIVED` 알림 발송, `status=NOTIFIED` 전환 |
| T46 | 타인의 예약 취소 시도 (IDOR) | 403 `FORBIDDEN` |
| T47 | 동시 예약 요청 (비관적 락 검증) | 초과분은 409 반환 |

---

## 7. 예약 승계 스케줄러 (4일 룰)

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T48 | `RESERVE_HOLD` 도서 4일 경과, 2순위 대기자 있음 | 1순위 `EXPIRED` 처리, 2순위에게 알림 발송 후 4일 보관 카운트 재시작 |
| T49 | `RESERVE_HOLD` 도서 4일 경과, 대기자 없음 | `BookHold.status=AVAILABLE` 전환 |

---

## 8. 알림 시스템

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T50 | SSE 구독 연결 | `GET /notifications/subscribe` → 200, `Content-Type: text/event-stream` 연결 유지 |
| T51 | 알림 목록 조회 | 200, 읽음/미읽음 포함한 내 알림 목록 반환 |
| T52 | 알림 읽음 처리 | 200, `is_read=true` 전환 |
| T53 | 타인의 알림 읽음 처리 시도 (IDOR) | 403 `FORBIDDEN` |

---

## 9. 관리자 도서 관리

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T54 | 신규 도서 등록 | 201, `Book` + `BookHold` 생성 |
| T55 | 대출 중인 `BookHold`를 `LOST`/`DISCARDED`로 변경 시도 | 400 예외 반환 |
| T56 | 계정 잠금 해제 | `PATCH /admin/users/{id}/unlock` → 200, `is_locked=false` 전환 |

---

## 10. 보안 공통

| # | 시나리오 | 기대 결과 |
|---|---------|---------|
| T57 | 인증 없이 보호된 API 호출 | 401 |
| T58 | 일반 유저가 `/api/v1/admin/**` 호출 | 403 |
| T59 | Soft Delete 확인 — 삭제된 도서 목록 조회 | 해당 도서 목록에 노출되지 않음 |
| T60 | XSS 입력 (신청 사유에 `<script>` 삽입) | 이스케이프 처리되어 저장, 스크립트 실행 없음 |

---

## 알림 타입 참조

| 알림 타입 | 한글 설명 | 발송 시점 |
|---------|---------|---------|
| `BOOK_REQUEST_APPROVED` | 희망 도서 신청 승인 | 관리자 승인 처리 시 |
| `BOOK_REQUEST_REJECTED` | 희망 도서 신청 반려 | 관리자 반려 처리 시 |
| `BOOK_REQUEST_ARRIVED` | 희망 도서 입고 | 도서 입고(ARRIVED) 처리 시 |
| `RESERVATION_ARRIVED` | 예약 도서 입고 | 예약 도서 반납 시 / 승계 처리 시 |
| `BORROW_DUE_SOON` | 반납 기한 임박 | 반납 예정일 1일 전 (배치) |
| `BORROW_OVERDUE` | 반납 기한 초과 | 연체 감지 배치 |
