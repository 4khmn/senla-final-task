package com.project.velo.dto.response.salesHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesHistoryPrivateResponseDto(
        Long id,
        String advertisementTitle,
        Long advertisementId,
        BigDecimal price,
        String buyerUsername,
        LocalDateTime soldAt,
        boolean wasTop
) { }
