package com.example.bookmanagementsystembo.user.infra;

import com.example.bookmanagementsystembo.user.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
