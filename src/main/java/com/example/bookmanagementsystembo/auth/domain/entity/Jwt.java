package com.example.bookmanagementsystembo.auth.domain.entity;

import com.example.bookmanagementsystembo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "jwt")
public class Jwt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "jwt_id", columnDefinition = "BINARY(16)")
    @JdbcTypeCode(Types.BINARY)
    @Comment("토큰 ID (UUID)")
    private UUID id;

    @Column(name = "email", nullable = false)
    @Comment("사용자 이메일")
    private String email;

    @Column(name = "refresh_token", nullable = false, columnDefinition = "TEXT")
    @Comment("refresh token")
    private String refreshToken;

    @Column(name = "expired_at", nullable = false)
    @Comment("토큰 만료일시")
    private Instant expiredAt;

    @Column(name = "issued_at", nullable = false)
    @Comment("토큰 발급일시")
    private Instant issuedAt;

    public static Jwt create(String email, String refreshToken, Instant expiredAt, Instant issuedAt) {
        return new Jwt(null, email, refreshToken, expiredAt, issuedAt);
    }
}
