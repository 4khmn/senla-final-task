package com.project.velo.dto.response.profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProfilePrivateResponseDto(
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
