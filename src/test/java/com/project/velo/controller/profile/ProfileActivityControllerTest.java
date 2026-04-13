package com.project.velo.controller.profile;

import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.response.SalesHistoryResponseDto;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.SalesHistoryService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileActivityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalesHistoryService salesHistoryService;

    @Mock
    private AdvertisementService advertisementService;

    @InjectMocks
    private ProfileActivityController profileActivityController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileActivityController)
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
    void getMySales_ShouldReturnList() throws Exception {
        SalesHistoryResponseDto dto = new SalesHistoryResponseDto(
                1L, "Bike", 10L, new BigDecimal("500.00"), "buyer1", LocalDateTime.now()
        );
        given(salesHistoryService.getSales("testUser")).willReturn(List.of(dto));

        mockMvc.perform(get("/api/profiles/my/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].advertisementTitle").value("Bike"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getUserSales_ShouldReturnList() throws Exception {
        given(salesHistoryService.getSales("otherUser")).willReturn(List.of());

        mockMvc.perform(get("/api/profiles/otherUser/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getMyAdvertisements_ShouldReturnList() throws Exception {
        AdvertisementResponseDto dto = new AdvertisementResponseDto(
                1L, "Ad Title", "Desc", new BigDecimal("100"), "ACTIVE",
                false, LocalDateTime.now(), null, "Category", "url", List.of()
        );
        given(advertisementService.findAdvertisementsByUsername("testUser")).willReturn(List.of(dto));

        mockMvc.perform(get("/api/profiles/my/advertisements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Ad Title"));
    }

    @Test
    void getUserAdvertisements_ShouldReturnList() throws Exception {
        given(advertisementService.findAdvertisementsByUsername("someUser")).willReturn(List.of());

        mockMvc.perform(get("/api/profiles/someUser/advertisements"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}