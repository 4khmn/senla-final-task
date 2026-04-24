package com.project.velo.controller.auth;

import com.project.velo.dto.response.profile.ProfilePrivateResponseDto;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.auth.AuthResponseDto;
import com.project.velo.dto.auth.LoginRequestDto;
import com.project.velo.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Операции аутентификации и регистрации новых пользователей")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя",
            description = "Создает новый аккаунт")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "409", description = "Пользователь с таким логином или email уже существует")
    @PostMapping("/register")
    public ResponseEntity<ProfilePrivateResponseDto> register(@RequestBody @Valid UserCreateDto dto) {
        log.info("POST /api/auth/register — User: {} trying to register", dto.username());
        ProfilePrivateResponseDto response = authService.addUser(dto);
        log.info("POST /api/auth/register - User created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Вход в систему",
            description = "Проверяет учетные данные и возвращает JWT-токен")
    @ApiResponse(responseCode = "200", description = "Успешная авторизация")
    @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        log.info("POST /api/auth/login - Login attempt for user: {}", request.username());
        AuthResponseDto login = authService.login(request);
        log.info("POST /api/auth/login - User successfully login as user: {}", request.username());
        return ResponseEntity.ok(login);
    }
}
