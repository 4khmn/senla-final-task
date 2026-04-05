package com.project.velo.dto;

import com.project.velo.util.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank(message = "Username обязателен")
        @Size(min = 6, max = 50)
        String username,

        @NotBlank(message = "Пароль обязателен")
        @Pattern(regexp = ValidationConstants.PASSWORD_REGEX)
        String password,

        @NotBlank(message = "Email обязателен")
        @Email(regexp = ValidationConstants.EMAIL_REGEX)
        String email,

        String firstName,
        String lastName
) {}