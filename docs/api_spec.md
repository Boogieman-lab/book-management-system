# 📘 부기맨(Boogieman) 도서 관리 시스템 API 명세서

> 모든 API는 `/api/v1` 경로를 Base URL로 상속받아 사용합니다.
> Auth 항목의 명시가 빈 곳(`-`)은 공개 오픈 자원, `User`/`Admin`의 경우 해당 토큰/세션이 필요합니다.

### 👤 신원 제어(Auth) 및 사용자 계정 관리(Users)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) |
|---|---|---|---|
| POST | `/auth/signup` | 신규 회원 자격 가입 허가 (플랫폼 참여 신청) | - |
| POST | `/auth/login` | 이메일 패스워드 검증 및, Access 토큰/Refresh 토큰 신규 발행 | - |
| POST | `/auth/logout` | 현재 로그인 토큰 파괴 (강제 만료) | User |
| POST | `/auth/refresh` | 리프레시 토큰을 교환하여 새로운 Access Token으로 서비스 지속 | User |
| GET | `/users/me` | 로그인된 주체의 개인 프로필 자원 열람 | User |
| PUT | `/users/me` | 프로필 갱신 업데이트 (아바타/이름 변경) | User |
| GET | `/users/me/borrows` | 본인 계정의 과거 및 현재 소장 대출 이력의 일괄 발송 | User |
| GET | `/users/me/requests` | 희망 도서 구매 추천내역의 상태/이력 점검 | User |
| GET | `/users/me/reservations` | 소속된 모든 "도서 예약 및 대기열의 상태와 만료일" 시퀀스 발송 | User |

### 📚 도서 검색 서비스 (Books/Book_Holds)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) |
|---|---|---|---|
| GET | `/books` | 전체 서비스 도서 조회 (페이지네이션/가나다 정렬/도서명 필터 텍스트 파라미터 적용) | User |
| GET | `/books/{bookId}` | 식별된 책의 전체 ISBN, 저술가, 이미지 주소 등 포함 자원 | User |
| POST | `/admin/books` | [관리자용] 회사 구매 신간의 식별 메타 인덱스 신규 업로드 | Admin |
| PUT | `/admin/books/{bookId}` | [관리자용] 기등록된 도서 오류(저술가, 표지, 내용누락) 등 강제 갱신 | Admin |
| POST | `/admin/books/{bookId}/holds`| [관리자용] 하나의 도서에 대하여 `신규 실물(추가 구매)`이 도착했을 때 재고 +1 증가 분 추가 | Admin |
| PATCH | `/admin/book-holds/{bookHoldId}/status`| [관리자용] 1개의 도서 실물이 복구가 어려울 때 '도서 분실' 혹은 '대출 폐기' 상태 전환 | Admin |

### 📖 책 빌리기 및 반납 / 대출 자원 (Borrows)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) |
|---|---|---|---|
| POST | `/borrows` | 보유 재고를 대상으로 신규 '대출 이력' 생성 실행 (1인 제한 방어 필요) | User |
| POST | `/borrows/{borrowId}/return` | 도서물 점유를 포기/반납 실행 통지 (본인 IDOR 검증 진행 통과) | User |
| POST | `/borrows/{borrowId}/extend` | 대출 14일 연장 권한 청구 (단 1회, 후순위자 없을 시 인정됨) | User |
| GET | `/admin/borrows` | [관리자용] 현재 시스템 연체 명단, 미납 명단 일괄 취합 조회기능 | Admin |
| PATCH | `/admin/borrows/{borrowId}/extend/approve`| [관리자용] 사용자 연장 건에 대한 심의 평가/허용 여부 저장 | Admin |

### 📅 재고 예약 선점 등록 (Reservations)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) |
|---|---|---|---|
| POST | `/reservations` | 단 권도 남지 않은 도서에 '대기 알람 예약(최대 2권)' 선점 | User |
| DELETE | `/reservations/{reservationId}` | 대기 차례가 필요 없을 때 예약 목록 청약 파기 철회 | User |
| GET | `/admin/reservations` | [관리자용] 어떤 도서가 예약 누적이 많은지 순위별 차세대 통계 | Admin |

### 🛒 희망 도서 신청망 (Book-Requests)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) |
|---|---|---|---|
| POST | `/book-requests` | 사용자가 도서의 정보(사유 등)를 적어 문서 제출 | User |
| GET | `/book-requests` | (관리자는 모두/사용자는 자신이 올린) 내역의 리스트 업 | User/Admin|
| PATCH | `/admin/book-requests/{requestId}/status`| [관리자용] 구매 결제 통과/실패 도장에 대한 패치 업데이트 | Admin |

### 🔔 서비스 수발신 단말 알림 (Notifications)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) |
|---|---|---|---|
| GET | `/notifications` | 어플리케이션 안의 내가 받은 (문자제외/플랫폼 내부) 수신 알람 | User |
| PATCH | `/notifications/{notificationId}/read`| 신규 표시 (1 마크) 지우기 및 읽음 강제 지정 처방 | User |

### ⚙️ 시스템 관리자 통합 조율 도구 (Admin Tools)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) |
|---|---|---|---|
| GET | `/admin/users` | 플랫폼의 전체 사용자 목록 조회 | Admin |
| PATCH | `/admin/users/{userId}/role`| 슈퍼어드민 기능, 관리자 레벨로 임직원을 진급/격하 권한 적용 | Admin |
| GET | `/admin/stats/borrows` | 월간 활성 사용자 조회 / 도서 별 증가 데이터 그래프 통계 수급 목적 | Admin |
| GET | `/admin/stats/popular-books` | 사용자 타깃 인기 도서, 최대 대출 통계의 반환 추출 기능 | Admin |
