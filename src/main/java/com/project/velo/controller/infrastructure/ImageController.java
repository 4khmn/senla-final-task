package com.project.velo.controller.infrastructure;

import com.project.velo.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final String uploadPath = "uploads/";
    private final FileStorageService storageService;

    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String folder, @PathVariable String filename) {
        Resource file = storageService.load(folder, filename);
        String contentType = "application/octet-stream"; // Тип по умолчанию
        try {
            contentType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException e) {
            log.error("GET /api/images/{}/{} - Impossible to parse content type: {}",  folder, filename, filename, e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}
