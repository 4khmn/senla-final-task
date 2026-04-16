package com.project.velo.controller.profile;

import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.dto.response.UserCommentResponseDto;
import com.project.velo.service.social.CommentService;
import com.project.velo.service.social.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileSocialController {

    private final CommentService commentService;
    private final ReviewService reviewService;

    @GetMapping("/my/comments")
    public ResponseEntity<PageResponse<UserCommentResponseDto>> getAllComments(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/profiles/my/comments - Fetching all comments for user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<UserCommentResponseDto> comments = commentService.getCommentsByUser(user.getUsername(), page, size);
        log.info("GET /api/profiles/my/comments - Found {} comments for user: {}, page: {}, size: {}",
                comments.size(), user.getUsername(), page, size);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/my/reviews")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getMyReceivedReviews(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/profiles/my/reviews - Fetching reviews by user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<ReviewResponseDto> reviews = reviewService.getReviewsByUser(user.getUsername(), rating, sortDirection, page, size);
        log.info("GET /api/profiles/my/reviews - Found {} reviews by user: {}, page: {}, size: {}", reviews.content().size(), user.getUsername(), page, size);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{username}/reviews")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getUserReceivedReviews(
            @PathVariable String username,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/profiles/{}/reviews - Fetching reviews by user: {}, page: {}, size: {}", username, username, page, size);
        PageResponse<ReviewResponseDto> reviews = reviewService.getReviewsByUser(username, rating, sortDirection, page, size);
        log.info("GET /api/profiles/{}/reviews - Found {} reviews by user: {}, page: {}, size: {}", username, reviews.content().size(), username, page, size);
        return ResponseEntity.ok(reviews);
    }
}
