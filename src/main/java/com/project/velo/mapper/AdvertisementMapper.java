package com.project.velo.mapper;

import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.response.AdvertisementShortResponseDto;
import com.project.velo.dto.update.AdvertisementUpdateDto;
import com.project.velo.entity.AdImage;
import com.project.velo.entity.Advertisement;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AdvertisementMapper {

    @Mapping(target = "seller", source = "seller")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "primaryImageUrl", source = "images")
    @Mapping(target = "otherImageUrls", source = "images")
    @Mapping(target = "status", expression = "java(advertisement.getStatus().name())")
    @Mapping(target = "isTop", source = "top")
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


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(AdvertisementUpdateDto dto, @MappingTarget Advertisement advertisement);


    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "primaryImageUrl", source = "images")
    @Mapping(target = "isTop", source = "top")
    @Mapping(target = "sellerUsername", source = "seller.username")
    @Mapping(target = "sellerScore", source = "seller.rating")
    AdvertisementShortResponseDto toShortDto(Advertisement advertisement);


    default String mapPrimaryImage(List<AdImage> images) {
        if (images == null) return null;
        return images.stream()
                .filter(AdImage::isPrimary)
                .map(AdImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }

    default List<String> mapOtherImages(List<AdImage> images) {
        if (images == null) return List.of();
        return images.stream()
                .filter(img -> !img.isPrimary())
                .map(AdImage::getImageUrl)
                .toList();
    }
}