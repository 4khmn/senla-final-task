package com.project.velo.controller.profile;

import com.project.velo.dto.response.*;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;


    @GetMapping("/my")
    public ResponseEntity<ProfileResponseDto> getMyProfile(@AuthenticationPrincipal User user) {
        log.info("GET /api/profiles/my - Fetching profile for username = {}", user.getUsername());
        ProfileResponseDto profile = profileService.getByUsername(user.getUsername());
        log.info("GET /api/profiles/my - Profile for username: {} was successfully retrieved", user.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable String username) {
        log.info("GET /api/profiles/{} - Fetching profile for user: {}", username, username);
        ProfileResponseDto profile = profileService.getByUsername(username);
        log.info("GET /api/profiles/{} - Profile for user: {} was successfully retrieved", username, username);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/my")
    public ResponseEntity<ProfileResponseDto> updateProfileInfo(
            @RequestBody @Valid ProfileUpdateDto dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("PATCH /api/profiles/my - Fetching profile for username: {}", user.getUsername());
        ProfileResponseDto updated = profileService.update(dto, user.getUsername());
        log.info("PATCH /api/profiles/my - Profile for username: {} was successfully updated", user.getUsername());
        return ResponseEntity.ok(updated);
    }

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
