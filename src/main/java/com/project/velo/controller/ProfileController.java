package com.project.velo.controller;

import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.response.SalesHistoryResponseDto;
import com.project.velo.dto.response.UserCommentResponseDto;
import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.service.AdvertisementService;
import com.project.velo.service.CommentService;
import com.project.velo.service.ProfileService;
import com.project.velo.service.SalesHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final CommentService commentService;
    private final SalesHistoryService salesHistoryService;
    private final AdvertisementService advertisementService;

    @PatchMapping
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @RequestBody @Valid ProfileUpdateDto dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("PATCH /api/profile - Fetching profile for username: {}", user.getUsername());
        ProfileResponseDto updated = profileService.update(dto, user.getUsername());
        log.info("PATCH /api/profile - Profile for username: {} was successfully updated", user.getUsername());
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<ProfileResponseDto> getMyProfile(@AuthenticationPrincipal User user) {
        log.info("GET /api/profile - Fetching profile for username = {}", user.getUsername());
        ProfileResponseDto profile = profileService.getByUsername(user.getUsername());
        log.info("GET /api/profile - Profile for username: {} was successfully retrieved", user.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<UserCommentResponseDto>> getAllComments(@AuthenticationPrincipal User user) {
        log.info("GET /api/profile/comments - Fetching all comments for user: {}", user.getUsername());
        List<UserCommentResponseDto> comments = commentService.getCommentsByUser(user.getUsername());
        log.info("GET /api/profile/comments - Found {} comments for user: {}", comments.size(), user.getUsername());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<SalesHistoryResponseDto>> getMySales(
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("GET /api/profile/sales - Fetching sales by user: {}", user.getUsername());
        List<SalesHistoryResponseDto> mySales = salesHistoryService.getSales(user.getUsername());
        log.info("GET /api/profile/sales - Found {} sales by user: {}", mySales.size(), user.getUsername());
        return ResponseEntity.ok(mySales);
    }

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable String username) {
        log.info("GET /api/profile - Fetching profile for user: {}", username);
        ProfileResponseDto profile = profileService.getByUsername(username);
        log.info("GET /api/profile - Profile for user: {} was successfully retrieved", username);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{username}/sales")
    public ResponseEntity<List<SalesHistoryResponseDto>> getUserSales(
            @PathVariable String username
    ) {
        log.info("GET /api/profile/{}/sales - Fetching sales by user: {}", username, username);
        List<SalesHistoryResponseDto> mySales = salesHistoryService.getSales(username);
        log.info("GET /api/profile/{}/sales - Found {} sales by user: {}", username, mySales.size(), username);
        return ResponseEntity.ok(mySales);
    }

    @GetMapping("/advertisements")
    public ResponseEntity<List<AdvertisementResponseDto>> getMyActiveAdvertisements(
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("GET /api/profile/advertisements - Fetching advertisements by user: {}", user.getUsername());
        List<AdvertisementResponseDto> advertisements = advertisementService.findAdvertisementsByUsername(user.getUsername());
        log.info("GET /api/profile/advertisements - Found {} advertisements by user: {}", advertisements.size(), user.getUsername());
        return ResponseEntity.ok(advertisements);
    }

    @GetMapping("/{username}/advertisements")
    public ResponseEntity<List<AdvertisementResponseDto>> getUserActiveAdvertisements(
            @PathVariable String username
    ) {
        log.info("GET /api/profile/{}/sales - Fetching sales by user: {}", username, username);
        List<AdvertisementResponseDto> mySales = advertisementService.findAdvertisementsByUsername(username);
        log.info("GET /api/profile/{}/sales - Found {} sales by user: {}", username, mySales.size(), username);
        return ResponseEntity.ok(mySales);
    }

}
