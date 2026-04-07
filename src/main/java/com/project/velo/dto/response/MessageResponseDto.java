package com.project.velo.dto.response;

import java.time.LocalDateTime;

public record MessageResponseDto(
        Long id,
        String content,
        LocalDateTime sentAt,
        String senderUsername,
        boolean isMine,
        boolean isEdited
) {}