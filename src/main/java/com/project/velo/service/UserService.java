package com.project.velo.service;

import com.project.velo.dto.UserResponseDto;
import com.project.velo.entity.User;

import java.util.List;

public interface UserService {
    void updateRating(Long userId);

    UserResponseDto getById(Long id);

    void delete(Long id);

    void deactivate(Long id);

    List<UserResponseDto> getAll();
}