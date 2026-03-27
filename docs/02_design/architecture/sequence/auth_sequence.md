# 🔐 가입 및 로그인 시퀀스 다이어그램 (Auth Sequence Diagram)

이 문서는 사용자가 서비스에 진입하기 위해 거치는 자체 회원가입, 이메일/비밀번호 로그인(계정 잠금 포함), 카카오 OAuth 로그인, 토큰 갱신/로그아웃까지의 핵심 흐름을 설명합니다.

---

## 1. 이메일 회원가입 (`POST /api/auth/signup`) ✅

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API (Spring Boot)
    participant DB as MariaDB

    User->>Client: 이름, 이메일, 비밀번호 입력 후 가입 요청
    Client->>Server: POST /api/auth/signup { username, email, password, role }

    Server->>Server: Bean Validation 검사<br>(@NotBlank, @Email, @Size(min=8))

    alt 유효성 검사 실패
        Server-->>Client: HTTP 400 BAD_REQUEST (필드 오류 메시지)
    else 유효성 통과
        Server->>DB: SELECT email FROM users WHERE email = ?
        alt 이메일 중복
            DB-->>Server: 기존 레코드 존재
            Server-->>Client: HTTP 409 CONFLICT (USER_ALREADY_EXISTS)
        else 신규 이메일
            DB-->>Server: 레코드 없음
            Server->>Server: BCrypt 비밀번호 해싱
            Server->>DB: INSERT INTO users (email, password, name, role, login_fail_count, is_locked)
            DB-->>Server: 저장 완료
            Server-->>Client: HTTP 201 CREATED
        end
    end

    Client-->>User: 가입 완료 안내 후 로그인 페이지로 이동
```

---

## 2. 이메일 로그인 (`POST /api/auth/login`) ✅

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API (Spring Boot)
    participant DB as MariaDB
    participant Redis as Redis (Token Store)

    User->>Client: 이메일, 비밀번호 입력
    Client->>Server: POST /api/auth/login { email, password }

    Server->>DB: SELECT * FROM users WHERE email = ?

    alt 사용자 없음
        DB-->>Server: 레코드 없음
        Server-->>Client: HTTP 401 UNAUTHORIZED (INVALID_CREDENTIALS)
    else 사용자 존재
        DB-->>Server: users 레코드 반환

        alt 계정 잠금 상태 (is_locked = true)
            Server-->>Client: HTTP 403 FORBIDDEN (ACCOUNT_LOCKED)
        else 정상 계정
            Server->>Server: BCrypt 비밀번호 대조

            alt 비밀번호 불일치
                Server->>DB: UPDATE users SET login_fail_count = login_fail_count + 1
                Note over Server,DB: login_fail_count >= 5 이면 is_locked = true 함께 갱신
                DB-->>Server: 업데이트 완료
                Server-->>Client: HTTP 401 UNAUTHORIZED (INVALID_CREDENTIALS)
            else 비밀번호 일치
                Server->>DB: UPDATE users SET login_fail_count = 0, is_locked = false
                DB-->>Server: 초기화 완료
                Server->>Server: JWT Access Token (180s) + Refresh Token (300s) 생성
                Server->>Redis: SET token:{email} = RefreshToken (TTL: 300s)
                Redis-->>Server: 저장 완료
                Server-->>Client: HTTP 200 OK { accessToken, refreshToken, email, name }
            end
        end
    end

    Client-->>User: 메인 홈으로 이동 (토큰 저장)
```

---

## 3. 카카오 OAuth 로그인 (`GET /api/oauth/code/kakao/callback`) ✅

```mermaid
sequenceDiagram
    actor User as 사용자
    participant Client as Frontend
    participant Server as Backend API (Spring Boot)
    participant Kakao as Kakao OAuth API
    participant DB as MariaDB
    participant Redis as Redis

    User->>Client: "카카오로 로그인" 버튼 클릭
    Client->>Kakao: 카카오 외부 인증 페이지 요청
    Kakao-->>Client: Authorization Code 발급 및 리다이렉트

    Client->>Server: GET /api/oauth/code/kakao/callback?code={authCode}
    Server->>Kakao: Authorization Code로 Access Token 교환
    Kakao-->>Server: Kakao Access Token 반환
    Server->>Kakao: Kakao Access Token으로 사용자 프로필 조회
    Kakao-->>Server: { kakaoId, email, nickname, profileImage }

    Server->>DB: SELECT * FROM users WHERE email = 'kakao:{kakaoId}'

    alt 신규 사용자
        DB-->>Server: 레코드 없음
        Server->>DB: INSERT INTO users (email, name, role, login_fail_count, is_locked)
        DB-->>Server: 신규 등록 완료
    else 기존 사용자
        DB-->>Server: 기존 유저 반환
    end

    Server->>Server: JWT Access Token + Refresh Token 생성
    Server->>Redis: SET token:{email} = RefreshToken
    Redis-->>Server: 저장 완료
    Server-->>Client: HttpOnly Cookie(accessToken, refreshToken) 설정 후 /books 리다이렉트

    Client-->>User: 로그인 완료, 도서 목록 페이지 진입
```

---

## 4. 토큰 갱신 (`POST /api/auth/refresh`) ✅

```mermaid
sequenceDiagram
    participant Client as Frontend
    participant Server as Backend API (Spring Boot)
    participant Redis as Redis

    Client->>Server: POST /api/auth/refresh { refreshToken }

    Server->>Server: Refresh Token 서명 검증
    alt 서명 무효
        Server-->>Client: HTTP 401 (TOKEN_INVALID)
    else 서명 유효
        Server->>Redis: GET token:{email}
        alt 저장된 토큰 없음
            Redis-->>Server: null
            Server-->>Client: HTTP 404 (TOKEN_NOT_FOUND)
        else 토큰 불일치
            Redis-->>Server: 저장된 토큰 반환
            Note over Server: 클라이언트 토큰 ≠ 저장 토큰
            Server->>Redis: DEL token:{email} (토큰 탈취 의심, 강제 삭제)
            Server-->>Client: HTTP 400 (TOKEN_MISMATCH)
        else 토큰 일치
            Server->>Server: 새 Access Token + 새 Refresh Token 생성
            Server->>Redis: SET token:{email} = 새 RefreshToken
            Redis-->>Server: 갱신 완료
            Server-->>Client: HTTP 200 { accessToken, refreshToken }
        end
    end
```

---

## 5. 로그아웃 (`POST /api/auth/logout`) ✅

```mermaid
sequenceDiagram
    participant Client as Frontend
    participant Server as Backend API (Spring Boot)
    participant Redis as Redis

    Client->>Server: POST /api/auth/logout<br>Header: Authorization: Bearer {accessToken}

    Server->>Server: JwtAuthenticationFilter: Access Token 검증 및 SecurityContext 주입
    Server->>Redis: DEL token:{email} (Refresh Token 삭제)
    Redis-->>Server: 완료

    Server->>Server: Access Token JTI 및 잔여 TTL 추출
    Server->>Redis: SET bl:access:{jti} = "logout" (TTL: 잔여 초)
    Note over Server,Redis: 블랙리스트 등록으로 Access Token 즉시 무효화
    Redis-->>Server: 완료

    Server-->>Client: HTTP 204 NO CONTENT
    Client-->>Client: 토큰 삭제 및 로그인 페이지 이동
```

---

## 요약: 토큰 생명주기

### Access Token
- **발행**: 로그인/토큰 갱신 시
- **TTL**: 180초 (3분)
- **저장소**: HttpOnly 쿠키 (자동 포함)
- **무효화**: 로그아웃 시 Redis 블랙리스트 등록

### Refresh Token
- **발행**: 로그인/토큰 갱신 시
- **TTL**: 300초 (5분)
- **저장소**: Redis (서버 보관)
- **갱신**: 토큰 갱신 시 새로운 토큰 발급 (기존 토큰 무효화)
- **보안**: 토큰 탈취 시 불일치 감지 → 즉시 세션 강제 만료

### 보안 규칙

1. **Refresh Token Rotation**: 갱신 호출 시 새로운 토큰 발급, 이전 토큰 즉시 무효화
2. **Token Mismatch Detection**: 저장된 토큰과 요청 토큰 불일치 시 토큰 탈취 의심 → 강제 삭제
3. **Access Token Blacklist**: 로그아웃 시 Redis 블랙리스트에 등록, 잔여 TTL 동안 유효하지 않음

