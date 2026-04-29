package com.project.velo.dto.request;

import java.math.BigDecimal;

public record AdvertisementFilterDto(
        String query,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String sortDirection
) {}
