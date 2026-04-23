package com.project.velo.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.response.profile.ProfilePrivateResponseDto;
import com.project.velo.dto.response.profile.ProfilePublicResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.service.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(UserDetails.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return User.withUsername("denis").password("pass").authorities("ROLE_USER").build();
                    }
                })
                .build();
    }

    @Test
    void getMyProfile_Success() throws Exception {
        ProfilePrivateResponseDto profilePrivateResponseDto = new ProfilePrivateResponseDto(1L,
                "denis",
                "email",
                "phone",
                "ROLE_ANY",
                new BigDecimal("0"),
                "firstName",
                "lastName",
                "bio",
                "avatarUrl",
                true,
                LocalDateTime.now()
        );
        when(profileService.getPrivateByUsername(any())).thenReturn(profilePrivateResponseDto);

        mockMvc.perform(get("/api/profiles/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("denis"))
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void getProfile_Success() throws Exception {
        ProfilePublicResponseDto response = new ProfilePublicResponseDto(
                2L, "maxim", BigDecimal.ZERO, "firstName", "lastName", "bio", "avatar", LocalDateTime.now()
        );
        when(profileService.getPublicByUsername("maxim")).thenReturn(response);

        mockMvc.perform(get("/api/profiles/maxim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maxim"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void updateProfileInfo_Success() throws Exception {
        ProfileUpdateDto updateDto = new ProfileUpdateDto("firstName2", null, "+79998888776", null);
        ProfilePrivateResponseDto profilePrivateResponseDto = new ProfilePrivateResponseDto(1L,
                "denis",
                "email",
                "+79998888776",
                "ROLE_ANY",
                new BigDecimal("0"),
                "firstName2",
                "lastName",
                "bio",
                "avatarUrl",
                true,
                LocalDateTime.now()
        );

        when(profileService.update(any(ProfileUpdateDto.class), any())).thenReturn(profilePrivateResponseDto);

        mockMvc.perform(patch("/api/profiles/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("firstName2"));
    }

    @Test
    void updateProfileInfo_ShouldReturnBadRequest_WhenInvalidPhone() throws Exception {

        ProfileUpdateDto updateDto = new ProfileUpdateDto("firstName2", null, "invalid", null);

        mockMvc.perform(patch("/api/profiles/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void uploadAvatar_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "data".getBytes());
        when(profileService.updateAvatar(eq("denis"), any())).thenReturn("http://url.com/avatar.jpg");
        mockMvc.perform(multipart("/api/profiles/my/avatar").file(file))
                .andExpect(status().isNoContent());
    }
}