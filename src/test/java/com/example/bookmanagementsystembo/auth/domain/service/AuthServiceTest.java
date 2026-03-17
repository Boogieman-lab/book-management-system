package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.presentation.dto.LoginRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.token.dto.TokenRes;
import com.example.bookmanagementsystembo.token.service.TokenService;
import com.example.bookmanagementsystembo.user.entity.Users;
import com.example.bookmanagementsystembo.user.enums.Role;
import com.example.bookmanagementsystembo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ──────────────────────────────────────────────────────────────
    // 회원가입 (signup)
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("회원가입")
    class SignupTest {

        @Test
        @DisplayName("정상 케이스: 중복 이메일 없으면 사용자 저장")
        void signup_shouldSaveUser_whenEmailNotExists() {
            SignupRequest request = new SignupRequest("testuser", "password123", "test@example.com", Role.ROLE_USER);

            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

            authService.signup(request);

            ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
            verify(userRepository).save(userCaptor.capture());

            Users saved = userCaptor.getValue();
            assertThat(saved.getEmail()).isEqualTo("test@example.com");
            assertThat(saved.getPassword()).isEqualTo("encodedPassword");
            assertThat(saved.getName()).isEqualTo("testuser");
            assertThat(saved.getRole()).isEqualTo(Role.ROLE_USER);
            assertThat(saved.getLoginFailCount()).isEqualTo(0);
            assertThat(saved.isLocked()).isFalse();
        }

        @Test
        @DisplayName("예외 케이스: 중복 이메일이면 CONFLICT 예외 발생")
        void signup_shouldThrow_whenEmailAlreadyExists() {
            SignupRequest request = new SignupRequest("testuser", "password123", "dup@example.com", Role.ROLE_USER);

            when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> {
                        CoreException ce = (CoreException) ex;
                        assertThat(ce.getErrorType()).isEqualTo(ErrorType.USER_ALREADY_EXISTS);
                    });

            verify(userRepository, never()).save(any());
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 로그인 (login)
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("로그인")
    class LoginTest {

        private Users activeUser;

        @BeforeEach
        void setUp() {
            activeUser = Users.create("user@example.com", "encodedPass", "홍길동", null, null, Role.ROLE_USER);
        }

        @Test
        @DisplayName("정상 케이스: 올바른 자격증명으로 토큰 발급")
        void login_shouldIssueTokens_whenCredentialsValid() {
            LoginRequest request = new LoginRequest("user@example.com", "plainPass");

            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);
            when(tokenService.issue(activeUser)).thenReturn(new TokenRes("access-token", "refresh-token"));

            LoginResponse response = authService.login(request);

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            assertThat(response.email()).isEqualTo("user@example.com");
            assertThat(response.name()).isEqualTo("홍길동");

            // 로그인 성공 시 실패 횟수 초기화 저장 확인
            verify(userRepository).save(activeUser);
            assertThat(activeUser.getLoginFailCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("예외 케이스: 존재하지 않는 이메일은 INVALID_CREDENTIALS 발생")
        void login_shouldThrow_whenEmailNotFound() {
            LoginRequest request = new LoginRequest("nouser@example.com", "pass");

            when(userRepository.findByEmail("nouser@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_CREDENTIALS));

            verify(tokenService, never()).issue(any());
        }

        @Test
        @DisplayName("예외 케이스: 잘못된 비밀번호는 INVALID_CREDENTIALS 발생 및 실패 횟수 증가")
        void login_shouldThrowAndIncrementFailCount_whenPasswordWrong() {
            LoginRequest request = new LoginRequest("user@example.com", "wrongPass");

            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_CREDENTIALS));

            assertThat(activeUser.getLoginFailCount()).isEqualTo(1);
            verify(userRepository).save(activeUser);
            verify(tokenService, never()).issue(any());
        }

        @Test
        @DisplayName("예외 케이스: 5회 연속 실패 시 계정이 잠기고 ACCOUNT_LOCKED 발생")
        void login_shouldLockAccount_afterFiveFailures() {
            LoginRequest request = new LoginRequest("user@example.com", "wrongPass");

            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

            // 5회 실패 시도
            for (int i = 0; i < 4; i++) {
                assertThatThrownBy(() -> authService.login(request))
                        .isInstanceOf(CoreException.class);
            }

            assertThat(activeUser.isLocked()).isFalse();
            assertThat(activeUser.getLoginFailCount()).isEqualTo(4);

            // 5번째 시도 → 잠금 발생
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_CREDENTIALS));

            assertThat(activeUser.isLocked()).isTrue();
            assertThat(activeUser.getLoginFailCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("예외 케이스: 잠긴 계정으로 로그인 시 ACCOUNT_LOCKED 발생")
        void login_shouldThrow_whenAccountLocked() {
            // 5회 실패로 미리 잠긴 상태 만들기
            for (int i = 0; i < 5; i++) {
                activeUser.incrementLoginFailCount();
            }

            LoginRequest request = new LoginRequest("user@example.com", "plainPass");

            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.ACCOUNT_LOCKED));

            // 잠긴 경우 비밀번호 체크 자체를 하지 않아야 함
            verify(passwordEncoder, never()).matches(any(), any());
            verify(tokenService, never()).issue(any());
        }

        @Test
        @DisplayName("정상 케이스: 로그인 성공 시 실패 횟수 초기화")
        void login_shouldResetFailCount_onSuccess() {
            // 3회 실패 기록 시뮬레이션
            activeUser.incrementLoginFailCount();
            activeUser.incrementLoginFailCount();
            activeUser.incrementLoginFailCount();

            LoginRequest request = new LoginRequest("user@example.com", "plainPass");

            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);
            when(tokenService.issue(activeUser)).thenReturn(new TokenRes("at", "rt"));

            authService.login(request);

            assertThat(activeUser.getLoginFailCount()).isEqualTo(0);
            assertThat(activeUser.isLocked()).isFalse();
        }
    }
}
