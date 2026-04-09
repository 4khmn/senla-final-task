package com.project.velo.controller;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.service.social.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/advertisements/{adId}/reviews")
    public ResponseEntity<ReviewResponseDto> leaveReview(
            @PathVariable Long adId,
            @RequestBody @Valid ReviewCreateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("POST /api/advertisements/{}/reviews - User: {} is leaving a review for advertisement: {}", adId, user.getUsername(), adId);
        ReviewResponseDto review = reviewService.leaveReview(adId, dto, user.getUsername());
        log.info("POST /api/advertisements/{}/reviews - Review: {} was successfully posted by user: {}", adId, review, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }


    @GetMapping("/api/profile/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getMyReceivedReviews(@AuthenticationPrincipal UserDetails user) {
        log.info("GET /api/profile/reviews - Fetching reviews by user: {}", user.getUsername());
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUser(user.getUsername());
        log.info("GET /api/profile/reviews - Found {} reviews by user: {}", reviews.size(), user.getUsername());
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/api/profile/{username}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getUserReceivedReviews(@PathVariable String username) {
        log.info("GET /api/profile/reviews - Fetching reviews by user: {}", username);
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUser(username);
        log.info("GET /api/profile/reviews - Found {} reviews by user: {}", reviews.size(), username);
        return ResponseEntity.ok(reviews);
    }
}
