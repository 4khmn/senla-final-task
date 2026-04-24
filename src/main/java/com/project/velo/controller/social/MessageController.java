package com.project.velo.controller.social;

import com.project.velo.dto.create.MessageCreateDto;
import com.project.velo.dto.response.chat.MessageResponseDto;
import com.project.velo.dto.update.MessageUpdateDto;
import com.project.velo.service.social.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Message", description = "Управление личными сообщениями")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Operation(
            summary = "Редактировать сообщение",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "404", description = "Соообщение не найдено")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "200")
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponseDto> updateMessage(
            @PathVariable Long messageId,
            @RequestBody @Valid MessageUpdateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("PATCH /api/messages/{} - User: {} editing message: {}", messageId, user.getUsername(), messageId);
        MessageResponseDto updated = messageService.editMessage(messageId, dto, user.getUsername());
        log.info("PATCH /api/messages/{} - Message: {} was successfully updated", messageId, updated);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Удалить сообщение",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "404", description = "Сообщение не найдено")
    @ApiResponse(responseCode = "204", description = "Сообщение успешно удалено")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("DELETE /api/messages/{} - User: {} deleting message: {}", messageId, user.getUsername(), messageId);
        messageService.deleteMessage(messageId, user.getUsername());
        log.info("DELETE /api/messages/{} - Message: {} was successfully deleted", messageId, messageId);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Отправить сообщение",
            description = "Отправить сообщение в чат по его идентификатору",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    @ApiResponse(responseCode = "404", description = "Чат не найден")
    @ApiResponse(responseCode = "201", description = "Коментарий успешно отправлен")
    @PostMapping("/chat/{chatId}")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @PathVariable Long chatId,
            @RequestBody @Valid MessageCreateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("POST /api/messages/chat/{} - User: {} sending message", chatId, user.getUsername());
        MessageResponseDto message = messageService.sendMessage(chatId, dto, user.getUsername());
        log.info("POST /api/messages/chat/{} - Message: {} sent to chat: {}", chatId, message.id(), chatId);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}
