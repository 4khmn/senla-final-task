package com.project.velo.dto.response;

import java.math.BigDecimal;

public record ProfileResponseDto(
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
) { }
