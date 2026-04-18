package com.project.velo.service.advertisement;

import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.response.CategoryResponseDto;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.entity.Category;
import com.project.velo.exception.NotUniqueRecordException;
import com.project.velo.mapper.CategoryMapper;
import com.project.velo.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_ShouldReturnDto_WhenCategoryDoesNotExist() {
        CategoryCreateDto dto = new CategoryCreateDto("name");
        Category category = new Category();
        category.setName("name");
        CategoryResponseDto response = new CategoryResponseDto(1L, "name");
        given(categoryRepository.existsByName(dto.name())).willReturn(false);
        given(mapper.toEntity(dto)).willReturn(category);
        given(categoryRepository.save(category)).willAnswer(i -> i.getArgument(0));
        given(mapper.toDto(category)).willReturn(response);

        CategoryResponseDto result = categoryService.create(dto);

        assertNotNull(result);
        assertEquals(response, result);

        verify(mapper).toDto(category);
        verify(categoryRepository).save(category);
    }

    @Test
    void create_ShouldThrowNotUniqueRecordException_WhenCategoryExists() {
        CategoryCreateDto dto = new CategoryCreateDto("name");

        given(categoryRepository.existsByName(dto.name())).willReturn(true);

        NotUniqueRecordException result = assertThrows(NotUniqueRecordException.class,
                () -> categoryService.create(dto));

        assertEquals("Категория с именем " + dto.name() + " должна быть уникальной", result.getMessage());

        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    @Test
    void getAll_ShouldReturnListOfDtos_Success() {
        Category category = new Category();
        category.setName("name");

        CategoryResponseDto dto = new CategoryResponseDto(1L, "name");

        given(categoryRepository.findAll()).willReturn(List.of(category));
        given(mapper.toDto(category)).willReturn(dto);

        List<CategoryResponseDto> result = categoryService.getAll();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));

        verify(categoryRepository).findAll();
        verify(mapper).toDto(category);
    }

    @Test
    void update_ShouldReturnDto_Success() {
        CategoryUpdateDto dto = new CategoryUpdateDto("newName");
        Long id = 1L;

        Category category = new Category();
        category.setId(id);
        category.setName("name");

        CategoryResponseDto response = new CategoryResponseDto(1L, "newName");
        given(categoryRepository.existsByName(dto.name())).willReturn(false);
        given(categoryRepository.findById(id)).willReturn(Optional.of(category));
        given(mapper.toDto(category)).willReturn(response);
        given(categoryRepository.save(category)).willAnswer(i -> i.getArgument(0));

        CategoryResponseDto result = categoryService.update(id, dto);

        assertNotNull(result);
        assertEquals(response, result);
        verify(categoryRepository).findById(id);
        verify(mapper).toDto(category);
        verify(categoryRepository).save(category);
    }

    @Test
    void update_ShouldThrowNotUniqueRecordException_WhenCategoryExistsWithSameName() {
        CategoryUpdateDto dto = new CategoryUpdateDto("newName");
        Long id = 1L;

        given(categoryRepository.existsByName(dto.name())).willReturn(true);

        NotUniqueRecordException result = assertThrows(NotUniqueRecordException.class,
                () -> categoryService.update(id, dto));

        assertEquals("Категория с именем " + dto.name() + " должна быть уникальной", result.getMessage());

        verifyNoInteractions(mapper);
        verify(categoryRepository, never()).save(any());
        verify(categoryRepository, never()).findById(any());
    }

    @Test
    void update_ShouldThrowENFException_WhenCategoryDoesNotExist() {
        CategoryUpdateDto dto = new CategoryUpdateDto("newName");
        Long id = 1L;

        given(categoryRepository.existsByName(dto.name())).willReturn(false);
        given(categoryRepository.findById(id)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(id, dto));

        assertEquals("Категория с id " + id + " не найдена", result.getMessage());

        verifyNoInteractions(mapper);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void delete_Success() {
        Category category = new Category();
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void delete_WhenCategoryDoesNotExist() {
        Long id = 1L;
        given(categoryRepository.findById(id)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> categoryService.delete(id));

        assertEquals("Категория с id " + id + " не найдена", result.getMessage());
        verify(categoryRepository, never()).delete(any());
    }
}
