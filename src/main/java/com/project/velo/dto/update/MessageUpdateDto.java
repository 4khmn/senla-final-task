package com.project.velo.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateDto(
        @NotBlank(message = "Сообщение не может быть пустым")
        @Size(max = 5000)
        String content
) {}
