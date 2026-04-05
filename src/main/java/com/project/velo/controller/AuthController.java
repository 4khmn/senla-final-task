package com.project.velo.controller;

import com.project.velo.dto.UserCreateDto;
import com.project.velo.dto.UserResponseDto;
import com.project.velo.dto.auth.AuthResponseDto;
import com.project.velo.dto.auth.LoginRequestDto;
import com.project.velo.security.JwtUtil;
import com.project.velo.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserCreateDto dto){
        log.info("POST /api/auth/register — user with username={} trying to register", dto.username());
        UserResponseDto response = authService.addUser(dto);
        log.info("POST /api/auth/register - user created successfully with id={}", response.id());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }






    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginRequestDto request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(
                userDetails.getUsername(),
                String.join(", ", userDetails.getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .toList())
        );

        return new AuthResponseDto(token);
    }
}
