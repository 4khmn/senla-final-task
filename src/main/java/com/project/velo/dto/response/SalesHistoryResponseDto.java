package com.project.velo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesHistoryResponseDto(
        Long id,
        String advertisementTitle,
        Long advertisementId,
        BigDecimal price,
        String buyerUsername,
        LocalDateTime soldAt
) { }
