package com.project.velo.controller.advertisement;

import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.response.CategoryResponseDto;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.service.advertisement.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryCreateDto dto) {
        log.info("POST /api/categories - Trying to save new category: {}", dto.name());
        CategoryResponseDto category = categoryService.create(dto);
        log.info("POST /api/categories - Created category: {} with id: {}", category.name(), category.id());
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        log.info("GET /api/categories - fetching all categories");
        List<CategoryResponseDto> categories = categoryService.getAll();
        log.info("GET /api/categories - Found {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateDto dto) {
        log.info("PATCH /api/categories - Trying to update category: {}", dto.name());
        CategoryResponseDto category = categoryService.update(id, dto);
        log.info("PATCH /api/categories - Category successfully updated: {}", category.name());
        return ResponseEntity.ok(category);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /api/categories - Trying to delete category: {}", id);
        categoryService.delete(id);
        log.info("DELETE /api/categories - Category successfully deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}
