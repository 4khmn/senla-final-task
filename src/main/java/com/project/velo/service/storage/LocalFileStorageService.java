package com.project.velo.service.storage;

import com.project.velo.dto.infrastracture.MediaResource;
import com.project.velo.exception.FileNotFoundCustomException;
import com.project.velo.exception.FileStorageException;
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

    private final Path rootLocation;
    private final List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/webp");

    public LocalFileStorageService() {
        this.rootLocation = Paths.get("uploads");
    }

    public LocalFileStorageService(String uploadPath) {
        this.rootLocation = Paths.get(uploadPath);
    }

    @Override
    public String save(MultipartFile file, String folder) {
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new InvalidFileException("Недопустимый формат файла! Разрешены только: JPEG, PNG, WEBP");
        }
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            // склейка
            Path targetPath = this.rootLocation.resolve(folder).resolve(fileName);

            Files.createDirectories(targetPath.getParent());

            Files.write(targetPath, file.getBytes());

            return "/api/images/" + folder + "/" + fileName;
        } catch (IOException e) {
            log.error("Cannot save file: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Не удалось сохранить файл. Попробуйте позже.", e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            String relativePath = filePath.replace("/api/images/", "");
            Path file = this.rootLocation.resolve(relativePath).normalize();

            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.error("Cannot delete file {}", filePath, e);
        }
    }

    @Override
    public MediaResource loadAsResource(String folder, String filename) {
        Resource resource = load(folder, filename);

        String contentType;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (IOException e) {
            log.error("Could not determine file type for: {}", filename);
            contentType = "application/octet-stream";
        }

        return new MediaResource(resource, contentType);
    }



    private Resource load(String folder, String filename) {
        try {
            Path file = rootLocation.resolve(folder).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundCustomException("Файл " + filename + " не найден или недоступен");
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Внутренняя ошибка при чтении файла", e);
        }
    }
}
