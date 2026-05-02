package com.project.velo.integration.infrastructure;

import com.project.velo.integration.BaseIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

class ImageControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    private final Path testFilePath = Paths.get("uploads/avatars/test-image.jpg");

    @BeforeEach
    void setUp() throws IOException {
        Path directory = Paths.get("uploads/avatars").toAbsolutePath();
        Files.createDirectories(directory);

        Path file = directory.resolve("test-image.jpg");
        Files.write(file, "real-image-binary-content".getBytes());
    }


    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void getImage_ShouldReturnRealFileFromDisk() throws Exception {
        mockMvc.perform(get("/api/images/avatars/test-image.jpg"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().string("real-image-binary-content"));
    }

    @Test
    void getImage_ShouldReturnNotFound_WhenFileNotFoundOnDisk() throws Exception {
        mockMvc.perform(get("/api/images/avatars/non-existent.jpg"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}