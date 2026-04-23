package com.project.velo.mapper;

import com.project.velo.dto.response.salesHistory.SalesHistoryPrivateResponseDto;
import com.project.velo.dto.response.salesHistory.SalesHistoryPublicResponseDto;
import com.project.velo.entity.SalesHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesHistoryMapper {

    @Mapping(target = "advertisementTitle", source = "salesHistory.advertisement.title")
    @Mapping(target = "price", source = "finalPrice")
    @Mapping(target = "buyerUsername", source = "salesHistory.buyer.username")
    @Mapping(target = "advertisementId", source = "salesHistory.advertisement.id")
    SalesHistoryPrivateResponseDto toPrivateDto(SalesHistory salesHistory);

    @Mapping(target = "advertisementTitle", source = "salesHistory.advertisement.title")
    SalesHistoryPublicResponseDto toPublicDto(SalesHistory salesHistory);
}
