package com.project.velo.mapper;

import com.project.velo.dto.UserCreateDto;
import com.project.velo.dto.UserResponseDto;
import com.project.velo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "phone", source = "profile.phone")
    @Mapping(target = "bio", source = "profile.bio")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    @Mapping(target = "role", source = "role")
    UserResponseDto toDto(User user);


    User toEntity(UserCreateDto dto);
}
