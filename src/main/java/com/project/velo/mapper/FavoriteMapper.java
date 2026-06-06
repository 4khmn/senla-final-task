package com.project.velo.mapper;

import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.entity.AdImage;
import com.project.velo.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(target = "id", source = "favorite.advertisement.id")
    @Mapping(target = "title", source = "favorite.advertisement.title")
    @Mapping(target = "price", source = "favorite.advertisement.price")
    @Mapping(target = "categoryName", source = "favorite.advertisement.category.name")
    @Mapping(target = "primaryImageUrl", source = "favorite.advertisement.images")
    @Mapping(target = "isTop", source = "favorite.advertisement.top")
    @Mapping(target = "isActive", expression = "java(favorite.getAdvertisement().isActive())")
    @Mapping(target = "createdAt", source = "favorite.advertisement.createdAt")
    @Mapping(target = "sellerUsername", source = "favorite.advertisement.seller.username")
    @Mapping(target = "sellerScore", source = "favorite.advertisement.seller.rating")
    AdvertisementShortResponseDto toDto(Favorite favorite);



    default String mapPrimaryImage(List<AdImage> images) {
        if (images == null) return null;
        return images.stream()
                .filter(AdImage::isPrimary)
                .map(AdImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }

}


