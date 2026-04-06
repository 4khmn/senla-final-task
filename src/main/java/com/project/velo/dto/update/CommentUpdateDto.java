package com.project.velo.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateDto(
        @NotBlank(message = "Комментарий не может быть пустым")
        @Size(max = 2000, message = "Комментарий слишком длинный (макс. 2000 символов)")
        String content
) { }
