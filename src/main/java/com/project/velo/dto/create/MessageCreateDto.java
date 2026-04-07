package com.project.velo.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageCreateDto(
        @NotBlank(message = "Сообщение не может быть пустым")
        @Size(max = 5000)
        String content
) {}
