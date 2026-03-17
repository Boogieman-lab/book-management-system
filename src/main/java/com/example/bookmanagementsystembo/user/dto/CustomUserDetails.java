package com.example.bookmanagementsystembo.user.dto;

import com.example.bookmanagementsystembo.user.entity.Users;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class CustomUserDetails extends User {
    private final Long userId;

    public CustomUserDetails(Users user) {
        super(
                user.getEmail(),               // username
                user.getPassword(),            // password
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        this.userId = user.getUserId();
    }

}
