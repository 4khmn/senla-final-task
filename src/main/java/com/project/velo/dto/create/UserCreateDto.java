package com.project.velo.dto.create;

import com.project.velo.util.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank(message = "Username обязателен")
        @Size(min = 6, max = 50, message = "Username должен быть от 6 до 50 символов")
        String username,

        @NotBlank(message = "Пароль обязателен")
        @Pattern(
                regexp = ValidationConstants.PASSWORD_REGEX,
                message = "Пароль должен быть не менее 8 символов и содержать: хотя бы одну цифру, одну заглавную букву и один спецсимвол (@#$%^&+=!)"
        )
        String password,

        @NotBlank(message = "Email обязателен")
        @Email(
                regexp = ValidationConstants.EMAIL_REGEX,
                message = "Некорректный формат email"
        )
        String email,

        @Size(max = 50, message = "Имя не может быть длиннее 50 символов")
        @NotBlank(message = "First Name обязятелен")
        String firstName,

        @Size(max = 50, message = "Фамилия не может быть длиннее 50 символов")
        @NotBlank(message = "Last Name обязятелен")
        String lastName
) {}