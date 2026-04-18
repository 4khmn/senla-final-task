package com.project.velo.dto.update;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateDto(
        @NotBlank
        @Max(value = 48, message = "Название категории не может быть больше 48 символов")
        String name) {
}
