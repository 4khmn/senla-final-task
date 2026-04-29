package com.project.velo.dto.update;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdvertisementUpdateDto(
        @Size(max = 255, message = "Название слишком длинное")
        String title,

        @Size(max = 2000, message = "Описание слишком длинное")
        String description,

        @PositiveOrZero(message = "Цена не может быть отрицательной")
        @Digits(integer = 10, fraction = 2, message = "Цена должна быть числом (до 10 знаков до запятой и 2 после)")
        BigDecimal price,


        Long categoryId
) {}
