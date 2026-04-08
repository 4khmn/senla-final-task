package com.project.velo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        String advertisementTitle,
        Long advertisementId,
        String authorUsername,
        BigDecimal score,
        String content,
        LocalDateTime createdAt
) {
}
