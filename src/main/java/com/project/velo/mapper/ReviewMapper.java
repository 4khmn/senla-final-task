package com.project.velo.mapper;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "advertisementTitle", source = "review.advertisement.title")
    @Mapping(target = "advertisementId", source = "review.advertisement.id")
    @Mapping(target = "authorUsername", source = "review.author.username")
    ReviewResponseDto toDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "advertisement", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewCreateDto dto);
}
