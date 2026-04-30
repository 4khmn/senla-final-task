package com.project.velo.integration.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.integration.BaseIT;
import com.project.velo.repository.ReviewRepository;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReviewControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql("/sql/social/init_reviews.sql")
    void leaveReview_ShouldReturnConflict_WhereReviewAlreadyExists() throws Exception {
        ReviewCreateDto dto = new ReviewCreateDto(4, "New Review Content");
        mockMvc.perform(post("/api/reviews/advertisement/1")
                        .with(user("author_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @Sql("/sql/social/init_reviews.sql")
    void leaveReview_ShouldReturnReview() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/social/review_response.json"));

        ReviewCreateDto dto = new ReviewCreateDto(4, "New Review Content");
        mockMvc.perform(post("/api/reviews/advertisement/2")
                        .with(user("author_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @Sql("/sql/social/init_reviews.sql")
    void deleteReview_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/reviews/100")
                        .with(user("author_user")))
                .andExpect(status().isNoContent());

        assertThrows(EntityNotFoundException.class, () ->
                reviewRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Отзыва с id 100 не найдено")));

    }

    @Test
    @Sql("/sql/social/init_reviews.sql")
    void deleteReview_ShouldReturnForbidden_WhenNotAuthor() throws Exception {
        mockMvc.perform(delete("/api/reviews/100")
                        .with(user("seller_user")))
                .andExpect(status().isForbidden());
    }
}