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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageMapper mapper;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;


    @Test
    void sendMessage_ShouldReturnDto_Success() {
        Long chatId = 1L;
        String username = "senderUser";
        MessageCreateDto createDto = new MessageCreateDto("Hello!");

        User participant = new User();
        participant.setUsername(username);

        User participant2 = new User();
        participant2.setUsername("receiverUser");

        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setSeller(participant);
        chat.setBuyer(participant2);
        chat.setUpdatedAt(LocalDateTime.now().minusDays(1));

        Message message = new Message();
        message.setContent("Hello!");

        MessageResponseDto responseDto = new MessageResponseDto(1L, "Hello!", LocalDateTime.now(), username, true, false);

        given(chatRepository.findById(chatId)).willReturn(Optional.of(chat));
        given(chatRepository.isUserParticipant(chatId, username)).willReturn(true);
        given(userRepository.findByUsername(username)).willReturn(Optional.of(participant));
        given(mapper.toEntity(createDto)).willReturn(message);
        given(messageRepository.save(any(Message.class))).willAnswer(inv -> inv.getArgument(0));
        given(mapper.toDto(any(Message.class), eq(username))).willReturn(responseDto);

        MessageResponseDto result = messageService.sendMessage(chatId, createDto, username);

        assertNotNull(result);
        assertEquals(responseDto.content(), result.content());

        verify(messageRepository).save(message);
        verify(chatRepository).findById(chatId);
        assertTrue(chat.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void sendMessage_ShouldThrowNotEnoughRightsException_WhenUserNotParticipant() {
        Long chatId = 1L;
        String strangerName = "hacker";

        User participant = new User();
        participant.setUsername("seller");
        User participant2 = new User();
        participant2.setUsername("buyer");

        Chat chat = new Chat();
        chat.setSeller(participant);
        chat.setBuyer(participant2);

        given(chatRepository.findById(chatId)).willReturn(Optional.of(chat));
        given(chatRepository.isUserParticipant(chatId, strangerName)).willReturn(false);

        NotEnoughRightsException result = assertThrows(NotEnoughRightsException.class, () ->
                messageService.sendMessage(chatId, new MessageCreateDto("Hi"), strangerName)
        );

        assertEquals("Недостаточно прав для этого действия: Вы не являетесь участником этого чата", result.getMessage());
        verify(messageRepository, never()).save(any());
    }

    @Test
    void sendMessage_ShouldThrowException_WhenChatNotFound() {
        Long chatId = 99L;
        given(chatRepository.findById(chatId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                messageService.sendMessage(chatId, new MessageCreateDto("text"), "user")
        );

        verifyNoInteractions(messageRepository, userRepository);
    }

    @Test
    void sendMessage_ShouldThrowNotEnoughRightsException_WhenSenderNotFound() {
        Long chatId = 1L;
        String username = "user";

        User participant = new User();
        User participant2 = new User();
        participant2.setUsername("username");
        participant.setUsername(username);
        Chat chat = new Chat();
        chat.setSeller(participant);
        chat.setBuyer(participant2);

        given(chatRepository.findById(chatId)).willReturn(Optional.of(chat));
        given(chatRepository.isUserParticipant(chatId, username)).willReturn(false);

        assertThrows(NotEnoughRightsException.class, () ->
                messageService.sendMessage(chatId, new MessageCreateDto("text"), username)
        );
    }

    @Test
    void getMessagesByChat_ShouldReturnPageResponse_Success() {
        Long chatId = 1L;
        String username = "participantUser";
        int page = 0;
        int size = 10;

        User participant = new User();
        participant.setUsername(username);

        User participant2 = new User();
        participant2.setUsername("buyer");
        Chat chat = new Chat();
        chat.setId(chatId);
        chat.setSeller(participant);
        chat.setBuyer(participant2);

        Message message = new Message();
        message.setContent("Hello");
        List<Message> messages = List.of(message);

        MessageResponseDto dto = new MessageResponseDto(1L, "Hello", LocalDateTime.now(), username, true, false);

        given(chatRepository.isUserParticipant(chatId, username)).willReturn(true);
        given(messageRepository.findByChatWithPagination(chatId, page, size)).willReturn(messages);
        given(messageRepository.countByChat(chatId)).willReturn(1L);
        given(mapper.toDto(message, username)).willReturn(dto);

        PageResponse<MessageResponseDto> result = messageService.getMessagesByChat(chatId, username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(1, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(messageRepository).findByChatWithPagination(chatId, page, size);
    }

    @Test
    void getMessagesByChat_ShouldThrowENFException_WhenUserNotParticipant() {
        Long chatId = 1L;
        String stranger = "hacker";

        Chat chat = new Chat();
        chat.setSeller(new User());
        chat.getSeller().setUsername("seller");
        chat.setBuyer(new User());
        chat.getBuyer().setUsername("buyer");

        given(chatRepository.isUserParticipant(chatId, stranger)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                messageService.getMessagesByChat(chatId, stranger, 0, 10)
        );

        verifyNoInteractions(messageRepository);
    }

    @Test
    void getMessagesByChat_ShouldThrowENFException_WhenChatNotFound() {
        Long chatId = 99L;

        given(chatRepository.isUserParticipant(eq(chatId), anyString())).willReturn(false);
        assertThrows(EntityNotFoundException.class, () ->
                messageService.getMessagesByChat(chatId, "anyUser", 0, 10)
        );
    }

    @Test
    void editMessage_ShouldReturnDto_Success() {
        Long messageId = 1L;
        String username = "senderUser";
        String newContent = "Updated text";

        User sender = new User();
        sender.setUsername(username);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);
        message.setContent("Old text");

        MessageUpdateDto updateDto = new MessageUpdateDto(newContent);
        MessageResponseDto expectedDto = new MessageResponseDto(messageId, newContent, LocalDateTime.now(), username, true, false);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(mapper.toDto(message, username)).willReturn(expectedDto);

        MessageResponseDto result = messageService.editMessage(messageId, updateDto, username);

        assertNotNull(result);
        assertEquals(newContent, message.getContent());
        assertEquals(expectedDto, result);

        verify(messageRepository).findById(messageId);
        verify(mapper).toDto(message, username);
    }

    @Test
    void editMessage_ShouldThrowNotEnoughRightsException_WhenUserIsNotSender() {
        Long messageId = 1L;
        String strangerName = "notSender";

        User realSender = new User();
        realSender.setUsername("originalAuthor");

        Message message = new Message();
        message.setSender(realSender);

        MessageUpdateDto updateDto = new MessageUpdateDto("I hacked you");

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        NotEnoughRightsException ex = assertThrows(NotEnoughRightsException.class, () ->
                messageService.editMessage(messageId, updateDto, strangerName)
        );

        assertEquals("Недостаточно прав для этого действия: Вы не можете изменять чужие сообщения", ex.getMessage());
        assertNotEquals("I hacked you", message.getContent());
    }

    @Test
    void editMessage_ShouldThrowENFException_WhenMessageNotFound() {
        Long messageId = 1L;
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                messageService.editMessage(messageId, new MessageUpdateDto("text"), "anyUser")
        );
    }

    @Test
    void deleteMessage_Success() {
        Long messageId = 1L;
        String username = "authorUser";

        User sender = new User();
        sender.setUsername(username);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        messageService.deleteMessage(messageId, username);

        verify(messageRepository).delete(message);
        verify(messageRepository).findById(messageId);
    }

    @Test
    void deleteMessage_ShouldThrowNotEnoughRightsException_WhenUserIsNotSender() {
        Long messageId = 1L;
        String currentUsername = "otherUser";

        User actualSender = new User();
        actualSender.setUsername("originalAuthor");

        Message message = new Message();
        message.setSender(actualSender);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        NotEnoughRightsException ex = assertThrows(NotEnoughRightsException.class, () ->
                messageService.deleteMessage(messageId, currentUsername)
        );

        assertEquals("Недостаточно прав для этого действия: Вы не можете удалять чужие сообщения", ex.getMessage());

        verify(messageRepository, never()).delete(any());
    }

    @Test
    void deleteMessage_ShouldThrowENFException_WhenMessageNotFound() {
        Long messageId = 1L;
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                messageService.deleteMessage(messageId, "anyUser")
        );

        verify(messageRepository, never()).delete(any());
    }



}
