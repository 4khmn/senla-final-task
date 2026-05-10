package com.project.velo.controller.social;

import com.project.velo.dto.response.chat.ChatListResponseDto;
import com.project.velo.dto.response.chat.ChatResponseDto;
import com.project.velo.dto.response.chat.MessageResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.service.social.ChatService;
import com.project.velo.service.social.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat", description = "Управление чатами")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;

    @Operation(
            summary = "Получение чата",
            description = "Проверяет существование чата, создает его при отсувствии",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "201", description = "Чат успешно создан")
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

    @Operation(summary = "Получить список моих чатов", security = @SecurityRequirement(name = "JWT"))
    @GetMapping
    public ResponseEntity<PageResponse<ChatListResponseDto>> getMyChats(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        log.info("GET /api/chats - Fetching chat list for user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<ChatListResponseDto> chats = chatService.findAllByUsername(user.getUsername(), page, size);
        log.info("GET /api/chats - Found {} chats for user: {}, page: {}, size: {}", chats.content().size(), user.getUsername(), page, size);
        return ResponseEntity.ok(chats);
    }

    @Operation(summary = "Получить сообщения чата по его id", security = @SecurityRequirement(name = "JWT"))
    @ApiResponse(responseCode = "404", description = "Чат не найден")
    @ApiResponse(responseCode = "200")
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
                chatId, messages.content().size(), chatId, page, size);
        return ResponseEntity.ok(messages);
    }
}
