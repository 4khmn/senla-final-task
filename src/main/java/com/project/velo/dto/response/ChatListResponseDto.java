package com.project.velo.dto.response;

import java.time.LocalDateTime;

public record ChatListResponseDto(
        Long id,
        Long advertisementId,
        String advertisementTitle,
        String lastMessageContent,
        LocalDateTime updatedAt,
        String interlocutorUsername,
        String interlocutorAvatarUrl
) {}