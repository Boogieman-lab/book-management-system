package com.example.bookmanagementsystembo.token.repository;

import com.example.bookmanagementsystembo.token.domain.dto.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, String> {
}
