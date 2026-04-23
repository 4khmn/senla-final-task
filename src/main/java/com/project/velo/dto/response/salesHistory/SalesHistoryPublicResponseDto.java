package com.project.velo.dto.response.salesHistory;

import java.time.LocalDateTime;

public record SalesHistoryPublicResponseDto(
        Long id,
        String advertisementTitle,
        LocalDateTime soldAt
) { }
