package com.project.velo.service.storage;

import com.project.velo.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService{

    private final String uploadPath = "uploads/";
    private final List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/webp");

    @Override
    public String save(MultipartFile file, String folder) {
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new InvalidFileException("Недопустимый формат файла! Разрешены только: JPEG, PNG, WEBP");
        }
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadPath + folder + "/" + fileName);

            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            return "/api/images/" + folder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла", e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            String relativePath = filePath.replace("/api/images/", "uploads/");
            Path path = Paths.get(relativePath);

            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Cannot delete file {}", filePath, e);
        }
    }

    @Override
    public Resource load(String folder, String filename) {
        try {
            // Paths.get склеивает путь
            Path file = Paths.get(uploadPath, folder, filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не найден: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка при чтении файла", e);
        }
    }
}
