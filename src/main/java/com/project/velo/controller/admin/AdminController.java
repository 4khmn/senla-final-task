package com.project.velo.controller.admin;

import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.auth.AuthService;
import com.project.velo.service.profile.ProfileService;
import com.project.velo.service.social.CommentService;
import com.project.velo.service.social.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ProfileService profileService;
    private final ReviewService reviewService;
    private final AdvertisementService advertisementService;
    private final AuthService authService;

    @GetMapping("/users")
    public ResponseEntity<PageResponse<ProfileResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/users - Admin fetching all users, page: {}, size: {}", page, size);
        PageResponse<ProfileResponseDto> users = profileService.getAllProfiles(page, size);
        log.info("GET /api/admin/users - Found: {} users, page: {}, size: {}", users.size(), page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/reviews")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/reviews - Admin fetching all reviews, page: {}, size: {}", page, size);
        PageResponse<ReviewResponseDto> reviews = reviewService.getAllReviews(page, size);
        log.info("GET /api/admin/reviews - Found: {} reviews, page: {}, size: {}", reviews.size(), page, size);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/advertisements")
    public ResponseEntity<PageResponse<AdvertisementResponseDto>> getAllAdvertisements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/advertisements - Admin fetching all advertisements, page: {}, size: {}", page, size);
        PageResponse<AdvertisementResponseDto> ads = advertisementService.getAllForAdmin(page, size);
        log.info("GET /api/admin/advertisements - Found: {} advertisements, page: {}, size: {}", ads.size(), page, size);
        return ResponseEntity.ok(ads);
    }

    @PatchMapping("/users/{username}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable String username,
            @RequestParam boolean enabled
    ) {
        log.info("PATCH /api/admin/users/{}/status - Admin changing status to: {}", username, enabled);
        profileService.setUserStatus(username, enabled);
        log.info("PATCH /api/admin/users/{}/status - Status successfully updated to: {}", username, enabled);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-admin")
    public ResponseEntity<ProfileResponseDto> createAdmin(@RequestBody @Valid UserCreateDto dto) {
        log.info("POST /api/admin/create-admin - Admin is creating a new administrator: {}", dto.username());
        ProfileResponseDto response = authService.addAdmin(dto);
        log.info("POST /api/admin/create-admin - Admin created successfully with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
