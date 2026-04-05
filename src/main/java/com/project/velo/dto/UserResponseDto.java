package com.project.velo.dto;

import java.math.BigDecimal;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String role,
        BigDecimal rating,

        String firstName,
        String lastName,
        String phone,
        String bio,
        String avatarUrl
) {
}
