package com.project.velo.controller.social;

import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.CommentDetailsResponseDto;
import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.update.CommentUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.service.social.CommentService;
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
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/advertisement/{adId}")
    public ResponseEntity<CommentDetailsResponseDto> postComment(
            @PathVariable Long adId,
            @RequestBody @Valid CommentCreateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("POST /api/comments/advertisement/{} - User: {} trying to post a comment to advertisement with id: {}", adId, user.getUsername(), adId);
        CommentDetailsResponseDto comment = commentService.postComment(adId, dto, user.getUsername());
        log.info("POST /api/comments/advertisement/{} - Comment: {} by user: {} was successfully posted", adId, comment, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/advertisement/{adId}")
    public ResponseEntity<PageResponse<CommentDetailsResponseDto>> getComments(
            @PathVariable Long adId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/comments/advertisement/{} - Fetching all comments by advertisement with id: {}, page: {}, size: {}",
                adId, adId, page, size);
        PageResponse<CommentDetailsResponseDto> comments = commentService.getCommentsByAdvertisement(adId, page, size);
        log.info("GET /api/comments/advertisement/{} - Found {} comments for advertisement: {}, page: {}, size: {}",
                adId, comments.size(), adId, page, size);
        return ResponseEntity.ok(comments);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId, @AuthenticationPrincipal UserDetails user) {
        log.info("DELETE /api/comments/{} - User: {} trying to delete a comment with id: {}", commentId, user.getUsername(), commentId);
        commentService.delete(commentId, user.getUsername());
        log.info("DELETE /api/comments/{} - Comment with id: {} was successfully deleted", commentId, commentId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDetailsResponseDto> updateComment(
            @RequestBody @Valid CommentUpdateDto dto,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("PATCH /api/comments/{} - User: {} trying to update a comment with id: {}", commentId, user.getUsername(), commentId);
        CommentDetailsResponseDto updated = commentService.update(commentId, dto, user.getUsername());
        log.info("PATCH /api/comments/{} - Comment: {} by user: {} was successfully updated", commentId, updated, user.getUsername());
        return ResponseEntity.ok(updated);
    }

}
