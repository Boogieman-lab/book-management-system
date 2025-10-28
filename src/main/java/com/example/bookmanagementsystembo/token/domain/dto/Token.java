package com.example.bookmanagementsystembo.token.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@RedisHash(value = "token")
public class Token {

    @Id
    private String userEmail;
    private String token;
    private LocalDateTime createdAt;
    @TimeToLive
    private Long expiration;

    public void updateToken(String newToken, Long newExpiration) {
        this.token = newToken;
        this.createdAt = LocalDateTime.now();
        this.expiration = newExpiration;
    }

    public static Token create(String userEmail, String token, Long expiration) {
        return new Token(userEmail, token, LocalDateTime.now(), expiration);
    }

}
