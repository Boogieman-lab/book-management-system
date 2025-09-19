package com.example.bookmanagementsystembo.auth.Infra;

import com.example.bookmanagementsystembo.auth.domain.entity.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Jwt, UUID> {
    void deleteByEmail(String email);

    Optional<Jwt> findByRefreshToken(String refreshToken);
}
