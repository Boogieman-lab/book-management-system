package com.example.bookmanagementsystembo.token.config;

import com.example.bookmanagementsystembo.user.domain.dto.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public String createAccessToken(String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        Instant now = Instant.now();
        Instant expirationInstant = now.plusSeconds(jwtProperties.accessSec());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationInstant))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createRefreshToken(String email) {
        Instant now = Instant.now();
        Instant expirationInstant = now.plusSeconds(jwtProperties.refreshSec());

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationInstant))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }


    public String getUsername(String refreshToken) {
        return getClaims(refreshToken).getSubject();
    }

    public long getRefreshSec() {
        return jwtProperties.refreshSec();
    }

    public boolean validate(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getRemainingTime(String accessToken) {
        try {
            Date expiration = getClaims(accessToken).getExpiration();
            long now = new Date().getTime();

            if (expiration.getTime() > now) {
                return TimeUnit.MILLISECONDS.toSeconds(expiration.getTime() - now);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
