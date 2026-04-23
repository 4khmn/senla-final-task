package com.project.velo.service.profile;

import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.ProfilePrivateResponseDto;
import com.project.velo.dto.response.ProfilePublicResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.Profile;
import com.project.velo.entity.User;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.ProfileMapper;
import com.project.velo.mapper.UserMapper;
import com.project.velo.repository.UserRepository;
import com.project.velo.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    void getPrivateByUsername_Success() {
        String username = "username";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        ProfilePrivateResponseDto profilePrivateResponseDto = new ProfilePrivateResponseDto(1L,
                "username",
                "email",
                "phone",
                "ROLE_ANY",
                new BigDecimal("0"),
                "firstName",
                "lastName",
                "bio",
                "avatarUrl",
                true,
                LocalDateTime.now()
        );
        given(userRepository.findByUsername("username")).willReturn(Optional.of(user));
        given(userMapper.toPrivateProfileDto(user)).willReturn(profilePrivateResponseDto);

        ProfilePrivateResponseDto result = profileService.getPrivateByUsername(username);

        assertNotNull(result);
        assertEquals(profilePrivateResponseDto, result);

        verify(userMapper).toPrivateProfileDto(any());
    }

    @Test
    void getPrivateByUsername_ShouldThrowEntityNotFoundException_WhenUsernameDoesNotExist() {

        String username = "username";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> profileService.getPrivateByUsername("username"));

        assertEquals("Пользователя с username " + username + " не найдено", result.getMessage());
        verifyNoInteractions(userMapper);
    }

    @Test
    void getPublicByUsername_Success() {
        String username = "username";

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        ProfilePublicResponseDto profilePublicResponseDto = new ProfilePublicResponseDto(1L,
                "username",
                new BigDecimal("0"),
                "firstName",
                "lastName",
                "bio",
                "avatarUrl",
                LocalDateTime.now()
        );
        given(userRepository.findByUsername("username")).willReturn(Optional.of(user));
        given(userMapper.toPublicProfileDto(user)).willReturn(profilePublicResponseDto);

        ProfilePublicResponseDto result = profileService.getPublicByUsername(username);

        assertNotNull(result);
        assertEquals(profilePublicResponseDto, result);

        verify(userMapper).toPublicProfileDto(any());
    }

    @Test
    void getPublicByUsername_ShouldThrowEntityNotFoundException_WhenUsernameDoesNotExist() {

        String username = "username";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> profileService.getPublicByUsername("username"));

        assertEquals("Пользователя с username " + username + " не найдено", result.getMessage());
        verifyNoInteractions(userMapper);
    }

    @Test
    void update_Success() {
        String username = "username";

        Profile profile = new Profile();
        profile.setFirstName("firstName");
        profile.setLastName("lastName");
        profile.setBio("bio");

        User user = new User();
        user.setProfile(profile);
        ProfileUpdateDto dto = new ProfileUpdateDto("", "newLastName", "", "");

        ProfilePrivateResponseDto profilePrivateResponseDto = new ProfilePrivateResponseDto(1L,
                "denis",
                "email",
                "phone",
                "ROLE_ANY",
                new BigDecimal("0"),
                "firstName",
                "newLastName",
                "bio",
                "avatarUrl",
                true,
                LocalDateTime.now()
        );
        given(userRepository.findByUsername("username")).willReturn(Optional.of(user));
        given(userMapper.toPrivateProfileDto(user)).willReturn(profilePrivateResponseDto);

        ProfilePrivateResponseDto result = profileService.update(dto, username);

        assertNotNull(result);
        assertEquals(profilePrivateResponseDto, result);
        verify(profileMapper).updateEntityFromDto(dto, user.getProfile());
        verify(userRepository).findByUsername(username);
    }


    @Test
    void update_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
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
    void updateAvatar_ShouldThrowEntityNotFoundException_WhenUsernameDoesNotExist() {
        String username = "username";
        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> profileService.updateAvatar(username, file));

        verifyNoInteractions(storageService);
    }

    @Test
    void getAllProfiles_ShouldReturnCorrectPageResponse() {
        int page = 0;
        int size = 2;
        User user1 = new User();
        List<User> users = List.of(user1);
        ProfilePrivateResponseDto profilePrivateResponseDto = new ProfilePrivateResponseDto(1L,
                "denis",
                "email",
                "phone",
                "ROLE_ANY",
                new BigDecimal("0"),
                "firstName",
                "lastName",
                "bio",
                "avatarUrl",
                true,
                LocalDateTime.now()
        );
        when(userRepository.findAll(page, size)).thenReturn(users);
        when(userRepository.count()).thenReturn(10L);
        when(userMapper.toPrivateProfileDto(any())).thenReturn(profilePrivateResponseDto);

        PageResponse<ProfilePrivateResponseDto> result = profileService.getAllProfiles(page, size);

        assertEquals(10L, result.totalElements());
        assertEquals(5, result.totalPages());
        assertEquals(1, result.content().size());
        verify(userRepository).findAll(page, size);
    }


    @Test
    void setUserStatus_ShouldChangeStatus_WhenStatusIsDifferent() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setEnabled(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        profileService.setUserStatus(username, false);

        assertFalse(user.isEnabled());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void setUserStatus_ShouldThrowException_WhenStatusIsSame() {
        String username = "testUser";
        User user = new User();
        user.setEnabled(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        ValidationException result = assertThrows(ValidationException.class,
                () -> profileService.setUserStatus(username, true));

        assertTrue(result.getMessage().contains("уже имеет enabled true"));
    }

    @Test
    void setUserStatus_ShouldThrowENFException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> profileService.setUserStatus("unknown", false));
    }
}
