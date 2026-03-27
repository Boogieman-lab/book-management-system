package com.example.bookmanagementsystembo.auth.domain.service;

import com.example.bookmanagementsystembo.auth.presentation.dto.LoginRequest;
import com.example.bookmanagementsystembo.auth.presentation.dto.LoginResponse;
import com.example.bookmanagementsystembo.auth.presentation.dto.SignupRequest;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.token.dto.TokenResponse;
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
            // given
            SignupRequest request = new SignupRequest("testuser", "password123", "test@example.com");
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

            // when
            authService.signup(request);

            // then
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
            // given
            SignupRequest request = new SignupRequest("testuser", "password123", "dup@example.com");
            when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

            // when & then
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
            // given
            LoginRequest request = new LoginRequest("user@example.com", "plainPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);
            when(tokenService.issue(activeUser)).thenReturn(new TokenResponse("access-token", "refresh-token"));

            // when
            LoginResponse response = authService.login(request);

            // then
            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            assertThat(response.email()).isEqualTo("user@example.com");
            assertThat(response.name()).isEqualTo("홍길동");
            verify(userRepository).save(activeUser);
            assertThat(activeUser.getLoginFailCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("예외 케이스: 존재하지 않는 이메일은 INVALID_CREDENTIALS 발생")
        void login_shouldThrow_whenEmailNotFound() {
            // given
            LoginRequest request = new LoginRequest("nouser@example.com", "pass");
            when(userRepository.findByEmail("nouser@example.com")).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_CREDENTIALS));

            verify(tokenService, never()).issue(any());
        }

        @Test
        @DisplayName("예외 케이스: 잘못된 비밀번호는 INVALID_CREDENTIALS 발생 및 실패 횟수 증가")
        void login_shouldThrowAndIncrementFailCount_whenPasswordWrong() {
            // given
            LoginRequest request = new LoginRequest("user@example.com", "wrongPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_CREDENTIALS));

            assertThat(activeUser.getLoginFailCount()).isEqualTo(1);
            verify(userRepository).save(activeUser);
            verify(tokenService, never()).issue(any());
        }

        @Test
        @DisplayName("경계값 케이스: 4회 실패까지는 계정이 잠기지 않는다")
        void login_shouldNotLock_beforeFiveFailures() {
            // given
            LoginRequest request = new LoginRequest("user@example.com", "wrongPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

            // when
            for (int i = 0; i < 4; i++) {
                assertThatThrownBy(() -> authService.login(request))
                        .isInstanceOf(CoreException.class);
            }

            // then
            assertThat(activeUser.getLoginFailCount()).isEqualTo(4);
            assertThat(activeUser.isLocked()).isFalse();
        }

        @Test
        @DisplayName("예외 케이스: 5회째 실패 시 계정이 잠기고 INVALID_CREDENTIALS 발생")
        void login_shouldLockAccount_onFifthFailure() {
            // given
            LoginRequest request = new LoginRequest("user@example.com", "wrongPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

            for (int i = 0; i < 4; i++) {
                assertThatThrownBy(() -> authService.login(request)).isInstanceOf(CoreException.class);
            }

            // when - 5번째 시도
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_CREDENTIALS));

            // then
            assertThat(activeUser.getLoginFailCount()).isEqualTo(5);
            assertThat(activeUser.isLocked()).isTrue();
        }

        @Test
        @DisplayName("예외 케이스: 5회 실패 후 6번째 시도 시 ACCOUNT_LOCKED 발생")
        void login_shouldThrowAccountLocked_onAttemptAfterLock() {
            // given - 5회 실패로 계정 잠금 유도
            LoginRequest wrongRequest = new LoginRequest("user@example.com", "wrongPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

            for (int i = 0; i < 5; i++) {
                assertThatThrownBy(() -> authService.login(wrongRequest)).isInstanceOf(CoreException.class);
            }
            assertThat(activeUser.isLocked()).isTrue();

            // when - 잠긴 상태에서 추가 시도 (비밀번호 무관)
            LoginRequest nextRequest = new LoginRequest("user@example.com", "anyPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));

            // then
            assertThatThrownBy(() -> authService.login(nextRequest))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.ACCOUNT_LOCKED));

            verify(passwordEncoder, never()).matches(eq("anyPass"), any());
            verify(tokenService, never()).issue(any());
        }

        @Test
        @DisplayName("예외 케이스: 잠긴 계정은 올바른 비밀번호로도 로그인 불가")
        void login_shouldThrowAccountLocked_evenWithCorrectPassword() {
            // given - 5회 수동 실패로 잠금
            for (int i = 0; i < 5; i++) {
                activeUser.incrementLoginFailCount();
            }

            LoginRequest request = new LoginRequest("user@example.com", "plainPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> assertThat(((CoreException) ex).getErrorType())
                            .isEqualTo(ErrorType.ACCOUNT_LOCKED));

            verify(passwordEncoder, never()).matches(any(), any());
            verify(tokenService, never()).issue(any());
        }

        @Test
        @DisplayName("정상 케이스: 로그인 성공 시 실패 횟수와 잠금 상태 초기화")
        void login_shouldResetFailCount_onSuccess() {
            // given - 3회 실패 기록
            activeUser.incrementLoginFailCount();
            activeUser.incrementLoginFailCount();
            activeUser.incrementLoginFailCount();

            LoginRequest request = new LoginRequest("user@example.com", "plainPass");
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(activeUser));
            when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);
            when(tokenService.issue(activeUser)).thenReturn(new TokenResponse("at", "rt"));

            // when
            authService.login(request);

            // then
            assertThat(activeUser.getLoginFailCount()).isEqualTo(0);
            assertThat(activeUser.isLocked()).isFalse();
        }
    }
}
