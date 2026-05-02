package com.project.velo.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.auth.LoginRequestDto;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.integration.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Sql("/sql/auth/init_auth.sql")
    void register_ShouldReturnCreated() throws Exception {
        UserCreateDto dto = new UserCreateDto(
                "new_user",
                "Password123!",
                "new@test.com",
                "Ivan",
                "Ivanov"
        );

        String expectedJson = Files.readString(Path.of("src/test/resources/json/auth/auth_register_response.json"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson, false));
    }

    @Test
    @Sql("/sql/auth/init_auth.sql")
    void register_ShouldReturnConflict_WhenUserExists() throws Exception {
        UserCreateDto dto = new UserCreateDto(
                "existing_user",
                "Password123!",
                "existing@test.com",
                "Name",
                "Lastname"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @Sql("/sql/auth/init_auth.sql")
    void login_ShouldReturnToken_WhenCredentialsValid() throws Exception {
        LoginRequestDto request = new LoginRequestDto("existing_user", "Password1!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @Sql("/sql/auth/init_auth.sql")
    void login_ShouldReturnUnauthorized_WhenPasswordWrong() throws Exception {
        LoginRequestDto request = new LoginRequestDto("existing_user", "InvalidPassword1!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
