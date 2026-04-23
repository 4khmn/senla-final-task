package com.project.velo.dto.response.advertisement;


import com.project.velo.dto.response.profile.AuthorResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdvertisementResponseDto(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String status,
        boolean isTop,
        LocalDateTime createdAt,
        AuthorResponseDto seller,
        String categoryName,
        String primaryImageUrl,
        List<String> otherImageUrls
) {
}
