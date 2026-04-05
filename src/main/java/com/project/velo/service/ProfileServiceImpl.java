package com.project.velo.service;

import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.mapper.ProfileMapper;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponseDto getByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с таким username е найдено.")
        );
        return userMapper.toProfileDto(user);
    }


    @Override
    public ProfileResponseDto update(ProfileUpdateDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с таким username е найдено.")
        );
        profileMapper.updateEntityFromDto(dto, user.getProfile());

        return userMapper.toProfileDto(user);
    }
}
