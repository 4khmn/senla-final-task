package com.project.velo.mapper;

import com.project.velo.dto.AdvertisementCreateDto;
import com.project.velo.dto.AdvertisementResponseDto;
import com.project.velo.entity.AdImage;
import com.project.velo.entity.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


// uses = UserMapper.class позволяет MapStruct использовать логику UserMapper
// для заполнения поля UserResponseDto user
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AdvertisementMapper {

    @Mapping(target = "user", source = "seller") // Мапим User seller -> UserResponseDto user
    @Mapping(target = "categoryName", source = "category.name") // Достаем имя из объекта Category
    @Mapping(target = "imageUrls", source = "images") // Мапим список объектов картинок в список строк
    AdvertisementResponseDto toDto(Advertisement advertisement);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Advertisement toEntity(AdvertisementCreateDto dto);

    default String mapImageToUrl(AdImage image) {
        return image != null ? image.getImageUrl() : null;
    }
}