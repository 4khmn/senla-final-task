package com.project.velo.controller.infrastructure;

import com.project.velo.dto.infrastracture.MediaResource;
import com.project.velo.service.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Images", description = "Управление изображениями")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final FileStorageService storageService;

    @Operation(summary = "Получение изображения")
    @ApiResponse(responseCode = "404", description = "Изображение не найдено")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String folder, @PathVariable String filename) {
        log.info("GET /api/images/{}/{} - Fetching image by folder: {} and by filename: {}", folder, filename, folder, filename);
        MediaResource media = storageService.loadAsResource(folder, filename);
        log.info("GET /api/images/{}/{} - Image was successfully retrieved: {}", folder, filename, media);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(media.contentType()))
                .body(media.resource());
    }
}
