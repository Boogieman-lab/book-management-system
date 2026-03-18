package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.user.entity.Users;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * Spring Security의 UserDetails 구현체.
 * userId를 보관하여 IDOR 방어 시 SecurityContextHolder에서 바로 꺼낼 수 있습니다.
 * 컨트롤러에서 @AuthenticationPrincipal CustomUserDetails principal 로 주입받아 사용합니다.
 */
@Getter
public class CustomUserDetails extends User {

    private final Long userId;

    /** DB 조회 기반 생성자 — CustomUserDetailsService.loadUserByUsername() 에서 사용 */
    public CustomUserDetails(Users user) {
        super(
                user.getEmail(),
                user.getPassword() != null ? user.getPassword() : "",
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        this.userId = user.getUserId();
    }

    /**
     * JWT 토큰 파싱 기반 생성자 — JwtAuthenticationFilter에서 DB 조회 없이 사용.
     * 비밀번호는 필요 없으므로 빈 문자열로 처리합니다.
     */
    public CustomUserDetails(Long userId, String email, String role) {
        super(email, "", List.of(new SimpleGrantedAuthority(role)));
        this.userId = userId;
    }
}
