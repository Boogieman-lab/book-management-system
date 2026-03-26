package com.example.bookmanagementsystembo.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 1, max = 30) String name,
        @Size(max = 500) @Pattern(regexp = "^$|^https?://.*", message = "올바른 URL 형식이어야 합니다.") String profileImage
) {
}
