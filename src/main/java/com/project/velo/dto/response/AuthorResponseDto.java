package com.project.velo.dto.response;

import java.math.BigDecimal;

public record AuthorResponseDto(
        Long id,
        String username,
        BigDecimal rating,
        String avatarUrl
) {}
