package com.project.velo.service.storage;

import com.project.velo.dto.infrastracture.MediaResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String save(MultipartFile file, String folder);
    void delete(String filePath);
    MediaResource loadAsResource(String folder, String filename);
}
