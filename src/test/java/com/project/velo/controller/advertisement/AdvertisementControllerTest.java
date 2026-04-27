package com.project.velo.controller.advertisement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.request.AdvertisementFilterDto;
import com.project.velo.dto.response.advertisement.AdvertisementResponseDto;
import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.dto.response.profile.AuthorResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.update.AdvertisementPromoteDto;
import com.project.velo.dto.update.AdvertisementUpdateDto;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.service.advertisement.AdvertisementService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AdvertisementControllerTest {


    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private AdvertisementService advertisementService;

    @InjectMocks
    private AdvertisementController advertisementController;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(advertisementController)
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
    void getAllAdvertisements_ShouldReturnPageResponse_Success() throws Exception {
        AdvertisementShortResponseDto dto = new AdvertisementShortResponseDto(
                1L, "title", new BigDecimal("2000.00"), "categoryName",
                "img.jpg", true, LocalDateTime.now(), "seller", new BigDecimal("4.8")
        );
        AdvertisementFilterDto filter = new AdvertisementFilterDto(null, null, null, null, null);


        PageResponse<AdvertisementShortResponseDto> pageResponse = new PageResponse<>(
                List.of(dto), 1, 1, 0, 10
        );

        given(advertisementService.getAll(eq(filter), anyInt(), anyInt()))
                .willReturn(pageResponse);

        mockMvc.perform(get("/api/advertisements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.content[0].price").value(2000.00))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllAdvertisements_WithFilters_ShouldReturnFilteredPageResponse() throws Exception {

        AdvertisementFilterDto filter = new AdvertisementFilterDto("query", "category", new BigDecimal(0), new BigDecimal(100), null );

        PageResponse<AdvertisementShortResponseDto> emptyResponse = new PageResponse<>(
                List.of(), 0, 0, 0, 10
        );

        given(advertisementService.getAll(eq(filter), anyInt(), anyInt()))
                .willReturn(emptyResponse);

        mockMvc.perform(get("/api/advertisements")
                        .param("query", filter.query())
                        .param("category", filter.category())
                        .param("minPrice", "0")
                        .param("maxPrice", "100")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getAdvertisementById_ShouldReturnDto_Success() throws Exception {
        Long adId = 1L;
        AuthorResponseDto seller = new AuthorResponseDto(10L, "username", new BigDecimal("4.9"), "avatar.png");

        AdvertisementResponseDto responseDto = new AdvertisementResponseDto(
                adId,
                "title",
                "description",
                new BigDecimal("3500.00"),
                "ACTIVE",
                false,
                LocalDateTime.now(),
                seller,
                "categoryName",
                "main.jpg",
                List.of("side.jpg", "back.jpg")
        );

        given(advertisementService.getById(adId)).willReturn(responseDto);

        mockMvc.perform(get("/api/advertisements/{id}", adId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adId))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.price").value(3500.00))
                .andExpect(jsonPath("$.seller.username").value("username"))
                .andExpect(jsonPath("$.seller.rating").value(4.9))
                .andExpect(jsonPath("$.otherImageUrls.length()").value(2))
                .andExpect(jsonPath("$.otherImageUrls[0]").value("side.jpg"));
    }

    @Test
    void getAdvertisementById_ShouldReturnNotFound_WhenAdvertisementDoesNotExist() throws Exception {
        Long adId = 404L;
        given(advertisementService.getById(adId))
                .willThrow(new EntityNotFoundException("Объявления с id " + adId + " не найдено"));

        mockMvc.perform(get("/api/advertisements/{id}", adId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAdvertisement_ShouldReturnDto_Success() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "bike1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );

        AdvertisementResponseDto responseDto = new AdvertisementResponseDto(
                1L, "title", "description", new BigDecimal("2000.00"),
                "ACTIVE", false, LocalDateTime.now(),
                new AuthorResponseDto(1L, "testUser", BigDecimal.ZERO, null),
                "categoryName", "main-url", List.of()
        );

        given(advertisementService.create(any(AdvertisementCreateDto.class), anyList(), eq("testUser")))
                .willReturn(responseDto);

        mockMvc.perform(multipart("/api/advertisements")
                        .file(file1)
                        .param("title", "title")
                        .param("description", "description")
                        .param("price", "2000.00")
                        .param("categoryId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("title"));
    }

    @Test
    void createAdvertisement_ShouldReturnBadRequest_WhenFilesMissing() throws Exception {
        mockMvc.perform(multipart("/api/advertisements")
                        .param("title", "title")
                        .param("description", "description")
                        .param("price", "2000.00")
                        .param("categoryId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAdvertisement_Success() throws Exception {
        Long adId = 1L;
        String username = "testUser";

        willDoNothing().given(advertisementService).delete(adId, username);

        mockMvc.perform(delete("/api/advertisements/{id}", adId))
                .andExpect(status().isNoContent());

        verify(advertisementService).delete(adId, username);
    }

    @Test
    void deleteAdvertisement_ShouldReturnNotFound_WhenAdvertisementDoesNotExist() throws Exception {
        Long adId = 1L;
        willThrow(new EntityNotFoundException("Объявления с id " + adId + " не найдено"))
                .given(advertisementService).delete(adId, "testUser");

        mockMvc.perform(delete("/api/advertisements/{id}", adId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAdvertisement_ShouldReturnForbidden_WhenNotOwner() throws Exception {
        Long adId = 1L;
        willThrow(new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете удалить чужое объявление"))
                .given(advertisementService).delete(adId, "testUser");

        mockMvc.perform(delete("/api/advertisements/{id}", adId))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateAdvertisement_ShouldReturnDto_Success() throws Exception {
        Long adId = 1L;
        AdvertisementUpdateDto updateDto = new AdvertisementUpdateDto(
                "New Title", "New Desc", new BigDecimal("150.00"), 2L, List.of("img1.jpg")
        );

        AdvertisementResponseDto responseDto = new AdvertisementResponseDto(
                adId, "New Title", "New Desc", new BigDecimal("150.00"),
                "ACTIVE", false, LocalDateTime.now(),
                new AuthorResponseDto(1L, "testUser", BigDecimal.ZERO, null),
                "Components", "img1.jpg", List.of()
        );

        given(advertisementService.update(adId, updateDto, "testUser"))
                .willReturn(responseDto);

        mockMvc.perform(patch("/api/advertisements/{id}", adId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.price").value(150.00));
    }

    @Test
    void updateAdvertisement_ShouldReturnNotFound_WhenAdvertisementDoesNotExist() throws Exception {
        Long adId = 1L;
        AdvertisementUpdateDto updateDto = new AdvertisementUpdateDto("title", "desc", BigDecimal.TEN, 1L, List.of());

        given(advertisementService.update(adId, updateDto, "testUser"))
                .willThrow(new EntityNotFoundException("Объявления с id " + adId + " не найдено"));

        mockMvc.perform(patch("/api/advertisements/{id}", adId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAdvertisement_ShouldReturnBadRequest_WhenPriceIsNegative() throws Exception {
        Long adId = 1L;
        AdvertisementUpdateDto invalidDto = new AdvertisementUpdateDto(
                "title", "desc", new BigDecimal("-100.00"), 1L, List.of()
        );

        mockMvc.perform(patch("/api/advertisements/{id}", adId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buyAdvertisement_Success() throws Exception {
        Long adId = 1L;
        String username = "testUser";

        willDoNothing().given(advertisementService).processPurchase(adId, username);

        mockMvc.perform(post("/api/advertisements/{adId}/buy", adId))
                .andExpect(status().isNoContent());

        verify(advertisementService).processPurchase(adId, username);
    }

    @Test
    void promote_Success() throws Exception {
        Long adId = 1L;
        String username = "testUser";
        AdvertisementPromoteDto dto = new AdvertisementPromoteDto(7);

        willDoNothing().given(advertisementService).promote(adId, dto, username);

        mockMvc.perform(post("/api/advertisements/{adId}/promote", adId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(advertisementService).promote(eq(adId), any(AdvertisementPromoteDto.class), eq(username));
    }

    @Test
    void promote_ShouldReturnBadRequest_WhenDaysMoreThan30() throws Exception {
        Long adId = 1L;
        AdvertisementPromoteDto invalidDto = new AdvertisementPromoteDto(31);

        mockMvc.perform(post("/api/advertisements/{adId}/promote", adId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buyAdvertisement_ShouldReturnNotFound_WhenAdvertisementDoesNotExist() throws Exception {
        Long adId = 1L;
        willThrow(new EntityNotFoundException("Объявление с id " + adId + " не доступно"))
                .given(advertisementService).processPurchase(adId, "testUser");

        mockMvc.perform(post("/api/advertisements/{adId}/buy", adId))
                .andExpect(status().isNotFound());
    }
}
