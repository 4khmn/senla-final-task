package com.project.velo.mapper;

import com.project.velo.dto.response.SalesPrivateHistoryResponseDto;
import com.project.velo.dto.response.SalesPublicHistoryResponseDto;
import com.project.velo.entity.SalesHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesHistoryMapper {

    @Mapping(target = "advertisementTitle", source = "salesHistory.advertisement.title")
    @Mapping(target = "price", source = "finalPrice")
    @Mapping(target = "buyerUsername", source = "salesHistory.buyer.username")
    @Mapping(target = "advertisementId", source = "salesHistory.advertisement.id")
    SalesPrivateHistoryResponseDto toPrivateDto(SalesHistory salesHistory);

    @Mapping(target = "advertisementTitle", source = "salesHistory.advertisement.title")
    SalesPublicHistoryResponseDto toPublicDto(SalesHistory salesHistory);
}
