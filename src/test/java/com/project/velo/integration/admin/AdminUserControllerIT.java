package com.project.velo.integration.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.entity.User;
import com.project.velo.integration.BaseIT;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AdminUserControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Sql("/sql/admin/init_users_admin.sql")
    void getAllUsers_ShouldReturnUsers() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/admin/admin_users_response.json"));

        mockMvc.perform(get("/api/admin/users")
                        .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/admin/init_users_admin.sql")
    void updateUserStatus_ShouldSetUserEnabled() throws Exception {

        mockMvc.perform(patch("/api/admin/users/other_user/status")
                        .param("enabled", "true")
                        .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isNoContent());


        User user = userRepository.findByUsername("other_user").orElseThrow
                (() -> new EntityNotFoundException("Пользователя с username other_user не найдено"));

        assertTrue(user.isEnabled());
    }

    @Test
    @Sql("/sql/admin/init_users_admin.sql")
    void createAdmin_ShouldReturnCreatedAdmin() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/admin/admin_created_response.json"));
        UserCreateDto userCreateDto = new UserCreateDto(
                "new-admin",
                "Password1!",
                "email@new.com",
                "firstName",
                "lastName"
        );

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto))
                        .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson, false));
    }
}
