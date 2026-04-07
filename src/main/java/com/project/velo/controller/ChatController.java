package com.project.velo.controller;

import com.project.velo.dto.create.MessageCreateDto;
import com.project.velo.dto.response.ChatListResponseDto;
import com.project.velo.dto.response.ChatResponseDto;
import com.project.velo.dto.response.MessageResponseDto;
import com.project.velo.dto.update.MessageUpdateDto;
import com.project.velo.entity.User;
import com.project.velo.service.ChatService;
import com.project.velo.service.MessageService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<ChatListResponseDto>> getMyChats(@AuthenticationPrincipal UserDetails user) {
        log.info("GET /api/chats - Fetching chat list for user: {}", user.getUsername());
        List<ChatListResponseDto> chats = chatService.findAllByUser(user.getUsername());
        log.info("GET /api/chats - Found {} chats for user: {}", chats.size(), user.getUsername());
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageResponseDto>> getChatMessages(
            @PathVariable Long chatId,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("GET /api/chats/{}/messages - User: {} fetching history for chat: {}", chatId, user.getUsername(), chatId);
        List<MessageResponseDto> messages = messageService.getMessagesByChat(chatId, user.getUsername());
        log.info("GET /api/chats/{}/messages - Found {} messages in chat: {}", chatId, messages.size(), chatId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @PathVariable Long chatId,
            @RequestBody @Valid MessageCreateDto dto,
            @AuthenticationPrincipal User user
    ) {
        log.info("POST /api/chats/{}/messages - User: {} sending message", chatId, user.getUsername());
        MessageResponseDto message = messageService.sendMessage(chatId, dto, user.getUsername());
        log.info("POST /api/chats/{}/messages - Message: {} sent to chat: {}", chatId, message.id(), chatId);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}
