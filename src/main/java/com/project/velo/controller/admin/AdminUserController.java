package com.project.velo.controller.admin;

import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.profile.ProfilePrivateResponseDto;
import com.project.velo.service.auth.AuthService;
import com.project.velo.service.profile.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin: Users", description = "Управление пользователями")
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final ProfileService profileService;
    private final AuthService authService;

    @Operation(
            summary = "Список всех пользователей",
            description = "Позволяет админу просматривать всех пользователей системе для модерации"
    )
    @GetMapping
    public ResponseEntity<PageResponse<ProfilePrivateResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/users - Admin fetching all users, page: {}, size: {}", page, size);
        PageResponse<ProfilePrivateResponseDto> users = profileService.getAllProfiles(page, size);
        log.info("GET /api/admin/users - Found: {} users, page: {}, size: {}", users.size(), page, size);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Управление блокировкой пользователя",
            description = "Позволяет включить (разбанить) или выключить (забанить) учетную запись пользователя по его username"
    )
    @ApiResponse(responseCode = "204", description = "Блокировка успешно изменена")
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

    @Operation(
            summary = "Регистрация админа в системе",
            description = "Создание учетной записи с правами ADMIN. Доступно только действующим администраторам"
    )
    @ApiResponse(responseCode = "201", description = "Админ успешно создан")
    @PostMapping
    public ResponseEntity<ProfilePrivateResponseDto> createAdmin(@RequestBody @Valid UserCreateDto dto) {
        log.info("POST /api/admin/users - Admin is creating a new administrator: {}", dto.username());
        ProfilePrivateResponseDto response = authService.addAdmin(dto);
        log.info("POST /api/admin/users - Admin created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
