package com.project.velo.controller;

import com.project.velo.dto.response.MessageResponseDto;
import com.project.velo.dto.update.MessageUpdateDto;
import com.project.velo.service.social.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

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
}
