package com.project.velo.controller.infrastructure;

import com.project.velo.dto.infrastracture.MediaResource;
import com.project.velo.exception.FileNotFoundCustomException;
import com.project.velo.exception.GlobalExceptionHandler;
import com.project.velo.service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileStorageService storageService;

    @InjectMocks
    private ImageController imageController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getImage_ShouldReturnResource_Success() throws Exception {
        String folder = "avatars";
        String filename = "user1.jpg";
        String contentType = "image/jpeg";

        Resource mockResource = mock(Resource.class);
        MediaResource mediaResource = new MediaResource(mockResource, contentType);

        given(storageService.loadAsResource(folder, filename)).willReturn(mediaResource);

        mockMvc.perform(get("/api/images/{folder}/{filename}", folder, filename))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    void getImage_ShouldReturnNotFound() throws Exception {
        String folder = "bikes";
        String filename = "missing.png";

        given(storageService.loadAsResource(folder, filename))
                .willThrow(new FileNotFoundCustomException("Файл " + filename + " не найден или недоступен"));

        mockMvc.perform(get("/api/images/{folder}/{filename}", folder, filename))
                .andExpect(status().isNotFound());
    }
}