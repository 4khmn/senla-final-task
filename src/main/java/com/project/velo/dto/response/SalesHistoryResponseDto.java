package com.project.velo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesHistoryResponseDto(
        Long id,
        String adTitle,
        Long adId,
        BigDecimal price,
        String buyerUsername,
        LocalDateTime soldAt
) { }
