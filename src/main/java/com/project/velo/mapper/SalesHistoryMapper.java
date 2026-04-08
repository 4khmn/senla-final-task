package com.project.velo.mapper;

import com.project.velo.dto.response.SalesHistoryResponseDto;
import com.project.velo.entity.SalesHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesHistoryMapper {

    @Mapping(target = "advertisementTitle", source = "salesHistory.advertisement.title")
    @Mapping(target = "price", source = "finalPrice")
    @Mapping(target = "buyerUsername", source = "salesHistory.buyer.username")
    @Mapping(target = "advertisementId", source = "salesHistory.advertisement.id")
    SalesHistoryResponseDto toDto(SalesHistory salesHistory);
}
