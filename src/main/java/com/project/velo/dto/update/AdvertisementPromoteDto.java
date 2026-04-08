package com.project.velo.dto.update;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AdvertisementPromoteDto(
    @NotBlank
    @Min(value = 0, message = "Минимальное значение продвижения объявления- 1")
    @Max(value = 30, message = "Максимальное значение продвижения объявления - 30 дней")
    int days
) { }
