package com.project.velo.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.response.advertisement.AdvertisementResponseDto;
import com.project.velo.dto.response.advertisement.CategoryResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.review.ReviewResponseDto;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.security.JwtUtil;
import com.project.velo.security.SecurityConfig;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.CategoryService;
import com.project.velo.service.social.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminContentController.class)
@Import(SecurityConfig.class)
public class AdminContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private AdvertisementService advertisementService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReviews_ShouldReturnOk() throws Exception {
        PageResponse<ReviewResponseDto> pageResponse = new PageResponse<>(List.of(), 0, 0, 0, 20);
        given(reviewService.getAllReviews(0, 20)).willReturn(pageResponse);

        mockMvc.perform(get("/api/admin/content/reviews")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllReviews_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/content/reviews"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAdvertisements_ShouldReturnOk() throws Exception {
        PageResponse<AdvertisementResponseDto> pageResponse = new PageResponse<>(List.of(), 0, 0, 0, 20);
        given(advertisementService.getAllForAdmin(0, 20)).willReturn(pageResponse);

        mockMvc.perform(get("/api/admin/content/advertisements")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllAdvertisements_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/content/advertisements"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_ShouldReturnDto_Success() throws Exception {
        CategoryUpdateDto request = new CategoryUpdateDto("name");
        CategoryResponseDto response = new CategoryResponseDto(1L, "name");
        given(categoryService.update(1L, request)).willReturn(response);

        mockMvc.perform(patch("/api/admin/content/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCategory_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {

        CategoryUpdateDto request = new CategoryUpdateDto("");


        mockMvc.perform(patch("/api/admin/content/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_Success() throws Exception {

        mockMvc.perform(delete("/api/admin/content/categories/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(categoryService).delete(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_ShouldReturnDto_Success() throws Exception {

        CategoryCreateDto request = new CategoryCreateDto("name");
        CategoryResponseDto response = new CategoryResponseDto(1L, "name");
        given(categoryService.create(request)).willReturn(response);

        mockMvc.perform(post("/api/admin/content/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {

        CategoryCreateDto request = new CategoryCreateDto("");


        mockMvc.perform(post("/api/admin/content/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
