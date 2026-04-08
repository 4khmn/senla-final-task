package com.project.velo.mapper;

import com.project.velo.dto.response.AuthorResponseDto;
import com.project.velo.dto.create.UserCreateDto;
import com.project.velo.dto.response.ProfileResponseDto;
import com.project.velo.entity.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    AuthorResponseDto toAuthorDto(User user);


    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "bio", source = "profile.bio")
    @Mapping(target = "avatarUrl", source = "profile.avatarUrl")
    ProfileResponseDto toProfileDto(User user);


    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "profile.firstName", source = "firstName")
    @Mapping(target = "profile.lastName", source = "lastName")
    User toEntity(UserCreateDto dto);

    @AfterMapping
    default void linkProfile(@MappingTarget User user) {
        if (user.getProfile() != null) {
            user.getProfile().setUser(user);
        }
    }
}
