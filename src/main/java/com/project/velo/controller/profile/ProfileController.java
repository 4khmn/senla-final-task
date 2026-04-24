package com.project.velo.controller.profile;

import com.project.velo.dto.response.profile.ProfilePrivateResponseDto;
import com.project.velo.dto.response.profile.ProfilePublicResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.service.profile.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "Profile: General", description = "Управление пользователями: получение профиля, редактирование")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;


    @Operation(
            summary = "Получение профиля текущего пользователя",
            description = "Получение полной информации о текущем пользователе",
            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/my")
    public ResponseEntity<ProfilePrivateResponseDto> getMyProfile(@AuthenticationPrincipal User user) {
        log.info("GET /api/profiles/my - Fetching profile for username = {}", user.getUsername());
        ProfilePrivateResponseDto profile = profileService.getPrivateByUsername(user.getUsername());
        log.info("GET /api/profiles/my - Profile for username: {} was successfully retrieved", user.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Получение профиля пользователя по username",
            description = "Получение публичной информации о пользователе"
    )
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{username}")
    public ResponseEntity<ProfilePublicResponseDto> getProfile(@PathVariable String username) {
        log.info("GET /api/profiles/{} - Fetching profile for user: {}", username, username);
        ProfilePublicResponseDto profile = profileService.getPublicByUsername(username);
        log.info("GET /api/profiles/{} - Profile for user: {} was successfully retrieved", username, username);
        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Обновления информации текущего пользователя",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "200")
    @PatchMapping("/my")
    public ResponseEntity<ProfilePrivateResponseDto> updateProfileInfo(
            @RequestBody @Valid ProfileUpdateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("PATCH /api/profiles/my - Fetching profile for username: {}", user.getUsername());
        ProfilePrivateResponseDto updated = profileService.update(dto, user.getUsername());
        log.info("PATCH /api/profiles/my - Profile for username: {} was successfully updated", user.getUsername());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Обновления аватара",
            description = "Изображение передается файлом",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "204", description = "Аватар успешно обновлен")
    @PostMapping(value = "/my/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("POST /api/profiles/my/avatar - Uploading avatar for username: {}", user.getUsername());
        String newAvatarUrl = profileService.updateAvatar(user.getUsername(), file);
        log.info("POST /api/profiles/my/avatar - User {} updated avatar to {}", user.getUsername(), newAvatarUrl);
        return ResponseEntity.noContent().build();
    }

}
