package com.project.velo.mapper;

import com.project.velo.dto.create.CategoryCreateDto;
import com.project.velo.dto.response.advertisement.CategoryResponseDto;
import com.project.velo.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDto toDto(Category category);

    Category toEntity(CategoryCreateDto dto);
}
