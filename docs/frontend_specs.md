# 🖥️ 부기맨(Boogieman) 프론트엔드 화면 목록 및 상태 관리 문서

본 문서는 부기맨 MVP 시스템의 프론트엔드 라우팅 및 화면 단위별 진행률(Status Tracker)을 관리하기 위한 문서입니다. 모든 뷰(View)는 컴포넌트 단위로 식별 ID가 부여되며, 백엔드의 REST API와 1:1매핑을 추적합니다.

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

## 📊 부기맨 프론트엔드 화면 목록 및 상태 관리보드 (Screen List Board)

| 화면 ID | 분류(Depth 1) | 화면명(Depth 2) | 추정 URL (Route) | 연동 API Endpoint (Method) | 진행 상태 | QA 상태 |
| :--- | :--- | :--- | :--- | :--- | :---: | :---: |
| **UI-COM-001** | **공통** | 메인 레이아웃 및 헤더/푸터 | `(All)` | ❌ API 없음 (토큰 유무에 따른 헤더 스위칭 JS만 존재) | `Done` | `Pending` |
| **UI-AUT-001** | **인증** | 로그인 메인 화면 | `/user/auth/login` | `POST /api/auth/login`<br>`GET (Kakao OAuth 리다이렉트)` | `Done` | `Pending` |
| **UI-AUT-002** | **인증** | 신규 회원가입 폼 | `/user/auth/signup` | `POST /api/auth/signup` | `Done` | `Pending` |
| **UI-BKS-001** | **도서** | 도서 통합 검색 및 전체 목록 | `/` (또는 `/books`) | `GET /api/v1/books` | `Not Started` | `-` |
| **UI-BKS-002** | **도서** | 도서 상세 조회 및 대출/예약 창 | `/books/{id}` (혹은 모달) | `GET /api/v1/books/{bookId}`<br>`POST /api/v1/borrows`<br>`POST /api/v1/reservations` | `Not Started` | `-` |
| **UI-REQ-001** | **신청** | 희망 도서 카카오 검색 모달 | `팝업(Modal)` | `GET /api/v1/external/kakao/books` | `Not Started` | `-` |
| **UI-REQ-002** | **신청** | 외부 도서 반입(희망) 신청 폼 | `/book-requests` | `POST /api/v1/book-requests` | `Not Started` | `-` |
| **UI-MYP-001** | **마이페이지** | 개인 프로필 통합 홈 & 수정 | `/user/mypage` | `GET /api/v1/users/me`<br>`PUT /api/v1/users/me` | `Not Started` | `-` |
| **UI-MYP-002** | **마이페이지** | 대출/반납 이력 탭 | `/user/mypage?tab=borrows` | `GET /api/v1/users/me/borrows`<br>`POST /api/v1/borrows/{id}/return`<br>`POST /borrows/.../extend` | `Not Started` | `-` |
| **UI-MYP-003** | **마이페이지** | 현재 예약 및 대기열 탭 | `/user/mypage?tab=reserv` | `GET /api/v1/users/me/reservations`<br>`DELETE /api/v1/reservations/{id}` | `Not Started` | `-` |
| **UI-MYP-004** | **마이페이지** | 내가 쓴 희망 도서 신청 진행 탭 | `/user/mypage?tab=requests`| `GET /api/v1/users/me/requests` | `Not Started` | `-` |
| **UI-MYP-005** | **마이페이지** | 인앱 수신 알림함 탭 | `/user/mypage?tab=noti` | `GET /api/v1/notifications`<br>`PATCH /api/v1/notifications/{id}/read` | `Not Started` | `-` |
| **UI-INF-001** | **정보** | About 프로젝트 소개 (정적) | `/about` | ❌ API 없음 | `Not Started` | `-` |
| **UI-INF-002** | **정보** | 이용안내 및 시스템 공지사항 | `/notices` | `GET /notices` / `GET /notices/{id}` | `Not Started` | `-` |
| **UI-ADM-001** | **관리자** | 어드민 대시보드 (통계 메인) | `/admin/dashboard` | `GET /api/v1/admin/stats/...` 다수 통계 API 묶음 호출 | `Not Started` | `-` |
| **UI-ADM-002** | **관리자** | 도서 센터 (신규 등록 및 실물 추가) | `/admin/books` | `POST /api/v1/admin/books`<br>`POST /api/v1/admin/books/{id}/holds` | `Not Started` | `-` |
| **UI-ADM-003** | **관리자** | 대출망 통제 (연체자 현황) | `/admin/borrows` | `GET /api/v1/admin/borrows` | `Not Started` | `-` |
| **UI-ADM-004** | **관리자** | 부기맨 임직원 권한 롤 관리 | `/admin/users` | `GET /api/v1/admin/users`<br>`PATCH /api/v1/admin/users/{id}/...` | `Not Started` | `-` |
| **UI-ADM-005** | **관리자** | 희망 도서 결재함 (승인/거절) | `/admin/requests` | `GET /api/v1/book-requests` (`ADMIN` 권한으로 전체 나열)<br>`PATCH .../status` | `Not Started` | `-` |

---

## 🗺 사용자 흐름(User Flow) 가이드

프론트엔드 작업자 및 팀원 간 소통을 위해 핵심 유저 흐름 시나리오를 정의합니다.

1. **[인증/인가 사이클] (UI-AUT ➔ UI-BKS)**
   사용자는 `UI-AUT-001`(로그인)에서 토큰(`accessToken`)을 받으면 백그라운드의 전역 `api.js`가 이를 인터셉트하여, 추후 모든 API 요청과 리다이렉션을 `UI-BKS-001`(메인 홈)으로 안내합니다.
2. **[검색 ➔ 대출/예약 사이클] (UI-BKS ➔ UI-MYP)**
   메인 리스트(`UI-BKS-001`)에서 카드로 된 책을 누르면 상세창(`UI-BKS-002`)이 뜨며, 여기서 현재 재고(`BookHolds`)가 1명 이상일 경우 **주황색 [대출] 버튼**이, 재고가 없다면 1,2순위를 다투는 **회색 [예약] 버튼**이 렌더링 됩니다. 행동 후엔 자동으로 마이페이지 대출 탭(`UI-MYP-002`)으로 유도해 결과를 확인시킵니다.
3. **[외부 도서 발굴(신청) 사이클] (UI-REQ ➔ UI-ADM)**
   사용자가 메인 화면(`UI-BKS`)에서 쿼리 검색 후 원하는 책이 시스템에 없다고 느끼면, 즉시 `UI-REQ-002`(도서 구매 신청)로 진입합니다. 여기서 팝업 모달창으로 **카카오 API 검색 UI**를 띄워 ISBN과 기본 정보들을 자동 완성(Auto-fill)시킨 후 사유를 제출합니다. 이 데이터는 나중에 관리자가 `UI-ADM-005`(결재함)에 진입해 사내 공금으로 구매 후 결재를 승인(Approved) 처리합니다.
