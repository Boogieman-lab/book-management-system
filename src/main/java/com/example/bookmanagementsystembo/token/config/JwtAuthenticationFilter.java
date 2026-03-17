package com.example.bookmanagementsystembo.token.config;

import com.example.bookmanagementsystembo.exception.ErrorType;
import com.example.bookmanagementsystembo.exception.ErrorResponse;
import com.example.bookmanagementsystembo.user.dto.CustomUserDetails;
import com.example.bookmanagementsystembo.user.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BL_ACCESS_PREFIX = "bl:access:";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {
        String token = null;
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7).trim();
        } else {
            // 2. Authorization 헤더가 없으면 쿠키에서 'accessToken'을 찾습니다.
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        if (tokenProvider.isInvalid(token)) {
            writeUnauthorized(response, ErrorType.TOKEN_INVALID);
            return;
        }

        String jti = tokenProvider.getJti(token);
        if (!StringUtils.hasText(jti)) {
            writeUnauthorized(response, ErrorType.TOKEN_JTI_MISSING);
            return;
        }
        Boolean blacklisted = redisTemplate.hasKey(BL_ACCESS_PREFIX + jti);
        if (blacklisted) {
            writeUnauthorized(response, ErrorType.TOKEN_BLACKLISTED);
            return;
        }
        String username = tokenProvider.getUsername(token);
        String role = tokenProvider.getRole(token);

        var auth = new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(role)));

        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }


    private void writeUnauthorized(HttpServletResponse response, ErrorType errorType) throws IOException {
        response.setStatus(errorType.getCode().getStatus().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = ErrorResponse.of(
                errorType.getCode().getMessage(),
                errorType.getMessage()
        );

        String jsonBody = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonBody);
    }
}
