package com.project.velo.dto.response;

import java.time.LocalDateTime;

public record SalesPublicHistoryResponseDto(
        Long id,
        String advertisementTitle,
        LocalDateTime soldAt
) { }
