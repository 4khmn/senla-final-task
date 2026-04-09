package com.project.velo.service.social;

import com.project.velo.dto.response.ChatListResponseDto;
import com.project.velo.dto.response.ChatResponseDto;

import java.util.List;

public interface ChatService {

    List<ChatListResponseDto> findAllByUser(String username);

    ChatResponseDto getOrCreate(Long adId, String username);


}
