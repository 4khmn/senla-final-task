package com.project.velo.mapper;

import com.project.velo.dto.update.AdvertisementUpdateDto;
import com.project.velo.dto.update.ProfileUpdateDto;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Profile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProfileUpdateDto dto, @MappingTarget Profile profile);


}
