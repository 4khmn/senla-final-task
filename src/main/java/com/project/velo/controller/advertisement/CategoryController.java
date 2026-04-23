package com.project.velo.controller.advertisement;

import com.project.velo.dto.response.CategoryResponseDto;
import com.project.velo.service.advertisement.CategoryService;
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



    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        log.info("GET /api/categories - fetching all categories");
        List<CategoryResponseDto> categories = categoryService.getAll();
        log.info("GET /api/categories - Found {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

}
