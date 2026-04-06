package com.project.velo.controller;

import com.project.velo.dto.response.UserCommentResponseDto;
import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.service.CommentService;
import com.project.velo.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final CommentService commentService;

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
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal User user) {
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

}
