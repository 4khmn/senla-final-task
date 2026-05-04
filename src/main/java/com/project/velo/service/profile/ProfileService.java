package com.project.velo.service.profile;

import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.profile.ProfilePrivateResponseDto;
import com.project.velo.dto.response.profile.ProfilePublicResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.Profile;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.Role;
import com.project.velo.exception.UserDisabledException;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.ProfileMapper;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import com.project.velo.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;
    private final FileStorageService storageService;

    @Transactional(readOnly = true)
    public ProfilePublicResponseDto getPublicByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        if (!user.isEnabled()) {
            throw new UserDisabledException("Пользователь с username " + username + " деактивирован");
        }
        return userMapper.toPublicProfileDto(user);
    }

    @Transactional(readOnly = true)
    public ProfilePrivateResponseDto getPrivateByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        return userMapper.toPrivateProfileDto(user);
    }


    @Transactional
    public ProfilePrivateResponseDto update(ProfileUpdateDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        profileMapper.updateEntityFromDto(dto, user.getProfile());

        return userMapper.toPrivateProfileDto(user);
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


    @Transactional(readOnly = true)
    public PageResponse<ProfilePrivateResponseDto> getAllProfiles(Boolean enabled, Role role, int page, int size) {
        List<User> users = userRepository.findAllFiltered(enabled, role, page, size);

        long totalElements = userRepository.countFiltered(enabled, role);

        List<ProfilePrivateResponseDto> dtos = users.stream()
                .map(userMapper::toPrivateProfileDto)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                dtos,
                totalElements,
                totalPages,
                page,
                size
        );
    }

    @Transactional
    public void setUserStatus(String username, boolean enabled) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с именем " + username + " не найден"));

        if (user.isEnabled() == enabled) {
            throw new ValidationException("Пользователь " + username + " уже имеет enabled " + enabled);
        }

        user.setEnabled(enabled);

        if (enabled) {
            log.info("Admin UNBANNED user: {}", username);
        } else {
            log.info("Admin BANNED user: {}", username);
        }
    }
}
