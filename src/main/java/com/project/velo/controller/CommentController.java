package com.project.velo.controller;

import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.CommentDetailsResponseDto;
import com.project.velo.dto.update.CommentUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.service.social.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/advertisements/{adId}/comments")
    public ResponseEntity<CommentDetailsResponseDto> createComment(
            @PathVariable Long adId,
            @RequestBody @Valid CommentCreateDto dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("POST /api/advertisements/{}/comments - User: {} trying to post a comment to advertisement with id: {}", adId, user.getUsername(), adId);
        CommentDetailsResponseDto comment = commentService.postComment(adId, dto, user.getUsername());
        log.info("POST /api/advertisements/{}/comments - Comment: {} by user: {} was successfully posted", adId, comment, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/api/advertisements/{adId}/comments")
    public ResponseEntity<List<CommentDetailsResponseDto>> getComments(
            @PathVariable Long adId) {
        log.info("GET /api/advertisements/{}/comments - Fetching all comments by advertisement with id: {}", adId, adId);
        List<CommentDetailsResponseDto> comments = commentService.getCommentsByAdvertisement(adId);
        log.info("GET /api/advertisements/{}/comments - Found {} comments for advertisement: {}", adId, comments.size(), adId);
        return ResponseEntity.ok(comments);
    }


    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId, @AuthenticationPrincipal User user) {
        log.info("DELETE /api/comments/{} - User: {} trying to delete a comment with id: {}", commentId, user.getUsername(), commentId);
        commentService.delete(commentId, user.getUsername());
        log.info("DELETE /api/comments/{} - Comment with id: {} was successfully deleted", commentId, commentId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDetailsResponseDto> updateComment(
            @RequestBody @Valid CommentUpdateDto dto,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user
    ) {
        log.info("PUT /api/comments/{} - User: {} trying to update a comment with id: {}", commentId, user.getUsername(), commentId);
        CommentDetailsResponseDto updated = commentService.update(commentId, dto, user.getUsername());
        log.info("PUT /api/comments/{} - Comment: {} by user: {} was successfully updated", commentId, updated, user.getUsername());
        return ResponseEntity.ok(updated);
    }
}
