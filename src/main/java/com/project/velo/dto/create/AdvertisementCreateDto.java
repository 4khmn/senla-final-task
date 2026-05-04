package com.project.velo.dto.create;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record AdvertisementCreateDto(
        @NotBlank(message = "Название объявления не может быть пустым")
        @Size(max = 64, message = "Название объявления должно быть до 64 символов")
        String title,

        @NotBlank(message = "Описание объявления не может быть пустым")
        @Size(max = 2000, message = "Описание объявления должно быть до 2000 символов")
        String description,

        @NotNull(message = "Цена должна быть указана")
        @PositiveOrZero(message = "Цена не может быть отрицательной")
        @Digits(integer = 10, fraction = 2, message = "Цена должна быть числом (до 10 знаков до запятой и 2 после)")
        BigDecimal price,

        @NotNull(message = "ID категории обязателен")
        Long categoryId
) {}
