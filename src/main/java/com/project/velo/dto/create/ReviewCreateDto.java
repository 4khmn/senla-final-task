package com.project.velo.dto.create;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ReviewCreateDto(

        @NotNull(message = "Оценка обязательна")
        @Min(value = 1, message = "Минимальная оценка — 1")
        @Max(value = 5, message = "Максимальная оценка — 5")
        BigDecimal score,

        @Size(max = 1000, message = "Отзыв не может быть длиннее 1000 символов")
        String content
) {
}
