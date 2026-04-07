package com.project.velo.dto.response;

public record ChatResponseDto(
        Long id,
        Long advertisementId,
        String sellerUsername,
        String buyerUsername
) {}
