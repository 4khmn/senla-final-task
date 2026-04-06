package com.project.velo.dto.response;

import java.time.LocalDateTime;

public record CommentDetailsResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        AuthorResponseDto author
) {}
