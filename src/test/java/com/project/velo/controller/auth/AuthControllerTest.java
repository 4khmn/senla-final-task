package com.project.velo.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.auth.AuthResponseDto;
import com.project.velo.dto.auth.LoginRequestDto;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.response.ProfilePrivateResponseDto;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - Успешная регистрация")
    void register_ShouldReturnFullDto_Success() throws Exception {
        UserCreateDto createDto = new UserCreateDto(
                "denis1",
                "Password123!",
                "denis@example.com",
                "firstName",
                "lastName"
        );

        ProfilePrivateResponseDto responseDto = new ProfilePrivateResponseDto(
                1L, "denis1", "ROLE_USER", "phone", "ROLE_USER", BigDecimal.ZERO,
                "firstName", "lastName", null, null, true, LocalDateTime.now()
        );

        given(authService.addUser(any(UserCreateDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("denis1"));
    }

    @Test
    void login_Success() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("denis", "Password123!");
        AuthResponseDto authResponse = new AuthResponseDto("fake-jwt-token");

        given(authService.login(any(LoginRequestDto.class))).willReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }


    @Test
    void register_ShouldReturnBadRequest_WhenEmptyFields() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto(
                "",
                "short",
                "invalid-email",
                "",
                ""
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenShortUsername() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto(
                "den",
                "Password123!",
                "den@mail.com",
                "First",
                "Last"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

}