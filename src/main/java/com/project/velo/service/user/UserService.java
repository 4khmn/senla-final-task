package com.project.velo.service.user;

import com.project.velo.dto.response.ProfileResponseDto;

import java.util.List;

public interface UserService {
    void updateRating(Long userId);

    ProfileResponseDto getById(Long id);

    void delete(Long id);

    void deactivate(Long id);

    List<ProfileResponseDto> getAll();
}