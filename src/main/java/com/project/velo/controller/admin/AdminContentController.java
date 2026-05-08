package com.project.velo.controller.admin;

import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.response.advertisement.AdvertisementResponseDto;
import com.project.velo.dto.response.advertisement.CategoryResponseDto;
import com.project.velo.dto.response.review.ReviewFullResponseDto;
import com.project.velo.dto.response.review.ReviewReceivedResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.CategoryService;
import com.project.velo.service.social.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin: Content", description = "Управление объявлениями, отзывами и категориями")
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
@Slf4j
public class AdminContentController {

    private final ReviewService reviewService;
    private final AdvertisementService advertisementService;
    private final CategoryService categoryService;

    @Operation(
            summary = "Список всех отзывов",
            description = "Позволяет админу просматривать все отзывы в системе для модерации"
    )
    @GetMapping("/reviews")
    public ResponseEntity<PageResponse<ReviewFullResponseDto>> getAllReviews(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/content/reviews - Admin fetching all reviews, page: {}, size: {}", page, size);
        PageResponse<ReviewFullResponseDto> reviews = reviewService.getAllReviews(page, size);
        log.info("GET /api/admin/content/reviews - Found: {} reviews, page: {}, size: {}", reviews.size(), page, size);
        return ResponseEntity.ok(reviews);
    }

    @Operation(
            summary = "Список всех объявлений",
            description = "Включает скрытые и заблокированные объявления"
    )
    @GetMapping("/advertisements")
    public ResponseEntity<PageResponse<AdvertisementResponseDto>> getAllAdvertisements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/content/advertisements - Admin fetching all advertisements, page: {}, size: {}", page, size);
        PageResponse<AdvertisementResponseDto> ads = advertisementService.getAllForAdmin(page, size);
        log.info("GET /api/admin/content/advertisements - Found: {} advertisements, page: {}, size: {}", ads.size(), page, size);
        return ResponseEntity.ok(ads);
    }

    @Operation(summary = "Создание категории")
    @ApiResponse(responseCode = "201", description = "Категория успешно создана")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryCreateDto dto) {
        log.info("POST /api/admin/content/categories - Trying to save new category: {}", dto.name());
        CategoryResponseDto category = categoryService.create(dto);
        log.info("POST /api/admin/content/categories - Created category: {} with id: {}", category.name(), category.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @Operation(summary = "Изменение категории")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "200")
    @PatchMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateDto dto) {
        log.info("PATCH /api/admin/content/categories - Trying to update category: {}", dto.name());
        CategoryResponseDto category = categoryService.update(id, dto);
        log.info("PATCH /api/admin/content/categories - Category successfully updated: {}", category.name());
        return ResponseEntity.ok(category);
    }

    @Operation(
            summary = "Удаление категории")
    @ApiResponse(responseCode = "204", description = "Категория удалена")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /api/admin/content/categories/{} - Trying to delete category: {}", id, id);
        categoryService.delete(id);
        log.info("DELETE /api/admin/content/categories/{} - Category successfully deleted: {}", id, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Блокировка объявления")
    @ApiResponse(responseCode = "204", description = "Объявление заблокировано")
    @DeleteMapping("/advertisements/{id}")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable Long id) {
        log.info("DELETE /api/admin/content/advertisements/{} - Trying to delete advertisement: {}", id, id);
        advertisementService.deleteByAdmin(id);
        log.info("DELETE /api/admin/content/advertisements/{} - Advertisement: {} successfully banned by admin", id, id);
        return ResponseEntity.noContent().build();
    }

}
