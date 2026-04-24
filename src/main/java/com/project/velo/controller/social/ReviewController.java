package com.project.velo.controller.social;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.review.ReviewResponseDto;
import com.project.velo.service.social.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "Управление отзывами")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;


    @Operation(
            summary = "Оставить отзыв",
            description = "Оставить отзыв к объявлению по id",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "409", description = "Отзыв на объявление уже оставлен")
    @ApiResponse(responseCode = "201", description = "Отзыв успешно оставлен")
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

    @Operation(
            summary = "Удалить отзыв",
            description = "Удалить отзыв к объявлению по id",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    @ApiResponse(responseCode = "204", description = "Отзыв успешно удален")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("DELETE /api/reviews/{} - Delete attempt by user: {}", id, user.getUsername());
        reviewService.deleteReview(id, user.getUsername());
        log.info("DELETE /api/reviews/{} - Review successfully deleted by user: {}", id, user.getUsername());
        return ResponseEntity.noContent().build();
    }

}
