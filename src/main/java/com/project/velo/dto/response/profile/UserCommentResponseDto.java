package com.project.velo.dto.response.profile;

import java.time.LocalDateTime;

public record UserCommentResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        Long advertisementId,
        String advertisementTitle
) { }