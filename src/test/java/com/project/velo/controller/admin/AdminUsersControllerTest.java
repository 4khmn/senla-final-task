package com.project.velo.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.profile.ProfilePrivateResponseDto;
import com.project.velo.exception.CustomSecurityExceptionHandler;
import com.project.velo.security.JwtUtil;
import com.project.velo.security.SecurityConfig;
import com.project.velo.service.auth.AuthService;
import com.project.velo.service.profile.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@Import({SecurityConfig.class, CustomSecurityExceptionHandler.class})
public class AdminUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnOk() throws Exception {
        PageResponse<ProfilePrivateResponseDto> pageResponse = new PageResponse<>(List.of(), 0, 0, 0, 20);

        given(profileService.getAllProfiles(0, 20)).willReturn(pageResponse);

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_ShouldReturnNoContent() throws Exception {
        String username = "testuser";
        boolean enabled = false;

        mockMvc.perform(patch("/api/admin/users/{username}/status", username)
                        .param("enabled", String.valueOf(enabled)))
                .andExpect(status().isNoContent());

        verify(profileService).setUserStatus(username, enabled);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAdmin_ShouldReturnCreated() throws Exception {
        UserCreateDto dto = new UserCreateDto("new_admin", "Password123!", "admin@velo.com", "Admin", "Adminov");
        ProfilePrivateResponseDto profilePrivateResponseDto = new ProfilePrivateResponseDto(1L,
                "new_admin",
                "admin@velo.com",
                "phone",
                "ROLE_ADMIN",
                new BigDecimal("0"),
                "Admin",
                "Adminov",
                "",
                "",
                true,
                LocalDateTime.now()
        );

        given(authService.addAdmin(any(UserCreateDto.class))).willReturn(profilePrivateResponseDto);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("new_admin"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.email").value("admin@velo.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createAdmin_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        UserCreateDto dto = new UserCreateDto("hacker", "Pass123!", "h@h.com", "H", "H");

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
