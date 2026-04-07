package com.project.velo.mapper;

import com.project.velo.dto.create.MessageCreateDto;
import com.project.velo.dto.response.MessageResponseDto;
import com.project.velo.dto.update.MessageUpdateDto;
import com.project.velo.entity.Message;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "senderUsername", source = "message.sender.username")
    @Mapping(target = "isMine", expression = "java(message.getSender().getUsername().equals(currentUser))")
    @Mapping(target = "isEdited", expression = "java(message.getUpdatedAt().isAfter(message.getSentAt()))")
    MessageResponseDto toDto(Message message, @Context String currentUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chat", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    Message toEntity(MessageCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chat", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    Message toEntity(MessageUpdateDto dto);
}
