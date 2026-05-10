package com.project.velo.service.social;

import com.project.velo.dto.create.MessageCreateDto;
import com.project.velo.dto.response.chat.MessageResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.update.MessageUpdateDto;
import com.project.velo.entity.Chat;
import com.project.velo.entity.Message;
import com.project.velo.entity.User;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.mapper.MessageMapper;
import com.project.velo.repository.ChatRepository;
import com.project.velo.repository.MessageRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper mapper;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponseDto sendMessage(Long chatId, MessageCreateDto dto, String username) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Чата с id " + chatId + " не найдено")
        );

        if (!chatRepository.isUserParticipant(chatId, username)) {
            throw new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не являетесь участником этого чата");
        }

        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с username " + username + " не найдено"));

        Message message = mapper.toEntity(dto);
        message.setSender(sender);
        message.setChat(chat);
        chat.setUpdatedAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);
        return mapper.toDto(saved, username);
    }

    @Transactional(readOnly = true)
    public PageResponse<MessageResponseDto> getMessagesByChat(Long chatId, String username, int page, int size) {

        if (!chatRepository.isUserParticipant(chatId, username)) {
            throw new EntityNotFoundException("Чата с id " + chatId + " не найдено");
        }

        List<Message> messages = messageRepository.findByChatWithPagination(chatId, page, size);

        long totalElements = messageRepository.countByChat(chatId);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<MessageResponseDto> dtos = messages.stream()
                .map(m -> mapper.toDto(m, username))
                .toList();

        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }

    @Transactional
    public MessageResponseDto editMessage(Long messageId, MessageUpdateDto dto, String username) {
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new EntityNotFoundException("Сообщения с id" + messageId + " не найдено")
        );

        if (!message.getSender().getUsername().equals(username)) {
            throw new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете изменять чужие сообщения");
        }
        message.setContent(dto.content());
        message.setEdited(true);
        return mapper.toDto(message, username);
    }

    @Transactional
    public void deleteMessage(Long messageId, String username) {
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new EntityNotFoundException("Сообщения с id" + messageId + " не найдено")
        );

        if (!message.getSender().getUsername().equals(username)) {
            throw new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете удалять чужие сообщения");
        }
        messageRepository.delete(message);
    }
}
