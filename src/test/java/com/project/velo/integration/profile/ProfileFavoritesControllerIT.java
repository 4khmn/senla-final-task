package com.project.velo.integration.profile;

import com.project.velo.integration.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProfileFavoritesControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/sql/profile/init_favorites.sql")
    void getMyFavorites_ShouldReturnCurrentUserFavorites() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_favorites_response.json"));

        mockMvc.perform(get("/api/profiles/my/favorites")
                        .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_favorites.sql")
    void addToFavorite_ShouldAddToCurrentUserFavorites() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_added_to_favorites_response.json"));

        mockMvc.perform(post("/api/profiles/my/favorites")
                        .param("adId", "501")
                        .with(user("owner_user")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/profiles/my/favorites")
                        .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }


    @Test
    @Sql("/sql/profile/init_favorites.sql")
    void removeFromFavorite_ShouldRemoveFromCurrentUserFavorites() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_removed_from_favorites_response.json"));

        mockMvc.perform(delete("/api/profiles/my/favorites")
                        .param("adId", "500")
                        .with(user("owner_user")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/profiles/my/favorites")
                        .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
