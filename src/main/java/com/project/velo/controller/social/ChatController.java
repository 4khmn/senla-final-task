package com.project.velo.controller.social;

import com.project.velo.dto.response.ChatListResponseDto;
import com.project.velo.dto.response.ChatResponseDto;
import com.project.velo.dto.response.MessageResponseDto;
import com.project.velo.dto.response.PageResponse;
import com.project.velo.service.social.ChatService;
import com.project.velo.service.social.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ChatResponseDto> getOrCreateChat(
            @RequestParam Long adId,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("POST /api/chats - User: {} initiating chat for ad: {}", user.getUsername(), adId);
        ChatResponseDto chat = chatService.getOrCreate(adId, user.getUsername());
        log.info("POST /api/chats - User: {} returning chat: {}", user.getUsername(), chat.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(chat);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ChatListResponseDto>> getMyChats(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        log.info("GET /api/chats - Fetching chat list for user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<ChatListResponseDto> chats = chatService.findAllByUsername(user.getUsername(), page, size);
        log.info("GET /api/chats - Found {} chats for user: {}, page: {}, size: {}", chats.size(), user.getUsername(), page, size);
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<PageResponse<MessageResponseDto>> getChatMessages(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        log.info("GET /api/chats/{}/messages - User: {} fetching history for chat: {}, page: {}, size: {}",
                chatId, user.getUsername(), chatId, page, size);
        PageResponse<MessageResponseDto> messages = messageService.getMessagesByChat(chatId, user.getUsername(), page, size);
        log.info("GET /api/chats/{}/messages - Found {} messages in chat: {}, page: {}, size: {}",
                chatId, messages.size(), chatId, page, size);
        return ResponseEntity.ok(messages);
    }
}
