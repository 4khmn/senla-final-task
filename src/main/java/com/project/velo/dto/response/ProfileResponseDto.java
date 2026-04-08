package com.project.velo.dto.response;

import java.math.BigDecimal;

public record ProfileResponseDto(
        Long id,
        String username,
        String role,
        BigDecimal rating,

        String firstName,
        String lastName,
        String bio,
        String avatarUrl
) { }
