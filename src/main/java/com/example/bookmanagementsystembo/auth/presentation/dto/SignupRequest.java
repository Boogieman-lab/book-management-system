package com.example.bookmanagementsystembo.auth.presentation.dto;

import com.example.bookmanagementsystembo.user.enums.Role;

public record SignupRequest(
        String username,
        String password,
        String email,
        Role role
) {}
