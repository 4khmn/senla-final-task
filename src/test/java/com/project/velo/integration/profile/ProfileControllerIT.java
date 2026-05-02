package com.project.velo.integration.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.integration.BaseIT;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProfileControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getMyProfile_ShouldReturnPrivateInfo() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_private_response.json"));

        mockMvc.perform(get("/api/profiles/my")
                .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getProfile_ShouldReturnPublicInfo_WhenViewingOthers() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_public_response.json"));

        mockMvc.perform(get("/api/profiles/owner_user"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getProfile_ShouldReturnNotFound_WhenUsernameUnknown() throws Exception {
        mockMvc.perform(get("/api/profiles/other_user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void updateProfileInfo_ShouldReturnUpdatedProfile() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_update_response.json"));

        ProfileUpdateDto updateDto = new ProfileUpdateDto("firstName2", "secondName2", "+79998888776", "new bio");

        mockMvc.perform(patch("/api/profiles/my")
                        .with(user("owner_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void uploadAvatar_ShouldReturnNoContent() throws Exception {
        MockMultipartFile avatar = new MockMultipartFile(
                "file",
                "new_avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some-image-content".getBytes()
        );

        mockMvc.perform(multipart("/api/profiles/my/avatar")
                        .file(avatar)
                        .with(user("owner_user")))
                .andExpect(status().isNoContent());

        User updatedUser = userRepository.findByUsername("owner_user")
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с username owner_user не найдено"));

        String actualAvatarUrl = updatedUser.getProfile().getAvatarUrl();

        assertThat(actualAvatarUrl)
                .matches("^/api/images/avatars/[a-f0-9\\-]+_new_avatar\\.jpg$");

        String relativePath = actualAvatarUrl.replace("/api/images/", "");
        Path filePath = Path.of("uploads").resolve(relativePath);
        Files.deleteIfExists(filePath);
    }
}
