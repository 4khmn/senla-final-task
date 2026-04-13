package com.project.velo.service;

import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.Profile;
import com.project.velo.entity.User;
import com.project.velo.mapper.ProfileMapper;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import com.project.velo.service.profile.ProfileService;
import com.project.velo.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private FileStorageService storageService;

    @InjectMocks
    private ProfileService profileService;

    @Test
    void getByUsername_success() {
        String username = "username";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        ProfileResponseDto profileResponseDto = new ProfileResponseDto(1L, "username", "", BigDecimal.ZERO, "", "", "", "");
        given(userRepository.findByUsername("username")).willReturn(Optional.of(user));
        given(userMapper.toProfileDto(user)).willReturn(profileResponseDto);

        ProfileResponseDto result = profileService.getByUsername(username);

        assertNotNull(result);
        assertEquals(profileResponseDto, result);

        verify(userMapper).toProfileDto(any());
    }


    @Test
    void getByUsername_UsernameDoesNotExist_ThrowEntityNotFoundException() {

        String username = "username";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> profileService.getByUsername("username"));

        assertEquals("Пользователя с username " + username + " не найдено", result.getMessage());
        verifyNoInteractions(userMapper);
    }

    @Test
    void update_success() {
        String username = "username";

        Profile profile = new Profile();
        profile.setFirstName("firstName");
        profile.setLastName("lastName");
        profile.setBio("bio");

        User user = new User();
        user.setProfile(profile);
        ProfileUpdateDto dto = new ProfileUpdateDto("", "newLastName", "", "");

        ProfileResponseDto  profileResponseDto = new ProfileResponseDto(1L, "username", "", BigDecimal.ZERO, "firstName", "newLastName", "bio", "");
        given(userRepository.findByUsername("username")).willReturn(Optional.of(user));
        given(userMapper.toProfileDto(user)).willReturn(profileResponseDto);

        ProfileResponseDto result = profileService.update(dto, username);

        assertNotNull(result);
        assertEquals(profileResponseDto, result);
        verify(profileMapper).updateEntityFromDto(dto, user.getProfile());
        verify(userRepository).findByUsername(username);
    }


    @Test
    void update_UserDoesNotExist_ThrowEntityNotFoundException() {
        String username = "username";
        ProfileUpdateDto dto = new ProfileUpdateDto("", "", "", "");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> profileService.update(dto, username));

        assertEquals("Пользователя с username " + username + " не найдено", result.getMessage());

        verifyNoInteractions(profileMapper);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void updateAvatar_OldAvatarIsNotNull_Success() {
        String username = "username";
        String oldUrl = "/api/images/avatars/old.jpg";
        String newUrl = "/api/images/avatars/new.jpg";
        MockMultipartFile file = new MockMultipartFile(
                "file", "new_photo.jpg", "image/jpeg", "content".getBytes());

        User user = new User();
        Profile profile = new Profile();
        profile.setAvatarUrl(oldUrl);
        user.setProfile(profile);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(storageService.save(file, "avatars")).thenReturn(newUrl);

        String result = profileService.updateAvatar(username, file);

        assertEquals(newUrl, result);
        assertEquals(newUrl, profile.getAvatarUrl());

        verify(storageService).delete(oldUrl);
        verify(storageService).save(file, "avatars");
    }

    @Test
    void updateAvatar_OldAvatarIsNull_Success() {
        String username = "denis_pro";
        String newUrl = "/api/images/avatars/first.jpg";
        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());

        User user = new User();
        user.setProfile(new Profile());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(storageService.save(file, "avatars")).thenReturn(newUrl);

        profileService.updateAvatar(username, file);

        verify(storageService, never()).delete(anyString());
        verify(storageService).save(file, "avatars");
    }

    @Test
    void updateAvatar_UsernameDoesNotExist_ThrowEntityNotFoundException() {
        String username = "username";
        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> profileService.updateAvatar(username, file));

        verifyNoInteractions(storageService);
    }
}
