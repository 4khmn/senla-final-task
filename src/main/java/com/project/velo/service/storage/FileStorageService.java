package com.project.velo.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String save(MultipartFile file, String folder);
    void delete(String filePath);
    Resource load(String folder, String filename);
}
