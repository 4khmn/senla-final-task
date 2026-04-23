package com.project.velo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProfilePublicResponseDto(
        Long id,
        String username,
        BigDecimal rating,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        LocalDateTime createdAt
) { }
