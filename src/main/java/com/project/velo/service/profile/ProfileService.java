package com.project.velo.service.profile;

import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.Profile;
import com.project.velo.entity.User;
import com.project.velo.mapper.ProfileMapper;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import com.project.velo.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;
    private final FileStorageService storageService;

    @Transactional(readOnly = true)
    public ProfileResponseDto getByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        return userMapper.toProfileDto(user);
    }


    @Transactional
    public ProfileResponseDto update(ProfileUpdateDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        profileMapper.updateEntityFromDto(dto, user.getProfile());

        return userMapper.toProfileDto(user);
    }

    @Transactional
    public String updateAvatar(String username, MultipartFile file) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        String newAvatarUrl = storageService.save(file, "avatars");
        Profile profile = user.getProfile();
        if (profile.getAvatarUrl() != null) {
            storageService.delete(profile.getAvatarUrl());
        }
        profile.setAvatarUrl(newAvatarUrl);
        return newAvatarUrl;
    }
}
