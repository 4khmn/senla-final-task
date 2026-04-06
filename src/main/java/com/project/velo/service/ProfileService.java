package com.project.velo.service;

import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;

public interface ProfileService {

    ProfileResponseDto getByUsername(String username);

    ProfileResponseDto update(ProfileUpdateDto dto, String username);
}
