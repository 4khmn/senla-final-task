package com.project.velo.controller.admin;

import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.response.*;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.CategoryService;
import com.project.velo.service.social.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/content")
@RequiredArgsConstructor
@Slf4j
public class AdminContentController {

    private final ReviewService reviewService;
    private final AdvertisementService advertisementService;
    private final CategoryService categoryService;


    @GetMapping("/reviews")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/admin/content/reviews - Admin fetching all reviews, page: {}, size: {}", page, size);
        PageResponse<ReviewResponseDto> reviews = reviewService.getAllReviews(page, size);
        log.info("GET /api/admin/content/reviews - Found: {} reviews, page: {}, size: {}", reviews.size(), page, size);
        return ResponseEntity.ok(reviews);
    }

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

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryCreateDto dto) {
        log.info("POST /api/admin/content/categories - Trying to save new category: {}", dto.name());
        CategoryResponseDto category = categoryService.create(dto);
        log.info("POST /api/admin/content/categories - Created category: {} with id: {}", category.name(), category.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @PatchMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateDto dto) {
        log.info("PATCH /api/admin/content/categories - Trying to update category: {}", dto.name());
        CategoryResponseDto category = categoryService.update(id, dto);
        log.info("PATCH /api/admin/content/categories - Category successfully updated: {}", category.name());
        return ResponseEntity.ok(category);
    }


    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /api/admin/content/categories - Trying to delete category: {}", id);
        categoryService.delete(id);
        log.info("DELETE /api/admin/content/categories - Category successfully deleted: {}", id);
        return ResponseEntity.noContent().build();
    }

}
