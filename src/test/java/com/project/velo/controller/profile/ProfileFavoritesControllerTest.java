package com.project.velo.controller.profile;

import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.service.profile.FavoritesService;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProfileFavoritesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FavoritesService favoritesService;

    @InjectMocks
    private ProfileFavoritesController profileFavoritesController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileFavoritesController)
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
    void getMyFavorites_ShouldReturnPageResponse() throws Exception {

        AdvertisementShortResponseDto dto = new AdvertisementShortResponseDto(
                1L,
                "title",
                BigDecimal.ONE,
                "categoryName",
                null,
                false,
                true,
                LocalDateTime.now(),
                "sellerUsername",
                BigDecimal.ONE
        );
        PageResponse<AdvertisementShortResponseDto> pageResponse = new PageResponse<>(List.of(dto), 1, 1, 0, 10);
        given(favoritesService.getAllByUser("denis", 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/api/profiles/my/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.content[0].sellerUsername").value("sellerUsername"));
    }

    @Test
    void addToFavorite_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(post("/api/profiles/my/favorites")
                        .param("adId", "1"))
                .andExpect(status().isNoContent());

        verify(favoritesService).addToFavorites("denis", 1L);
    }

    @Test
    void removeFromFavorite_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/profiles/my/favorites")
                        .param("adId", "1"))
                .andExpect(status().isNoContent());

        verify(favoritesService).deleteFromFavorites("denis", 1L);
    }

}
