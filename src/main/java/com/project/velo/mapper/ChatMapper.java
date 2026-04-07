package com.project.velo.mapper;

import com.project.velo.dto.response.ChatListResponseDto;
import com.project.velo.dto.response.ChatResponseDto;
import com.project.velo.entity.Chat;
import com.project.velo.entity.Message;
import com.project.velo.entity.User;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "advertisementId", source = "advertisement.id")
    @Mapping(target = "sellerUsername", source = "seller.username")
    @Mapping(target = "buyerUsername", source = "buyer.username")
    ChatResponseDto toResponseDto(Chat chat);

    @Mapping(target = "advertisementId", source = "chat.advertisement.id")
    @Mapping(target = "advertisementTitle", source = "chat.advertisement.title")
    @Mapping(target = "lastMessageContent", source = "chat.messages", qualifiedByName = "mapLastMessage")
    @Mapping(target = "interlocutorUsername", expression = "java(getInterlocutor(chat, currentUser).getUsername())")
    @Mapping(target = "interlocutorAvatarUrl", expression = "java(getInterlocutor(chat, currentUser).getProfile().getAvatarUrl())")
    ChatListResponseDto toListDto(Chat chat, @Context String currentUser);

    default User getInterlocutor(Chat chat, String currentUser) {
        return chat.getSeller().getUsername().equals(currentUser)
                ? chat.getBuyer()
                : chat.getSeller();
    }

    @Named("mapLastMessage")
    default String mapLastMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "Сообщений пока нет";
        }
        return messages.get(messages.size() - 1).getContent();
    }
}
