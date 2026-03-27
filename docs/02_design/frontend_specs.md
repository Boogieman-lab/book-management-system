# 🖥️ 부기맨(Boogieman) 프론트엔드 화면 목록 및 상태 관리 문서

본 문서는 부기맨 MVP 시스템의 프론트엔드 라우팅 및 화면 단위별 진행률(Status Tracker)을 관리하기 위한 문서입니다. 모든 뷰(View)는 컴포넌트 단위로 식별 ID가 부여되며, 백엔드의 REST API와 1:1 매핑을 추적합니다.

---

## 📌 화면 ID 표기 규칙 (Screen Naming Convention)

체계적인 컴포넌트 관리와 라우팅 설정을 위해 접두어(Prefix) 규칙을 아래와 같이 명의합니다.

* **UI-COM** : 공통 요소 (Layout, Header, Footer)
* **UI-AUT** : 인증 및 인가 (Auth - 로그인/회원가입)
* **UI-BKS** : 도서 서비스 (Books - 검색/상세/대출/예약)
* **UI-REQ** : 희망 도서 신청 (Request)
* **UI-MYP** : 나만의 공간 대시보드 (MyPage)
* **UI-INF** : 정보 및 공지 (Info/Notices)
* **UI-ADM** : 운영자 통제 보드 (Admin)

---

## 📐 API 설계 원칙

모든 API는 아래 원칙을 준수한다. 백엔드 구현 시 이 원칙을 기준으로 설계하고, 변경 시에는 본 문서를 먼저 수정한 후 PR 리뷰를 거쳐 구현한다.

### Base URL

```
/api/v1/
```

모든 REST API는 `/api/v1/` prefix를 사용한다. 뷰 라우트(`/books`, `/user/auth/login` 등)와 API 경로는 반드시 분리한다.

### 공통 규칙

| 항목 | 규칙 | 예시 |
| :--- | :--- | :--- |
| 리소스명 | 복수 명사 (kebab-case) | `/books`, `/book-requests` |
| 동작 표현 | HTTP Method로만 표현 | `POST /borrows` (대출), `DELETE /reservations/{id}` (취소) |
| 상태 변경 | `PATCH` + body | `PATCH /borrows/{id}` + `{"status": "RETURNED"}` |
| 필터/검색 | Query Parameter | `GET /books?q=자바&category=IT&page=0&size=20` |
| 3rd-party 추상화 | 내부 구현 숨김 | `/external/aladin/books` (aladin 직접 노출 금지) |
| Admin 네임스페이스 | `/admin/` prefix 분리 | `/api/v1/admin/books` |

### 표준 HTTP 상태 코드

| 코드 | 의미 | 사용 시점 |
| :--- | :--- | :--- |
| `200 OK` | 성공 | 조회, 수정 성공 |
| `201 Created` | 생성 성공 | POST 성공 (Location 헤더 포함 권장) |
| `204 No Content` | 성공 (본문 없음) | 삭제, 로그아웃 |
| `400 Bad Request` | 입력값 검증 실패 | 필수 필드 누락, 형식 오류 |
| `401 Unauthorized` | 미인증 | 토큰 없음/만료 |
| `403 Forbidden` | 권한 부족 | ROLE_USER가 admin API 접근 |
| `404 Not Found` | 리소스 없음 | 존재하지 않는 ID |
| `409 Conflict` | 중복/충돌 | 이미 대출 중, 이미 예약 중 |

### 표준 응답 형식

```json
// 목록 응답
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150
}

// 에러 응답
{
  "code": "BORROW_ALREADY_EXISTS",
  "message": "이미 대출 중인 도서입니다."
}
```

---

## 📊 부기맨 프론트엔드 화면 목록 및 상태 관리보드 (Screen List Board)

| 화면 ID | 분류(Depth 1) | 화면명(Depth 2) | 추정 URL (Route) | 연동 API Endpoint (Method) | 주요 컴포넌트 | 진행 상태 | QA 상태 | 비고 |
|:---|:---|:---|:---|:---|:---|:---:|:---:|:---|
| **UI-COM-001** | **공통** | 메인 레이아웃 및 헤더/푸터 | `(All)` | ❌ API 없음 (Spring Security `sec:authorize` 기반 서버사이드 메뉴 렌더링) | Header, Footer, Sidebar | ✅ Done | ⬜ 미진행 | Thymeleaf Layout Dialect 기반 |
| **UI-AUT-001** | **인증** | 로그인 메인 화면 | `/user/auth/login` | `POST /api/auth/login`<br>`GET /api/auth/oauth/kakao` | 이메일 입력, 패스워드 입력, 로그인 버튼, 카카오 로그인 | ✅ Done | ⬜ 미진행 | HttpOnly 쿠키로 토큰 저장 |
| **UI-AUT-002** | **인증** | 신규 회원가입 폼 | `/user/auth/signup` | `POST /api/auth/signup` | 이름 입력, 이메일 입력, 비밀번호 입력, 회원가입 버튼 | ✅ Done | ⬜ 미진행 | Bean Validation 검사 |
| **UI-BKS-001** | **도서** | 도서 통합 검색 및 전체 목록 | `/books` | `GET /api/v1/books?q=&category=&page=&size=&sort=` | 검색창, 도서 카드 그리드, 페이지네이션, 필터/정렬 옵션 | ✅ Done | ⬜ 미진행 | QueryDSL 기반 동적 검색 |
| **UI-BKS-002** | **도서** | 도서 상세 조회 및 대출/예약 창 | `/books/{bookId}` | `GET /api/v1/books/{bookId}`<br>`POST /api/v1/borrows`<br>`POST /api/v1/reservations` | 도서 표지, 상세정보, 대출/예약 버튼, 현재 대출 상태 | ✅ Done | ⬜ 미진행 | 재고 여부에 따라 버튼 동적 변경 |
| **UI-REQ-001** | **신청** | 희망 도서 외부 검색 모달 | `팝업(Modal)` | `GET /api/v1/external/aladin/books?query=&target=&page=&size=` | 검색창, 알라딘 도서 검색 결과 리스트, 선택 버튼 | ✅ Done | ⬜ 미진행 | Aladin API 프록시 연동 |
| **UI-REQ-002** | **신청** | 외부 도서 반입(희망) 신청 폼 | `/book-requests` | `POST /api/v1/book-requests` | 도서명 입력, 저자 입력, ISBN 입력, 신청 사유 입력, 외부 검색 모달 버튼 | ✅ Done | ⬜ 미진행 | 모달에서 선택한 도서 자동 완성 |
| **UI-REQ-003** | **신청** | 희망 도서 신청 내역 | `/book-requests` | `GET /api/v1/book-requests` | 신청 리스트, 상태 배지(PENDING/APPROVED/REJECTED), 신청일시, 상태 필터 | ✅ Done | ⬜ 미진행 | 탭 UI로 신청/내역 분리 |
| **UI-MYP-001** | **마이페이지** | 개인 프로필 통합 홈 & 수정 | `/user/mypage` | `GET /api/v1/users/me`<br>`PATCH /api/v1/users/me` | 프로필 이미지, 이름, 부서, 이메일, 수정 버튼 | ✅ Done | ⬜ 미진행 | IDOR 검증 철저 |
| **UI-MYP-002** | **마이페이지** | 대출/반납 이력 탭 | `/user/mypage?tab=borrows` | `GET /api/v1/users/me/borrows`<br>`POST /api/v1/borrows/{id}/return`<br>`POST /api/v1/borrows/{id}/extend` | 대출 도서 리스트, 반납 예정일 (D-Day 계산), 반납/연장 버튼 | ✅ Done | ⬜ 미진행 | 현재 대출 중인 도서만 표시 |
| **UI-MYP-003** | **마이페이지** | 현재 예약 및 대기열 탭 | `/user/mypage?tab=reservations` | `GET /api/v1/users/me/reservations`<br>`DELETE /api/v1/reservations/{id}` | 예약 도서 리스트, 예약 순위, 만료일, 취소 버튼 | ✅ Done | ⬜ 미진행 | 예약 순위 명시 |
| **UI-MYP-004** | **마이페이지** | 인앱 수신 알림함 탭 | `/user/mypage?tab=notifications` | `GET /api/v1/notifications`<br>`PATCH /api/v1/notifications/{id}/read` | 알림 리스트, 미읽음 뱃지, 읽음 처리 버튼 | ✅ Done | ⬜ 미진행 | Redis Pub/Sub + SSE 준비 완료 (UI 전달 대기) |
| **UI-INF-001** | **정보** | About 프로젝트 소개 (정적) | `/about` | ❌ API 없음 | 프로젝트 소개 텍스트, 부기맨 캐릭터 일러스트 | ✅ Done | ⬜ 미진행 | 정적 HTML |
| **UI-INF-002** | **정보** | 이용안내 및 시스템 공지사항 | `/notices` | `GET /api/v1/notices?page=&size=`<br>`GET /api/v1/notices/{id}` | 공지 리스트, 공지 상세 조회, 페이지네이션 | 🔶 부분완료 | ⬜ 미진행 | UI 완료(847줄), 백엔드 API 미구현 |
| **UI-ADM-001** | **관리자** | 어드민 대시보드 (통계 메인) | `/admin/dashboard` | `GET /api/v1/admin/stats/overview` | 대출 수, 연체 수, 예약 수, 월별 대출량 차트 | 🔶 부분완료 | ⬜ 미진행 | UI 완료(124줄), stats API 미구현 (R2) |
| **UI-ADM-002** | **관리자** | 도서 센터 (신규 등록 및 실물 추가) | `/admin/books` | `POST /api/v1/admin/books`<br>`PUT /api/v1/admin/books/{bookId}`<br>`POST /api/v1/admin/books/{bookId}/holds`<br>`PATCH /api/v1/admin/book-holds/{holdId}/status` | 도서 검색 모달, 도서 등록 폼, 도서 리스트, 실물 추가 버튼 | ✅ Done | ⬜ 미진행 | UI 완료(794줄), 백엔드 API 구현 완료, Aladin API 활용 |
| **UI-ADM-003** | **관리자** | 대출망 통제 (연체자 현황) | `/admin/borrows` | `GET /api/v1/admin/borrows?status=&page=&size=`<br>`POST /api/v1/admin/borrows/{id}/notify-overdue` (R2) | 대출 현황 테이블, 연체자 필터, 알림 발송 버튼 | 🔶 부분완료 | ⬜ 미진행 | UI 완료(130줄), 연체 관리 API R2 미구현 |
| **UI-ADM-004** | **관리자** | 부기맨 임직원 권한 롤 관리 | `/admin/users` | `GET /api/v1/admin/users?page=&size=`<br>`PATCH /api/v1/admin/users/{id}/role` (R2) | 회원 리스트 테이블, 권한 변경 버튼, 비활성화 버튼 | 🔶 부분완료 | ⬜ 미진행 | UI 완료(124줄), 사용자 관리 API R2 미구현 |
| **UI-ADM-005** | **관리자** | 희망 도서 결재함 (승인/거절) | `/admin/requests` | `GET /api/v1/admin/book-requests?status=&page=`<br>`PATCH /api/v1/admin/book-requests/{id}/status` | 신청 리스트 테이블, 승인/거절 버튼, 상태 필터 | ✅ Done | ⬜ 미진행 | UI 완료(194줄), 백엔드 API 구현 완료 |
| **UI-ADM-006** | **관리자** | 공지 관리 (등록/수정/삭제) | `/admin/notices` | `POST /api/v1/admin/notices`<br>`PUT /api/v1/admin/notices/{id}`<br>`DELETE /api/v1/admin/notices/{id}` | 공지 목록, 등록 폼, 수정/삭제 버튼 | ❌ Not Started | ⬜ 미진행 | R3, UI 및 API 모두 미구현 |

---

## 🗺️ API 구조 전체 설계도

```
/api/v1/
│
├── auth/
│   ├── POST   /signup                              # 회원가입
│   ├── POST   /login                               # 로그인 (응답: accessToken HttpOnly 쿠키)
│   ├── POST   /logout                              # 로그아웃 (쿠키 만료 + 블랙리스트)
│   ├── POST   /refresh                             # 토큰 갱신
│   └── GET    /oauth/kakao                         # 카카오 OAuth 리다이렉트
│
├── books/
│   ├── GET    /                                    # 목록 조회 (?q=&category=&page=&size=&sort=)
│   ├── GET    /{bookId}                            # 상세 조회
│   ├── GET    /isbn-check?isbn=                   # ISBN 중복 체크
│   ├── GET    /popular                            # 인기 도서 추천 (R2)
│   └── GET    /personalized                       # 개인화 추천 (R3)
│
├── borrows/
│   ├── POST   /                                    # 대출 신청
│   ├── POST   /{id}/return                         # 반납
│   └── POST   /{id}/extend                         # 연장 신청 (R2)
│
├── reservations/
│   ├── POST   /                                    # 예약 신청
│   └── DELETE /{id}                                # 예약 취소
│
├── book-requests/
│   ├── POST   /                                    # 희망 도서 신청
│   └── GET    /                                    # 신청 목록/상태 조회
│
├── users/
│   └── me/
│       ├── GET    /                                # 내 프로필 조회
│       ├── PATCH  /                                # 프로필 부분 수정
│       ├── GET    /borrows                         # 내 대출/반납 이력
│       ├── GET    /reservations                    # 내 예약 목록
│       └── GET    /book-requests                  # 내 희망 신청 목록
│
├── notifications/
│   ├── GET    /                                    # 알림 목록
│   └── PATCH  /{id}/read                           # 읽음 처리 (body: {"isRead": true})
│
├── notices/
│   ├── GET    /                                    # 공지 목록 (?page=&size=)
│   └── GET    /{id}                                # 공지 상세
│
├── external/
│   └── aladin/
│       └── GET    /books                           # 알라딘 도서 검색 (?query=&target=&page=&size=)
│
└── admin/                                          # ROLE_ADMIN 전용 namespace
    ├── stats/
    │   ├── GET    /overview                        # 대시보드 통계
    │   ├── GET    /borrows                         # 월별 대출량
    │   └── GET    /popular-books                   # 인기 도서 순위
    ├── books/
    │   ├── POST   /                                # 도서 신규 등록
    │   ├── PUT    /{bookId}                        # 도서 정보 수정
    │   ├── DELETE /{bookId}                        # 도서 삭제/비활성화 (R2)
    │   └── POST   /{bookId}/holds                  # 실물 재고 추가
    ├── book-holds/
    │   └── PATCH  /{holdId}/status                 # 도서 상태 변경 (LOST, DISCARDED)
    ├── borrows/
    │   ├── GET    /                                # 전체 대출 현황 (?status=&page=&size=)
    │   ├── POST   /{id}/notify-overdue             # 연체 알림 발송 (R2)
    │   └── PATCH  /{id}/extend/approve             # 연장 승인 (R2)
    ├── book-requests/
    │   ├── GET    /                                # 전체 희망 신청 목록 (?status=PENDING)
    │   └── PATCH  /{id}/status                     # 승인/거절 (body: {"status":"APPROVED"|"REJECTED"})
    ├── users/
    │   ├── GET    /                                # 전체 회원 목록 (?page=&size=) (R2)
    │   └── PATCH  /{id}/role                       # 권한 변경 (R2)
    ├── reservations/
    │   └── GET    /                                # 예약 현황 통계 (R2)
    ├── notices/
    │   ├── POST   /                                # 공지 등록 (R3)
    │   ├── PUT    /{id}                            # 공지 수정 (R3)
    │   └── DELETE /{id}                            # 공지 삭제 (R3)
    └── stats/
        ├── GET    /users/{id}                      # 사용자별 이용 통계 (R3)
        └── GET    /requests                        # 희망도서 신청 현황 (R3)
```

---

## 🗺 사용자 흐름(User Flow) 가이드

프론트엔드 작업자 및 팀원 간 소통을 위해 핵심 유저 흐름 시나리오를 정의합니다.

### 1. [인증/인가 사이클] (UI-AUT → UI-BKS)

사용자는 `UI-AUT-001`(로그인)에서 `POST /api/auth/login` 요청 시 서버가 `accessToken`을 **HttpOnly 쿠키**로 발급한다. 이후 브라우저의 모든 요청에 쿠키가 자동 포함되어 Spring Security가 `SecurityContext`를 구성하고, Thymeleaf `sec:authorize`가 서버 렌더링 시점에 메뉴를 제어한다.

### 2. [검색 → 대출/예약 사이클] (UI-BKS → UI-MYP)

메인 리스트(`UI-BKS-001`)에서 카드로 된 책을 누르면 상세창(`UI-BKS-002`)이 뜨며, 현재 재고(`copies`)가 1개 이상일 경우 **[대출] 버튼**이, 재고가 없다면 **[예약] 버튼**이 렌더링된다. 대출 후엔 마이페이지 대출 탭(`UI-MYP-002`)으로 유도해 결과를 확인시킨다.

### 3. [외부 도서 발굴(신청) 사이클] (UI-REQ → UI-ADM)

사용자가 메인 화면(`UI-BKS`)에서 원하는 책이 없을 경우 `UI-REQ-002`(도서 구매 신청)로 진입한다. 팝업 모달에서 `GET /api/v1/external/aladin/books?query=` 로 외부 도서를 검색하여 ISBN과 기본 정보를 자동 완성한 뒤 사유를 제출한다. 관리자가 `UI-ADM-005`(결재함)에서 `PATCH /api/v1/admin/book-requests/{id}` 로 승인/거절 처리한다.

---

## 📝 프론트–백엔드 협업 규칙

1. **계약 우선 (Contract-First)**: API 변경 시 본 문서(`frontend_specs.md`)를 먼저 수정하고 PR 리뷰 후 백엔드를 구현한다.
2. **뷰 라우트와 API 경로 분리**: 뷰 라우트(`/notices`)와 API(`/api/v1/notices`)를 동일하게 사용하지 않는다.
3. **동사 금지**: URL에 `return`, `extend`, `read`, `approve` 등 동사를 사용하지 않는다. 상태 변경은 `PATCH` + body로 표현한다.
4. **에러 응답 통일**: 모든 에러는 `{"code": "...", "message": "..."}` 형식을 따른다. 프론트는 `message` 필드를 그대로 사용자에게 노출한다.
5. **페이징 파라미터 통일**: `page`(0-based), `size`, `sort` 파라미터명을 전 API에서 동일하게 사용한다.

---

## 🎨 디자인 시스템 연동

모든 화면은 `docs/02_design/design_system.md`의 **Green Turtle Edition** 가이드를 따릅니다:

- **Primary Color**: `#00A760` (부기맨 초록색)
- **Card Border Radius**: `32px` (둥글둥글한 친근한 느낌)
- **Button Hover**: `translateY(-5px)` + 그림자 강조
- **Typography**: Noto Sans KR (기본), Pretendard (로고/제목)

