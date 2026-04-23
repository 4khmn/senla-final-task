package com.project.velo.dto.response.advertisement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdvertisementShortResponseDto(
        Long id,
        String title,
        BigDecimal price,
        String categoryName,
        String primaryImageUrl,
        boolean isTop,
        LocalDateTime createdAt,
        String sellerUsername,
        BigDecimal sellerScore
) {
}
