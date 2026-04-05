package com.project.velo.dto;

import com.project.velo.entity.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AdvertisementCreateDto(
        @NotBlank(message = "Название объявления не может быть пустым")
        @Size(max = 255, message = "Название объявления должно быть до 255 символов")
        String title,

        @NotBlank(message = "Описание объявления не может быть пустым")
        String description,

        @NotNull(message = "Цена должна быть указана")
        @PositiveOrZero(message = "Цена не может быть отрицательной")
        @Digits(integer = 10, fraction = 2, message = "Цена должна быть числом (до 10 знаков до запятой и 2 после)")
        BigDecimal price,

        Long categoryId,

        boolean isTop
) {}
