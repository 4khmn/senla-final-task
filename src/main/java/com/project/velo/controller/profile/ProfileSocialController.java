package com.project.velo.controller.profile;

import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.dto.response.UserCommentResponseDto;
import com.project.velo.entity.User;
import com.project.velo.service.social.CommentService;
import com.project.velo.service.social.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileSocialController {

    private final CommentService commentService;
    private final ReviewService reviewService;

    @GetMapping("/my/comments")
    public ResponseEntity<List<UserCommentResponseDto>> getAllComments(@AuthenticationPrincipal User user) {
        log.info("GET /api/profiles/my/comments - Fetching all comments for user: {}", user.getUsername());
        List<UserCommentResponseDto> comments = commentService.getCommentsByUser(user.getUsername());
        log.info("GET /api/profiles/my/comments - Found {} comments for user: {}", comments.size(), user.getUsername());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/my/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getMyReceivedReviews(@AuthenticationPrincipal UserDetails user) {
        log.info("GET /api/profiles/my/reviews - Fetching reviews by user: {}", user.getUsername());
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUser(user.getUsername());
        log.info("GET /api/profiles/my/reviews - Found {} reviews by user: {}", reviews.size(), user.getUsername());
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{username}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getUserReceivedReviews(@PathVariable String username) {
        log.info("GET /api/profiles/{}/reviews - Fetching reviews by user: {}", username, username);
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUser(username);
        log.info("GET /api/profiles/{}/reviews - Found {} reviews by user: {}", username, reviews.size(), username);
        return ResponseEntity.ok(reviews);
    }
}
