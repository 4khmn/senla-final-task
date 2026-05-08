package com.project.velo.controller.profile;

import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.review.ReviewResponseDto;
import com.project.velo.dto.response.profile.UserCommentResponseDto;
import com.project.velo.service.social.CommentService;
import com.project.velo.service.social.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Profile: Social", description = "Управление коментариями и отзывами пользователя")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileSocialController {

    private final CommentService commentService;
    private final ReviewService reviewService;

    @Operation(
            summary = "Получение комментариев текущего пользователя",
            security = @SecurityRequirement(name = "JWT")
    )
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

    @Operation(
            summary = "Список отзывов, оставленных другими пользователями о текущем пользователе",
            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/my/reviews/received")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getMyReceivedReviews(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/profiles/my/reviews/received - Fetching reviews by user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<ReviewResponseDto> reviews = reviewService.getReceivedByUser(user.getUsername(), rating, sortDirection, page, size);
        log.info("GET /api/profiles/my/reviews/received - Found {} reviews by user: {}, page: {}, size: {}", reviews.content().size(), user.getUsername(), page, size);
        return ResponseEntity.ok(reviews);
    }

    @Operation(
            summary = "Список отзывов, оставленных текущем пользователем",
            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/my/reviews/sent")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getMyLeavedReviews(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/profiles/my/reviews/sent - Fetching reviews by user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<ReviewResponseDto> reviews = reviewService.getSentByUser(user.getUsername(), page, size);
        log.info("GET /api/profiles/my/reviews/sent - Found {} reviews by user: {}, page: {}, size: {}", reviews.content().size(), user.getUsername(), page, size);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Список отзывов, оставленных другими пользователями о пользователе по username")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{username}/reviews")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getUserReceivedReviews(
            @PathVariable String username,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/profiles/{}/reviews - Fetching reviews by user: {}, page: {}, size: {}", username, username, page, size);
        PageResponse<ReviewResponseDto> reviews = reviewService.getReceivedByUser(username, rating, sortDirection, page, size);
        log.info("GET /api/profiles/{}/reviews - Found {} reviews by user: {}, page: {}, size: {}", username, reviews.content().size(), username, page, size);
        return ResponseEntity.ok(reviews);
    }
}
