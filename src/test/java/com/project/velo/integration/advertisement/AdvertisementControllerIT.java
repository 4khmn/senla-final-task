package com.project.velo.integration.advertisement;

import com.project.velo.entity.Advertisement;
import com.project.velo.integration.BaseIT;
import com.project.velo.repository.AdvertisementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AdvertisementControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Test
    @Sql("/sql/advertisement/init_ads.sql")
    void getAllAdvertisements_ShouldReturnAdvertisements() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/advertisement/ads_list_response.json"));

        mockMvc.perform(get("/api/advertisements")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/advertisement/init_ads_filter.sql")
    void getAllAdvertisements_WithComplexFilter_ShouldReturnCorrectResult() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/advertisement/ads_filtered_response.json"));

        mockMvc.perform(get("/api/advertisements")
                        .param("minPrice", "50000")
                        .param("maxPrice", "100000")
                        .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }


    @Test
    @Sql("/sql/advertisement/init_ads.sql")
    void getAdvertisementById_ShouldReturnAdvertisement() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/advertisement/ad_response.json"));

        mockMvc.perform(get("/api/advertisements/100"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/advertisement/init_ads.sql")
    void createAdvertisement_ShouldReturnCreatedAdvertisement() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/advertisement/ad_created_response.json"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "image-content".getBytes()
        );
        mockMvc.perform(multipart("/api/advertisements")
                        .file(file)
                        .param("title", "title")
                        .param("description", "description")
                        .param("price", "500.00")
                        .param("categoryId", "1")
                        .with(user("seller1"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedJson));

        Advertisement savedAd = advertisementRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Объявление не найдено в БД!"));
        assertEquals("title", savedAd.getTitle());
    }

    @Test
    @Sql("/sql/advertisement/init_ads.sql")
    void updateAdvertisement_ShouldReturnUpdatedAdvertisement() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/advertisement/ad_updated_response.json"));

        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "bike1.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-content".getBytes()
        );
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/advertisements/100")
                        .file(file1)
                        .param("title", "New Title")
                        .param("price", "150.00")
                        .with(user("seller1")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson))

                .andExpect(jsonPath("$.primaryImageUrl",
                        matchesPattern("^/api/images/advertisements/[a-f0-9\\-]+_bike1\\.jpg$")));
    }
}