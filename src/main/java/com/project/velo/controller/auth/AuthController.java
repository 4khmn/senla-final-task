package com.project.velo.controller.auth;

import com.project.velo.dto.response.profile.ProfilePrivateResponseDto;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.auth.AuthResponseDto;
import com.project.velo.dto.auth.LoginRequestDto;
import com.project.velo.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<ProfilePrivateResponseDto> register(@RequestBody @Valid UserCreateDto dto) {
        log.info("POST /api/auth/register — User: {} trying to register", dto.username());
        ProfilePrivateResponseDto response = authService.addUser(dto);
        log.info("POST /api/auth/register - User created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        log.info("POST /api/auth/login - Login attempt for user: {}", request.username());
        AuthResponseDto login = authService.login(request);
        log.info("POST /api/auth/login - User successfully login as user: {}", request.username());
        return ResponseEntity.ok(login);
    }
}
