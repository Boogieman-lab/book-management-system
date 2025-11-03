package com.example.bookmanagementsystembo.token.config;

import com.example.bookmanagementsystembo.user.domain.dto.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.util.UUID;

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
        claims.put("role", role.name());

        Instant now = Instant.now();
        Instant expirationInstant = now.plusSeconds(jwtProperties.accessSec());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setId(UUID.randomUUID().toString())
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
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationInstant))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }


    public String getUsername(String refreshToken) {
        return getClaims(refreshToken).getSubject();
    }

    public String getJti(String token) {
        return getClaims(token).getId();
    }

    public long getRefreshSec() {
        return jwtProperties.refreshSec();
    }

    public boolean isInvalid(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public boolean isValid(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getRemainingTime(String token) {
        try {
            Claims claims = getClaims(token);
            long nowSec = Instant.now().getEpochSecond();
            long expSec = claims.getExpiration().toInstant().getEpochSecond();
            long remain = expSec - nowSec;
            return Math.max(0, remain);
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
