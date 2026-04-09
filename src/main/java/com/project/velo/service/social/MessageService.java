package com.project.velo.service.social;

import com.project.velo.dto.create.MessageCreateDto;
import com.project.velo.dto.response.MessageResponseDto;
import com.project.velo.dto.update.MessageUpdateDto;

import java.util.List;

public interface MessageService {

    MessageResponseDto sendMessage(Long chatId, MessageCreateDto dto, String username);

    List<MessageResponseDto> getMessagesByChat(Long chatId, String username);


    MessageResponseDto editMessage(Long messageId, MessageUpdateDto dto, String username);

    void deleteMessage(Long messageId, String username);
}
