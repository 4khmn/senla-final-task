package com.project.velo.controller.admin;

import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.ProfileFullResponseDto;
import com.project.velo.dto.response.ProfilePublicResponseDto;
import com.project.velo.service.auth.AuthService;
import com.project.velo.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final ProfileService profileService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<PageResponse<ProfileFullResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/users - Admin fetching all users, page: {}, size: {}", page, size);
        PageResponse<ProfileFullResponseDto> users = profileService.getAllProfiles(page, size);
        log.info("GET /api/admin/users - Found: {} users, page: {}, size: {}", users.size(), page, size);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{username}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable String username,
            @RequestParam boolean enabled
    ) {
        log.info("PATCH /api/admin/users/{}/status - Admin changing status to: {}", username, enabled);
        profileService.setUserStatus(username, enabled);
        log.info("PATCH /api/admin/users/{}/status - Status successfully updated to: {}", username, enabled);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ProfileFullResponseDto> createAdmin(@RequestBody @Valid UserCreateDto dto) {
        log.info("POST /api/admin/users - Admin is creating a new administrator: {}", dto.username());
        ProfileFullResponseDto response = authService.addAdmin(dto);
        log.info("POST /api/admin/users - Admin created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
