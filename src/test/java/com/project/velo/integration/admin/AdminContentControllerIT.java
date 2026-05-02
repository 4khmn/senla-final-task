package com.project.velo.integration.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.integration.BaseIT;
import com.project.velo.repository.CategoryRepository;
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
public class AdminContentControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Sql("/sql/admin/init_content_admin.sql")
    void getAllReview_ShouldReturnListOfReviews() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/admin/admin_reviews_response.json"));

        mockMvc.perform(get("/api/admin/content/reviews")
                .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/admin/init_content_admin.sql")
    void getAllAdvertisements_ShouldReturnListOfAdvertisements() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/admin/admin_advertisements_response.json"));

        mockMvc.perform(get("/api/admin/content/advertisements")
                        .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/admin/init_content_admin.sql")
    void createCategory_ShouldCreatedCategory() throws Exception {

        CategoryCreateDto categoryCreateDto = new CategoryCreateDto("name");

        mockMvc.perform(post("/api/admin/content/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryCreateDto))
                        .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isCreated())
                .andExpect(content().json("{'id': 1, 'name': 'name'}"));
    }

    @Test
    @Sql("/sql/admin/init_content_admin.sql")
    void updateCategory_ShouldUpdateCategory() throws Exception {

        CategoryUpdateDto categoryUpdateDto = new CategoryUpdateDto("new_name");

        mockMvc.perform(patch("/api/admin/content/categories/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryUpdateDto))
                        .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id': 2, 'name': 'new_name'}"));
    }

    @Test
    @Sql("/sql/admin/init_content_admin.sql")
    void deleteCategory_ShouldDeleteCategory() throws Exception {


        mockMvc.perform(delete("/api/admin/content/categories/3")
                        .with(user("admin-user").roles("ADMIN")))
                .andExpect(status().isNoContent());

        assertThrows(EntityNotFoundException.class, () ->
                categoryRepository.findById(3L).orElseThrow(
                () -> new EntityNotFoundException("Отзыва с id 100 не найдено")));
    }








}
