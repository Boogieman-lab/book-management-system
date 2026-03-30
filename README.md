# 📚 부기맨(Boogieman) | 도서 관리 시스템

<p align="center">
  <img src="src/main/resources/static/favicon.svg" width="200" alt="Boogieman Logo"/>
</p>

## 📖 프로젝트 개요

사내 복지로 **희망 도서를 신청하면 회사에서 구매 후 대출할 수 있는 제도**가 운영되고 있지만,
현재는 별도의 관리 시스템이 없어 다음과 같은 불편함이 존재합니다.

* 이미 보유한 도서를 다시 신청하는 중복 발생
* 누가 해당 도서를 대출 중인지 확인 불가
* 도서 신청 및 대출 현황 관리의 비효율성

이를 해결하기 위해 **도서 관리 시스템, 부기맨(Boogieman)** 을 기획했습니다.
부기맨은 귀여운 이름처럼 친근하게, 구성원 누구나 쉽고 빠르게 **희망도서 신청**과 **도서 대출/반납 관리**를 할 수 있도록 지원합니다.

---

## 👻 이름의 의미 – "부기맨(Boogieman)"

* 원래 영어의 **Bogeyman(부기맨)** 은 아이들을 무서워하게 만드는 상상의 괴물을 뜻합니다.
* 하지만 이 프로젝트에서는 "책(Book)"에서 착안하여 **책을 지켜주는 귀여운 부기맨** 으로 재해석했습니다.
* 이제 부기맨은 괴물이 아니라, **사내 도서 복지를 지켜주는 든든한 관리자**입니다. 📖✨

---

## 👨‍👩‍👧‍👦 사용자

* **사내 구성원**: 희망 도서 신청, 도서 검색 및 대출/반납 기능 사용
* **관리자**: 신청 도서 관리, 도서 현황 관리, 대출 기록 조회

---

## 🌏 사이트

(배포 URL 예정)

---

## ⏰ 개발 기간

* 2024.07 \~ (진행 중)

---

## ⚙️ 기술 스택

### Frontend

* HTML / CSS / JavaScript
* Thymeleaf (서버사이드 렌더링)
* Aladin Open API (도서 검색 연동)

### Backend

* Java 17 / Spring Boot 3.3.2 / JPA / QueryDSL
* MariaDB 11.3
* Redis 7
* AWS (EC2, S3, RDS)

---

## 🤓 기획 및 설계
* 🛠 [요구사항 정의서](https://github.com/Boogieman-lab/book-management-system/tree/main/docs/01_requirements/requirements.md)
* 🛠 [기능 명세서](https://github.com/Boogieman-lab/book-management-system/tree/main/docs/01_requirements/functional_spec.md)
* 📑 [API 명세서](https://github.com/Boogieman-lab/book-management-system//tree/main/docs/02_design/api_spec.md)
* 💾 [DB 명세서](https://github.com/Boogieman-lab/book-management-system/tree/main/docs/02_design/architecture/erd.md)
* 👀 [사용자 디자인 미리보기](https://boogieman-lab.github.io/book-management-system/src/main/resources/static/design/index.html)
---

## ✅ 구현 완료 기능 (MVP1)

### 인증 / 사용자

* 회원가입 · 로그인 · 로그아웃 (JWT 쿠키 기반)
* 카카오 소셜 로그인 (OAuth 2.0)
* AccessToken / RefreshToken 재발급
* 마이페이지 – 프로필 조회 및 수정

### 도서

* 도서 목록 조회 (페이지네이션 · 검색)
* 도서 상세 조회
* ISBN 중복 확인
* 알라딘 Open API 연동 도서 검색 (제목 · 저자 · 출판사 · 키워드)

### 대출 / 반납

* 도서 대출
* 도서 반납
* 내 대출 내역 조회 (상태별 필터링)

### 예약

* 예약 등록 · 취소
* 특정 도서 예약 대기 목록 조회
* 내 예약 목록 조회

### 희망 도서 신청

* 희망 도서 신청 · 취소
* 내 신청 목록 조회

### 알림

* 알림 목록 조회 (읽음 여부 필터링)
* 단일 알림 읽음 처리
* 전체 알림 읽음 처리

### 관리자 (Admin)

* 대시보드 – 통계 및 현황 요약
* 도서 센터 – 신규 도서 등록, 도서 정보 수정, 실물(보유본) 추가
* 보유본 상태 변경 (분실 등)
* 대출 / 반납 관제 – 전체 대출 현황 조회
* 희망도서 결재함 – 신청 승인 / 거절
* 임직원 권한(Role) 관리

---

## 🚧 구현 중인 기능

* 관리자 화면 UI (도서 센터 · 대출 관제 · 희망도서 결재 · 권한 관리)
* 도서 예약 → 대출 전환 자동화 흐름

---

## 📋 구현 예정 기능

* 공지사항 등록 · 수정 · 삭제 (관리자)
* 도서 반납 기한 초과 알림 (스케줄러)
* 예약 순번 도래 시 알림 자동 발송
* 부서 관리 화면 (관리자)
* 도서 리뷰 / 별점
* 도서 통계 리포트 (인기 도서, 대출 현황 등)
* 모바일 반응형 최적화

---

## 👩‍💻 팀원

* Developer 👩‍💻 [김수현](https://github.com/kim-soohyeon)
* Developer 👨‍💻 [이승원](https://github.com/seungwontech)
