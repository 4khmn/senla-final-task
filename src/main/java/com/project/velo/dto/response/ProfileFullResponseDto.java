package com.project.velo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProfileFullResponseDto(
        Long id,
        String username,
        String email,
        String phone,
        String role,
        BigDecimal rating,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        boolean enabled,
        LocalDateTime createdAt
) {
}
