package com.project.velo.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateDto(
        @NotBlank(message = "Название не может быть пустым")
        @Size(min = 2, message = "Название категории не может быть меньше 2 символов")
        @Size(max = 48, message = "Название категории не может быть больше 48 символов")
        String name) {
}
