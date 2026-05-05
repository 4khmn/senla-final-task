package com.project.velo.mapper;

import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.comment.CommentDetailsResponseDto;
import com.project.velo.dto.response.profile.UserCommentResponseDto;
import com.project.velo.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "advertisement", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentCreateDto dto);

    @Mapping(target = "advertisementTitle", source = "comment.advertisement.title")
    @Mapping(target = "advertisementId", source = "comment.advertisement.id")
    UserCommentResponseDto toShortDto(Comment comment);




    @Mapping(target = "isPinned", source = "pinned")
    @Mapping(target = "author", source = "author")
    CommentDetailsResponseDto toDetailsDto(Comment comment);
}
