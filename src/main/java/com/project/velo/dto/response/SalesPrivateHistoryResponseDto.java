package com.project.velo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesPrivateHistoryResponseDto(
        Long id,
        String advertisementTitle,
        Long advertisementId,
        BigDecimal price,
        String buyerUsername,
        LocalDateTime soldAt,
        boolean wasTop
) { }
