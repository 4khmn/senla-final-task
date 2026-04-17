package com.project.velo.service.advertisement;

import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.response.CategoryResponseDto;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.entity.Category;
import com.project.velo.exception.NotUniqueRecordException;
import com.project.velo.mapper.CategoryMapper;
import com.project.velo.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Transactional
    public CategoryResponseDto create(CategoryCreateDto dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new NotUniqueRecordException("Категория с именем " + dto.name() + " должна быть уникальной");
        }

        Category category = mapper.toEntity(dto);
        categoryRepository.save(category);

        return mapper.toDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        return categoryRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public CategoryResponseDto update(Long id, CategoryUpdateDto dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new NotUniqueRecordException("Категория с именем " + dto.name() + " должна быть уникальной");
        }
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Категория с id " + id + " не найдена"));

        category.setName(dto.name());
        return mapper.toDto(categoryRepository.save(category));
    }


    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Категория с id " + id + " не найдена")
        );
        categoryRepository.delete(category);
    }

}
