package com.project.velo.controller.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.profile.AuthorResponseDto;
import com.project.velo.dto.response.comment.CommentDetailsResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.update.CommentUpdateDto;
import com.project.velo.exception.AdvertisementNotAvailableException;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.service.social.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(UserDetails.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return User.withUsername("testUser").password("pass").authorities("ROLE_USER").build();
                    }
                })
                .build();
    }

    @Test
    void postComment_ShouldReturnDto_Success() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto("content");
        AuthorResponseDto author = new AuthorResponseDto(1L, "testUser", new BigDecimal("5.0"), "avatar");
        CommentDetailsResponseDto response = new CommentDetailsResponseDto(1L, "content", LocalDateTime.now(), author);
        Long adId = 1L;
        given(commentService.postComment(adId, commentCreateDto, "testUser")).willReturn(response);

        mockMvc.perform(post("/api/comments/advertisement/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void postComment_ShouldReturnBadRequest_WhenContentIsBlank() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto("");

        mockMvc.perform(post("/api/comments/advertisement/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getComments_ShouldReturnPageResponse_Success() throws Exception {
        Long adId = 1L;

        PageResponse<CommentDetailsResponseDto> pageResponse = new PageResponse<>(List.of(), 10, 1, 0, 10);
        given(commentService.getCommentsByAdvertisement(adId, 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/api/comments/advertisement/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void delete_Success() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(delete("/api/comments/" + commentId))
                .andExpect(status().isNoContent());
        verify(commentService).delete(1L, "testUser");
    }


    @Test
    void delete_ShouldReturnNotFound_WhenCommentDoesNotExist() throws Exception {
        Long commentId = 999L;

        willThrow(new EntityNotFoundException("Комментария с id " + commentId + " не найдено"))
                .given(commentService).delete(commentId, "testUser");

        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturnForbidden_WhenNotOwner() throws Exception {
        Long commentId = 1L;

        willThrow(new NotEnoughRightsException("Нельзя удалять чужое"))
                .given(commentService).delete(eq(commentId), anyString());

        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_ShouldReturnNotFound_WhenAdvertisementDoesNotActive() throws Exception {
        Long commentId = 1L;

        willThrow(new AdvertisementNotAvailableException("Объявление с id 1 не доступно"))
                .given(commentService).delete(eq(commentId), anyString());

        mockMvc.perform(delete("/api/comments/{commentId}", commentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ShouldReturnDto_Success() throws Exception {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("content");
        AuthorResponseDto author = new AuthorResponseDto(1L, "testUser", new BigDecimal("5.0"), "avatar");
        CommentDetailsResponseDto response = new CommentDetailsResponseDto(1L, "content", LocalDateTime.now(), author);
        Long commentId = 1L;

        given(commentService.update(commentId, commentUpdateDto, "testUser")).willReturn(response);

        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateComment_ShouldReturnNotFound_WhenCommentDoesNotExist() throws Exception {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("content");
        Long commentId = 1L;

        given(commentService.update(commentId, commentUpdateDto, "testUser"))
                .willThrow(new EntityNotFoundException("Комментария с id " + commentId + " не найдено"));
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ShouldReturnNotFound_WhenAdvertisementDoesNotActive() throws Exception {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("content");
        Long commentId = 1L;

        given(commentService.update(commentId, commentUpdateDto, "testUser"))
                .willThrow(new AdvertisementNotAvailableException("Объявление с id 1 не доступно"));
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_ShouldReturnForbidden_WhenNotOwner() throws Exception {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("content");
        Long commentId = 1L;

        given(commentService.update(commentId, commentUpdateDto, "testUser"))
                .willThrow(new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете удалять чужие комментарии"));
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenContentIsBlank() throws Exception {
        Long commentId = 1L;
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto("");

        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void togglePin_ShouldPinComment() throws Exception {
        Long commentId = 1L;
        mockMvc.perform(patch("/api/comments/{commentId}/pin", commentId))
                .andExpect(status().isNoContent());

        verify(commentService).togglePin(eq(commentId), anyString());
    }
}
