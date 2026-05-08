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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProfileSocialControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/sql/profile/init_social.sql")
    void getMyComments_ShouldReturnCommentsPage() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/user_comments_response.json"));

        mockMvc.perform(get("/api/profiles/my/comments")
                        .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_social.sql")
    void getMyReceivedReviews_ShouldReturnReviewsPage() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/user_reviews_response.json"));

        mockMvc.perform(get("/api/profiles/my/reviews/received")
                        .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_social.sql")
    void getUserReceivedReviews_ShouldReturnPublicReviewsPage() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/user_reviews_response.json"));

        mockMvc.perform(get("/api/profiles/owner_user/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_social.sql")
    void getUserReceivedReviews_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/profiles/non_existent_user/reviews")
                        .with(user("owner_user")))
                .andExpect(status().isNotFound());
    }
}