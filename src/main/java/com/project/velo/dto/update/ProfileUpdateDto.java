package com.project.velo.dto.update;

import com.project.velo.util.ValidationConstants;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateDto(
        @Size(max = 50, message = "Имя не может быть длиннее 50 символов")
        String firstName,

        @Size(max = 50, message = "Фамилия не может быть длиннее 50 символов")
        String lastName,

        @Pattern(
                regexp = ValidationConstants.PHONE_REGEX,
                message = "Номер телефона должен быть в формате +7... или 8... (всего 11 цифр)"
        )
        String phone,

        @Size(max = 500, message = "О себе: максимум 500 символов")
        String bio
) {
}
