package com.project.velo.dto.response.comment;

import com.project.velo.dto.response.profile.AuthorResponseDto;

import java.time.LocalDateTime;

public record CommentDetailsResponseDto(
        Long id,
        String content,
        LocalDateTime createdAt,
        AuthorResponseDto author,
        boolean isPinned
) {}
