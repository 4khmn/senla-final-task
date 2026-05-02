package com.project.velo.integration.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.update.CommentUpdateDto;
import com.project.velo.integration.BaseIT;
import com.project.velo.repository.CommentRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CommentControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Sql("/sql/social/init_comments.sql")
    void postComment_ShouldReturnCreated() throws Exception {
        CommentCreateDto dto = new CommentCreateDto("content");

        mockMvc.perform(post("/api/comments/advertisement/1")
                        .with(user("commenter_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json("{'content': 'content', 'author': {'username': 'commenter_user'}}", false));
    }

    @Test
    @Sql("/sql/social/init_comments.sql")
    void getComments_ShouldReturnPage() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/social/comment_list_response.json"));

        mockMvc.perform(get("/api/comments/advertisement/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/social/init_comments.sql")
    void updateComment_ShouldReturnUpdatedDetails() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/social/comment_update_response.json"));
        CommentUpdateDto updateDto = new CommentUpdateDto("new content");

        mockMvc.perform(patch("/api/comments/100")
                        .with(user("commenter_user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson, false));
    }

    @Test
    @Sql("/sql/social/init_comments.sql")
    void deleteComment_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/comments/100")
                        .with(user("commenter_user")))
                .andExpect(status().isNoContent());

        assertThrows(EntityNotFoundException.class, () ->
                commentRepository.findById(100L).orElseThrow(
                () -> new EntityNotFoundException("Комментария с id 100 не найдено")));
    }

    @Test
    @Sql("/sql/social/init_comments.sql")
    void deleteComment_ShouldReturnForbidden_WhenNotAuthor() throws Exception {
        mockMvc.perform(delete("/api/comments/100")
                        .with(user("owner_user")))
                .andExpect(status().isForbidden());
    }
}