package com.project.velo.controller.social;

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


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/advertisement/{adId}")
    public ResponseEntity<ReviewResponseDto> leaveReview(
            @PathVariable Long adId,
            @RequestBody @Valid ReviewCreateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("POST /api/reviews/advertisement/{} - User: {} is leaving a review for advertisement: {}", adId, user.getUsername(), adId);
        ReviewResponseDto review = reviewService.leaveReview(adId, dto, user.getUsername());
        log.info("POST /api/reviews/advertisement/{} - Review: {} was successfully posted by user: {}", adId, review, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }
}
