# 📘 부기맨(Boogieman) 도서 관리 시스템 API 명세서

> 모든 API는 `/api/v1` 경로를 Base URL로 상속받아 사용합니다.
> Auth 항목의 명시가 빈 곳(`-`)은 공개 오픈 자원, `User`/`Admin`의 경우 해당 토큰/세션이 필요합니다.
>
> **구현 상태 범례**: ✅ 구현완료 | 🔶 부분구현 | ⬜ 미구현
>
> ⚠️ **경로 참고**: 현재 구현된 실제 Base URL은 `/api` (버전 prefix 미적용) 입니다.
> 예시: `/auth/signup` → 실제 경로 `POST /api/auth/signup`

### 👤 신원 제어(Auth) 및 사용자 계정 관리(Users)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| POST | `/auth/signup` | 신규 회원 자격 가입 허가 (이메일·이름·비밀번호 Bean Validation 적용, HTTP 201) | - | ✅ |
| POST | `/auth/login` | 이메일 패스워드 검증 및 Access/Refresh Token 신규 발행 (5회 실패 시 계정 잠금, HTTP 401/403) | - | ✅ |
| POST | `/auth/logout` | 현재 로그인 Access Token Redis 블랙리스트 등록 (강제 만료, HTTP 204) | User | ✅ |
| POST | `/auth/refresh` | Refresh Token 교환하여 새 Access Token 재발급 (HTTP 200) | User | ✅ |
| GET | `/users/me` | 로그인된 주체의 개인 프로필 자원 열람 | User | ⬜ |
| PUT | `/users/me` | 프로필 갱신 업데이트 (아바타/이름 변경) | User | ⬜ |
| GET | `/users/me/borrows` | 본인 계정의 과거 및 현재 소장 대출 이력의 일괄 발송 | User | ⬜ |
| GET | `/users/me/requests` | 희망 도서 구매 추천내역의 상태/이력 점검 | User | ⬜ |
| GET | `/users/me/reservations` | 소속된 모든 "도서 예약 및 대기열의 상태와 만료일" 시퀀스 발송 | User | ⬜ |

### 📚 도서 검색 서비스 (Books/Book_Holds)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| GET | `/books` | 전체 서비스 도서 조회 (페이지네이션/가나다 정렬/도서명 필터 텍스트 파라미터 적용) | User | ✅ |
| GET | `/books/{bookId}` | 식별된 책의 전체 ISBN, 저술가, 이미지 주소 등 포함 자원 | User | ✅ |
| POST | `/admin/books` | [관리자용] 회사 구매 신간의 식별 메타 인덱스 신규 업로드 | Admin | ✅ |
| PUT | `/admin/books/{bookId}` | [관리자용] 기등록된 도서 오류(저술가, 표지, 내용누락) 등 강제 갱신 | Admin | ✅ |
| DELETE | `/admin/books/{bookId}`| [관리자용]기등록된 도서 정보 삭제/비활성화 (R2) | Admin | ⬜ |
| POST | `/admin/books/{bookId}/holds`| [관리자용] 하나의 도서에 대하여 `신규 실물(추가 구매)`이 도착했을 때 재고 +1 증가 분 추가 | Admin | ✅ |
| PATCH | `/admin/book-holds/{bookHoldId}/status`| [관리자용] 1개의 도서 실물이 복구가 어려울 때 '도서 분실' 혹은 '대출 폐기' 상태 전환 | Admin | ✅ |
| GET | `/books/popular` | 인기 도서 추천 (최근 대출 많은 도서 기준 정렬) (R2) | User | ⬜ |
| GET | `/books/personalized` | 개인화 추천 (사용자 대출 기록 기반 도서 추천) (R3) | User | ⬜ |

### 🌐 외부 연동 (External APIs)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| GET | `/external/kakao/books` | [Proxy] 카카오 API 기반 도서 검색 (희망 도서 신청 및 신규 등록 시 메타데이터 획득용) | User | ✅ |

### 📖 책 빌리기 및 반납 / 대출 자원 (Borrows)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| POST | `/borrows` | 보유 재고를 대상으로 신규 '대출 이력' 생성 실행 (1인 제한 방어 필요) | User | ⬜ |
| POST | `/borrows/{borrowId}/return` | 도서물 점유를 포기/반납 실행 통지 (본인 IDOR 검증 진행 통과) | User | ⬜ |
| POST | `/borrows/{borrowId}/extend` | 대출 14일 연장 권한 청구 (단 1회, 후순위자 없을 시 인정됨) | User | ⬜ |
| GET | `/admin/borrows` | [관리자용] 현재 시스템 대출자 확인 및 미납 명단 조회 | Admin | ⬜ |
| POST | `/admin/borrows/{borrowId}/notify-overdue`| [관리자용] 연체 사용자 대상 알림 수동 발송 (R2) | Admin | ⬜ |
| PATCH | `/admin/borrows/{borrowId}/extend/approve`| [관리자용] 사용자 연장 건에 대한 심의 평가/승인 관리 (R2) | Admin | ⬜ |

### 📅 재고 예약 선점 등록 (Reservations)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| POST | `/reservations` | 단 권도 남지 않은 도서에 '대기 알람 예약(최대 2권)' 선점 | User | ⬜ |
| DELETE | `/reservations/{reservationId}` | 대기 차례가 필요 없을 때 예약 목록 청약 파기 철회 | User | ⬜ |
| GET | `/admin/reservations` | [관리자용] 어떤 도서가 예약 누적이 많은지 순위별 차세대 통계 | Admin | ⬜ |

### 🛒 희망 도서 신청망 (Book-Requests)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| POST | `/book-requests` | 사용자가 도서의 정보(사유, ISBN 등)를 적어 문서 제출 | User | ⬜ |
| GET | `/book-requests` | (관리자는 모두/사용자는 자신이 올린) 내역의 리스트 업 | User/Admin| ⬜ |
| PATCH | `/admin/book-requests/{requestId}/status`| [관리자용] 구매 결제 통과/실패 도장에 대한 패치 업데이트 | Admin | ⬜ |

### 🔔 서비스 수발신 단말 알림 (Notifications)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| GET | `/notifications` | 어플리케이션 안의 내가 받은 (문자제외/플랫폼 내부) 수신 알람 | User | ⬜ |
| PATCH | `/notifications/{notificationId}/read`| 신규 표시 (1 마크) 지우기 및 읽음 강제 지정 처방 | User | ⬜ |

### 📢 시스템 공지사항 및 운영 (Notices)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| GET | `/notices` | 시스템 점검, 일정 안내, 이용 가이드 등 전체 공지 조회 (R3) | - | ⬜ |
| GET | `/notices/{noticeId}` | 특정 공지사항 상세 조회 (R3) | - | ⬜ |
| POST | `/admin/notices` | [관리자용] 시스템 신규 공지사항 등록 (R3) | Admin | ⬜ |
| PUT | `/admin/notices/{noticeId}` | [관리자용] 공지사항 내용 수정 (R3) | Admin | ⬜ |
| DELETE| `/admin/notices/{noticeId}`| [관리자용] 불필요한 공지사항 삭제 (R3) | Admin | ⬜ |

### ⚙️ 시스템 관리자 통합 조율 도구 (Admin Tools)
| HTTP | URL Endpoint | 기능 설명 요약 | 요구 보호수준 (Auth) | 구현 여부 |
|---|---|---|---|---|
| GET | `/admin/users` | 플랫폼의 전체 사용자(회원) 목록 조회 (R2) | Admin | ⬜ |
| PATCH | `/admin/users/{userId}/role`| 관리자 레벨로 임직원을 진급/격하 권한 적용 (User ↔ Admin) (R2) | Admin | ⬜ |
| PATCH | `/admin/users/{userId}/status`| [관리자용] 사원 비활성화/정보 변경 통합 관리 (R3) | Admin | ⬜ |
| GET | `/admin/stats/borrows` | 월별 대출량 등 통계 리포트 조회 (R2) | Admin | ⬜ |
| GET | `/admin/stats/popular-books` | 사용자 타깃 인기 도서, 최대 대출 통계 순위 조회 (R2) | Admin | ⬜ |
| GET | `/admin/stats/requests` | 기간별 희망도서 신청 현황 통계 추적 수급 목적 (R3) | Admin | ⬜ |
| GET | `/admin/stats/users/{userId}`| 사용자 개별의 이용 통계 및 각종 연체/반납 내역 추적 (R3) | Admin | ⬜ |

---

## 📌 상세 API 명세: 외부 연동 도서 검색 (Kakao API Proxy)

### `GET /api/v1/external/kakao/books`
사용자의 희망 도서 신청이나 관리자의 신규 도서 등록 시, 정확한 도서 정보(ISBN, 표지, 저자 등)를 자동 완성하기 위해 제공되는 검색 API입니다. (백엔드에서 Kakao Open API를 대리 호출하여 결과를 반환합니다.)

#### Request Parameters (Query String)
| 파라미터명 | 타입 | 필수여부 | 설명 | 기본값 |
|---|---|---|---|---|
| `query` | String | **O** | 검색을 원하는 질의어 (도서명, ISBN 등) | - |
| `sort` | String | X | 정렬 방식: `accuracy`(정확도순), `latest`(발간일순) | `accuracy` |
| `page` | Integer | X | 결과 페이지 번호 (1~50) | `1` |
| `size` | Integer | X | 한 페이지에 보여질 문서 수 (1~50) | `10` |
| `target` | String | X | 검색 필드 제한 (`title`, `isbn`, `publisher`, `person`) | - |

#### Response Format (HTTP 200 OK)
```json
{
  "meta": {
    "total_count": 10,
    "pageable_count": 9,
    "is_end": true
  },
  "documents": [
    {
      "title": "미움받을 용기",
      "contents": "인간은 변할 수 있고, 누구나 행복해 질 수 있다...",
      "url": "[https://search.daum.net/search](https://search.daum.net/search)?...",
      "isbn": "8996991341 9788996991342",
      "datetime": "2014-11-17T00:00:00.000+09:00",
      "authors": ["기시미 이치로", "고가 후미타케"],
      "publisher": "인플루엔셜",
      "translators": ["전경아"],
      "price": 14900,
      "sale_price": 13410,
      "thumbnail": "[https://search1.kakaocdn.net/thumb/](https://search1.kakaocdn.net/thumb/)...",
      "status": "정상판매"
    }
  ]
}