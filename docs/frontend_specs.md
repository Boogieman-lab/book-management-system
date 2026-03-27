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
| 3rd-party 추상화 | 내부 구현 숨김 | `/books/search?source=external` (kakao 직접 노출 금지) |
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

| 화면 ID          | 분류(Depth 1) | 화면명(Depth 2)          | 추정 URL (Route)              | 연동 API Endpoint (Method) | 진행 상태 | QA 상태 |
|:---------------| :--- |:----------------------|:----------------------------| :--- | :---: | :---: |
| **UI-COM-001** | **공통** | 메인 레이아웃 및 헤더/푸터       | `(All)`                     | ❌ API 없음 (Spring Security `sec:authorize` 기반 서버사이드 메뉴 렌더링) | `Done` | `Pending` |
| **UI-AUT-001** | **인증** | 로그인 메인 화면             | `/user/auth/login`          | `POST /api/v1/auth/login`<br>`GET /api/v1/auth/oauth/kakao` | `Done` | `Pending` |
| **UI-AUT-002** | **인증** | 신규 회원가입 폼             | `/user/auth/signup`         | `POST /api/v1/auth/signup` | `Done` | `Pending` |
| **UI-BKS-001** | **도서** | 도서 통합 검색 및 전체 목록      | `/books`                    | `GET /api/v1/books?q=&category=&page=&size=` | `Done` | `Pending` |
| **UI-BKS-002** | **도서** | 도서 상세 조회 및 대출/예약 창    | `/books/{bookId}`q          | `GET /api/v1/books/{bookId}`<br>`POST /api/v1/borrows`<br>`POST /api/v1/reservations` | `Done` | `Pending` |
| **UI-REQ-001** | **신청** | 희망 도서 외부 검색 모달        | `팝업(Modal)`                 | `GET /api/v1/books/search?source=external&q=` | `Done` | `Pending` |
| **UI-REQ-002** | **신청** | 외부 도서 반입(희망) 신청 폼     | `/book-requests`            | `POST /api/v1/book-requests` | `Done` | `Pending` |
| **UI-REQ-003** | **신청** | 희망 도서 신청 내역           | `/book-requests`            | `GET /api/v1/book-requests` | `Done` | `Pending` |
| **UI-MYP-001** | **마이페이지** | 개인 프로필 통합 홈 & 수정      | `/user/mypage`              | `GET /api/v1/users/me`<br>`PATCH /api/v1/users/me` | `Done` | `Pending` |
| **UI-MYP-002** | **마이페이지** | 대출/반납 이력 탭            | `/user/mypage?tab=borrows`  | `GET /api/v1/users/me/borrows`<br>`PATCH /api/v1/borrows/{id}` + `{"status":"RETURNED"}`<br>`POST /api/v1/borrows/{id}/extensions` | `Done` | `Pending` |
| **UI-MYP-003** | **마이페이지** | 현재 예약 및 대기열 탭         | `/user/mypage?tab=reserv`   | `GET /api/v1/users/me/reservations`<br>`DELETE /api/v1/reservations/{id}` | `Done` | `Pending` |
| **UI-MYP-005** | **마이페이지** | 인앱 수신 알림함 탭           | `/user/mypage?tab=noti`     | `GET /api/v1/notifications`<br>`PATCH /api/v1/notifications/{id}` + `{"isRead":true}` | `Done` | `Pending` |
| **UI-INF-001** | **정보** | About 프로젝트 소개 (정적)    | `/about`                    | ❌ API 없음 | `Done` | `-` |
| **UI-INF-002** | **정보** | 이용안내 및 시스템 공지사항       | `/notices`                  | `GET /api/v1/notices?page=&size=`<br>`GET /api/v1/notices/{id}` | `Not Started` | `-` |
| **UI-ADM-001** | **관리자** | 어드민 대시보드 (통계 메인)      | `/admin/dashboard`          | `GET /api/v1/admin/stats/overview` | `Not Started` | `-` |
| **UI-ADM-002** | **관리자** | 도서 센터 (신규 등록 및 실물 추가) | `/admin/books`              | `POST /api/v1/admin/books`<br>`POST /api/v1/admin/books/{bookId}/copies` | `Not Started` | `-` |
| **UI-ADM-003** | **관리자** | 대출망 통제 (연체자 현황)       | `/admin/borrows`            | `GET /api/v1/admin/borrows?status=&page=&size=` | `Not Started` | `-` |
| **UI-ADM-004** | **관리자** | 부기맨 임직원 권한 롤 관리       | `/admin/users`              | `GET /api/v1/admin/users?page=&size=`<br>`PATCH /api/v1/admin/users/{id}` + `{"role":"ADMIN"}` 또는 `{"status":"LOCKED"}` | `Not Started` | `-` |
| **UI-ADM-005** | **관리자** | 희망 도서 결재함 (승인/거절)     | `/admin/requests`           | `GET /api/v1/admin/book-requests?status=&page=`<br>`PATCH /api/v1/admin/book-requests/{id}` + `{"status":"APPROVED"}` | `Not Started` | `-` |

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
│   └── GET    /search?source=external&q=          # 외부 도서 검색 (3rd-party 추상화)
│
├── borrows/
│   ├── POST   /                                    # 대출 신청
│   ├── PATCH  /{id}                                # 상태 변경 (body: {"status":"RETURNED"})
│   └── POST   /{id}/extensions                    # 반납 기한 연장 신청
│
├── reservations/
│   ├── POST   /                                    # 예약 신청
│   └── DELETE /{id}                                # 예약 취소
│
├── book-requests/
│   └── POST   /                                    # 희망 도서 신청
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
│   └── PATCH  /{id}                                # 읽음 처리 (body: {"isRead": true})
│
├── notices/
│   ├── GET    /                                    # 공지 목록 (?page=&size=)
│   └── GET    /{id}                                # 공지 상세
│
└── admin/                                          # ROLE_ADMIN 전용 namespace
    ├── stats/
    │   └── GET    /overview                        # 대시보드 통계 (대출 수, 연체 수 등)
    ├── books/
    │   ├── POST   /                                # 도서 신규 등록
    │   └── POST   /{bookId}/copies                # 실물 재고(Copy) 추가
    ├── borrows/
    │   └── GET    /                                # 전체 대출 현황 (?status=OVERDUE&page=&size=)
    ├── users/
    │   ├── GET    /                                # 전체 회원 목록 (?page=&size=)
    │   └── PATCH  /{id}                            # 권한·상태 변경 (body: {"role"} 또는 {"status"})
    ├── book-requests/
    │   ├── GET    /                                # 전체 희망 신청 목록 (?status=PENDING)
    │   └── PATCH  /{id}                            # 승인/거절 (body: {"status":"APPROVED"|"REJECTED"})
    └── notices/
        └── POST   /                                # 공지 등록
```

---

## 🗺 사용자 흐름(User Flow) 가이드

프론트엔드 작업자 및 팀원 간 소통을 위해 핵심 유저 흐름 시나리오를 정의합니다.

1. **[인증/인가 사이클] (UI-AUT → UI-BKS)**
   사용자는 `UI-AUT-001`(로그인)에서 `POST /api/v1/auth/login` 요청 시 서버가 `accessToken`을 **HttpOnly 쿠키**로 발급한다. 이후 브라우저의 모든 요청에 쿠키가 자동 포함되어 Spring Security가 `SecurityContext`를 구성하고, Thymeleaf `sec:authorize`가 서버 렌더링 시점에 메뉴를 제어한다.

2. **[검색 → 대출/예약 사이클] (UI-BKS → UI-MYP)**
   메인 리스트(`UI-BKS-001`)에서 카드로 된 책을 누르면 상세창(`UI-BKS-002`)이 뜨며, 현재 재고(`copies`)가 1개 이상일 경우 **[대출] 버튼**이, 재고가 없다면 **[예약] 버튼**이 렌더링된다. 대출 후엔 마이페이지 대출 탭(`UI-MYP-002`)으로 유도해 결과를 확인시킨다.

3. **[외부 도서 발굴(신청) 사이클] (UI-REQ → UI-ADM)**
   사용자가 메인 화면(`UI-BKS`)에서 원하는 책이 없을 경우 `UI-REQ-002`(도서 구매 신청)로 진입한다. 팝업 모달에서 `GET /api/v1/books/search?source=external&q=` 로 외부 도서를 검색하여 ISBN과 기본 정보를 자동 완성한 뒤 사유를 제출한다. 관리자가 `UI-ADM-005`(결재함)에서 `PATCH /api/v1/admin/book-requests/{id}` 로 승인/거절 처리한다.

---

## 📝 프론트–백엔드 협업 규칙

1. **계약 우선 (Contract-First)**: API 변경 시 본 문서(`frontend_specs.md`)를 먼저 수정하고 PR 리뷰 후 백엔드를 구현한다.
2. **뷰 라우트와 API 경로 분리**: 뷰 라우트(`/notices`)와 API(`/api/v1/notices`)를 동일하게 사용하지 않는다.
3. **동사 금지**: URL에 `return`, `extend`, `read`, `approve` 등 동사를 사용하지 않는다. 상태 변경은 `PATCH` + body로 표현한다.
4. **에러 응답 통일**: 모든 에러는 `{"code": "...", "message": "..."}` 형식을 따른다. 프론트는 `message` 필드를 그대로 사용자에게 노출한다.
5. **페이징 파라미터 통일**: `page`(0-based), `size`, `sort` 파라미터명을 전 API에서 동일하게 사용한다.
