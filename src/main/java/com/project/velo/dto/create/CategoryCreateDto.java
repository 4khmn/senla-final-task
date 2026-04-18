package com.project.velo.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record CategoryCreateDto(
        @NotBlank
        @Max(value = 48, message = "Название категории не может быть больше 48 символов")
        String name) {
}
