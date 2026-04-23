package com.project.velo.controller.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.service.social.ReviewService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new  ObjectMapper();

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
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
    void leaveReview_ShouldReturnDto_Success() throws Exception{
        ReviewCreateDto request = new ReviewCreateDto(BigDecimal.ONE, "content");
        Long adId = 1L;
        ReviewResponseDto response = new ReviewResponseDto(1L, "title", 1L, "testUser",  BigDecimal.ONE, "content", LocalDateTime.now());

        given(reviewService.leaveReview(adId, request, "testUser")).willReturn(response);

        mockMvc.perform(post("/api/reviews/advertisement/{adId}", adId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    void leaveReview_ShouldReturnBadRequest_WhenScoreMoreThan5() throws Exception{
        ReviewCreateDto request = new ReviewCreateDto(new BigDecimal("6"), "content");
        Long adId = 1L;

        mockMvc.perform(post("/api/reviews/advertisement/{adId}", adId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deleteReview_Success() throws Exception {
        mockMvc.perform(delete("/api/reviews/{idId}", 1))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(eq(1L), any());
    }
}
