package com.project.velo.dto;


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
        UserResponseDto user,
        String categoryName,
        String primaryImageUrl,
        List<String> otherImageUrls
) {
}
