package com.project.velo.controller.profile;

import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.dto.response.UserCommentResponseDto;
import com.project.velo.service.social.CommentService;
import com.project.velo.service.social.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProfileSocialControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ProfileSocialController profileSocialController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileSocialController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(UserDetails.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return User.withUsername("denis").password("pass").authorities("ROLE_USER").build();
                    }
                })
                .build();
    }

    @Test
    void getAllComments_ShouldReturnList() throws Exception {
        UserCommentResponseDto dto = new UserCommentResponseDto(
                1L, "Nice bike!", LocalDateTime.now(), 100L, "Giant TCR"
        );
        PageResponse<UserCommentResponseDto> pageResponse = new PageResponse<>(List.of(dto), 1, 1, 0, 10);
        given(commentService.getCommentsByUser("denis", 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/api/profiles/my/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Nice bike!"))
                .andExpect(jsonPath("$[0].advertisementTitle").value("Giant TCR"));
    }

    @Test
    void getMyReceivedReviews_ShouldReturnPageResponse() throws Exception {
        ReviewResponseDto dto = new ReviewResponseDto(
                1L, "Giant TCR", 100L, "buyer77",
                new BigDecimal("5.0"), "Great seller", LocalDateTime.now()
        );

        PageResponse<ReviewResponseDto> pageResponse = new PageResponse<>(List.of(dto), 1, 1, 0, 10);
        given(reviewService.getReviewsByUser("denis", null, null, 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/api/profiles/my/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].authorUsername").value("buyer77"))
                .andExpect(jsonPath("$.content[0].score").value(5.0));
    }

    @Test
    void getUserReceivedReviews_ShouldReturnPageResponse() throws Exception {
        given(reviewService.getReviewsByUser("maxim", null, null, 0, 10)).willReturn(new PageResponse<>(List.of(), 1, 1, 0, 10));

        mockMvc.perform(get("/api/profiles/maxim/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}