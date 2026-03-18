package com.example.bookmanagementsystembo.common;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.user.dto.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SecurityUtils IDOR 방어 테스트")
class SecurityUtilsTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    // ──────────────────────────────────────────────────────────────
    // getCurrentUserId() — IDOR 방어 핵심
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getCurrentUserId()")
    class GetCurrentUserIdTest {

        @Test
        @DisplayName("(IDOR 방어) SecurityContext의 userId가 토큰에서 추출한 값과 정확히 일치한다")
        void userId_matchesTokenClaim() {
            long expectedUserId = 42L;
            setAuthentication(expectedUserId, "user@example.com", "ROLE_USER");

            Long actualUserId = SecurityUtils.getCurrentUserId();

            assertThat(actualUserId).isEqualTo(expectedUserId);
        }

        @Test
        @DisplayName("(IDOR 방어) 서로 다른 사용자의 userId는 SecurityContext에서 일치하지 않는다")
        void userId_doesNotMatchOtherUser() {
            setAuthentication(100L, "hacker@example.com", "ROLE_USER");

            Long userId = SecurityUtils.getCurrentUserId();

            // 공격자가 파라미터로 넘긴 victim userId와 SecurityContext의 userId는 다르다
            long victimUserId = 999L;
            assertThat(userId).isNotEqualTo(victimUserId);
        }

        @Test
        @DisplayName("인증 정보가 없으면 CoreException이 발생한다")
        void noAuthentication_throwsCoreException() {
            // SecurityContext 비어있음

            assertThatThrownBy(SecurityUtils::getCurrentUserId)
                    .isInstanceOf(CoreException.class);
        }

        @Test
        @DisplayName("principal이 CustomUserDetails가 아니면 CoreException이 발생한다")
        void wrongPrincipalType_throwsCoreException() {
            // String을 principal로 등록 (CustomUserDetails가 아님)
            var auth = new UsernamePasswordAuthenticationToken("anonymous", null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            assertThatThrownBy(SecurityUtils::getCurrentUserId)
                    .isInstanceOf(CoreException.class);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // getCurrentUserEmail()
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getCurrentUserEmail()")
    class GetCurrentUserEmailTest {

        @Test
        @DisplayName("SecurityContext에서 현재 사용자 이메일을 반환한다")
        void shouldReturnEmail() {
            setAuthentication(1L, "user@example.com", "ROLE_USER");

            String email = SecurityUtils.getCurrentUserEmail();

            assertThat(email).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("인증 정보가 없으면 CoreException이 발생한다")
        void noAuthentication_throwsCoreException() {
            assertThatThrownBy(SecurityUtils::getCurrentUserEmail)
                    .isInstanceOf(CoreException.class);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // IDOR 시나리오 통합 검증
    // ──────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("IDOR 방어 시나리오")
    class IdorDefenseScenarioTest {

        @Test
        @DisplayName("요청 파라미터의 userId가 달라도 SecurityContext의 userId는 변하지 않는다")
        void securityContextUserId_isImmutableToRequestParam() {
            long tokenUserId = 10L;
            setAuthentication(tokenUserId, "real@example.com", "ROLE_USER");

            // 악의적인 파라미터로 다른 사람의 리소스 접근 시도
            long attackerSuppliedUserId = 999L;

            Long actualUserId = SecurityUtils.getCurrentUserId();

            // SecurityUtils는 항상 토큰 기반 userId를 반환
            assertThat(actualUserId).isEqualTo(tokenUserId);
            assertThat(actualUserId).isNotEqualTo(attackerSuppliedUserId);
        }

        @Test
        @DisplayName("ROLE_ADMIN 사용자도 올바른 userId가 SecurityContext에서 반환된다")
        void adminUser_returnsCorrectUserId() {
            setAuthentication(1L, "admin@example.com", "ROLE_ADMIN");

            assertThat(SecurityUtils.getCurrentUserId()).isEqualTo(1L);
            assertThat(SecurityUtils.getCurrentUserEmail()).isEqualTo("admin@example.com");
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 헬퍼
    // ──────────────────────────────────────────────────────────────

    private void setAuthentication(Long userId, String email, String role) {
        CustomUserDetails principal = new CustomUserDetails(userId, email, role);
        var auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
