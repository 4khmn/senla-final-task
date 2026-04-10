package com.project.velo.service.user;

import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponseDto getByUsername(String username);

    ProfileResponseDto update(ProfileUpdateDto dto, String username);

    String updateAvatar(String username, MultipartFile file);
}
