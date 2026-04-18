package com.project.velo.service.storage;

import com.project.velo.dto.infrastracture.MediaResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileStorageServiceTest {

    private LocalFileStorageService storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageService = new LocalFileStorageService(tempDir.toAbsolutePath().toString());
    }

    @Test
    void save_ShouldReturnPath_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "content".getBytes());

        String resultPath = storageService.save(file, "ads");

        String fileName = resultPath.substring(resultPath.lastIndexOf("/") + 1);
        Path expectedFile = tempDir.resolve("ads").resolve(fileName);

        assertTrue(Files.exists(expectedFile));
    }

    @Test
    void delete_ShouldRemoveFile_Success() throws IOException {
        Path folder = tempDir.resolve("testFolder");
        Files.createDirectories(folder);
        Path file = folder.resolve("to_delete.png");
        Files.write(file, "content".getBytes());

        String dbPath = "/api/images/testFolder/to_delete.png";
        storageService.delete(dbPath);

        assertFalse(Files.exists(file));
    }

    @Test
    void loadAsResource_Success() throws IOException {
        Path folder = tempDir.resolve("items");
        Files.createDirectories(folder);
        Files.write(folder.resolve("bike.webp"), "data".getBytes());

        MediaResource resource = storageService.loadAsResource("items", "bike.webp");

        assertNotNull(resource);
        assertTrue(resource.resource().exists());
    }
}