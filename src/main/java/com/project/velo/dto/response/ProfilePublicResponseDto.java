package com.project.velo.dto.response;

import java.math.BigDecimal;

public record ProfilePublicResponseDto(
        Long id,
        String username,
        BigDecimal rating,

        String firstName,
        String lastName,
        String bio,
        String avatarUrl
) { }
