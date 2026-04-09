package com.project.velo.dto.create;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record AdvertisementCreateDto(
        @NotBlank(message = "Название объявления не может быть пустым")
        @Size(max = 255, message = "Название объявления должно быть до 255 символов")
        String title,

        @NotBlank(message = "Описание объявления не может быть пустым")
        @Size(max = 2000, message = "Описание объявления должно быть до 2000 символов")
        String description,

        @NotNull(message = "Цена должна быть указана")
        @PositiveOrZero(message = "Цена не может быть отрицательной")
        @Digits(integer = 10, fraction = 2, message = "Цена должна быть числом (до 10 знаков до запятой и 2 после)")
        BigDecimal price,

        @NotNull(message = "ID категории обязателен")
        Long categoryId,

        @Size(min = 1, max = 20, message = "Количество фотографий может быть от 1 до 20")
        List<@NotBlank String> imageUrls // get(0) - primary
) {}
