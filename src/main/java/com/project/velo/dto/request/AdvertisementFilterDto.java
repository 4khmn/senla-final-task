package com.project.velo.dto.request;

import java.math.BigDecimal;

public record AdvertisementFilterDto(
        String query,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String priceSortDirection
) {

    public boolean isEmpty() {
        return query == null && categoryId == null && minPrice == null && maxPrice == null && priceSortDirection == null;
    }

}
