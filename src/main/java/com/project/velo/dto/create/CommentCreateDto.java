package com.project.velo.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateDto(
        @NotBlank(message = "Комментарий не может быть пустым")
        @Size(max = 2000, message = "Комментарий слишком длинный (макс. 2000 символов)")
        String content
) {
}
