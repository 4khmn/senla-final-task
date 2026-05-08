package com.project.velo.dto.response.review;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewSentResponseDto(
        Long id,
        String advertisementTitle,
        Long advertisementId,
        String targetUsername,
        BigDecimal score,
        String content,
        LocalDateTime createdAt
) {
}
