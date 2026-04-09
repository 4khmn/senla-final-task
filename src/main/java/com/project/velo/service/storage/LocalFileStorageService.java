package com.project.velo.service.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService{

    private final String uploadPath = "uploads/";

    @Override
    public String save(MultipartFile file, String folder) {
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
}
