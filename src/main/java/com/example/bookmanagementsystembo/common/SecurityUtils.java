package com.example.bookmanagementsystembo.common;

import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.user.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContextHolder에서 현재 로그인 사용자 정보를 꺼내는 유틸리티 클래스.
 *
 * 사용 예:
 *   Long userId = SecurityUtils.getCurrentUserId();
 *   String email = SecurityUtils.getCurrentUserEmail();
 *
 * IDOR 방어를 위해 서비스 레이어에서 항상 SecurityUtils로 userId를 검증해야 합니다.
 * (요청 파라미터의 userId는 신뢰하지 않고, 토큰에서 추출된 값을 기준으로 처리)
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * 현재 인증된 사용자의 userId를 반환합니다.
     * @throws CoreException 인증 정보가 없거나 principal 타입이 맞지 않을 때
     */
    public static Long getCurrentUserId() {
        CustomUserDetails principal = getPrincipal();
        return principal.getUserId();
    }

    /**
     * 현재 인증된 사용자의 이메일(username)을 반환합니다.
     * @throws CoreException 인증 정보가 없거나 principal 타입이 맞지 않을 때
     */
    public static String getCurrentUserEmail() {
        CustomUserDetails principal = getPrincipal();
        return principal.getUsername();
    }

    private static CustomUserDetails getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return details;
        }
        throw new CoreException(ErrorType.USER_NOT_FOUND, "인증된 사용자 정보를 찾을 수 없습니다.");
    }
}
