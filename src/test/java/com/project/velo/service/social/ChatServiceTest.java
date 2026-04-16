package com.project.velo.service.social;

import com.project.velo.dto.response.ChatListResponseDto;
import com.project.velo.dto.response.ChatResponseDto;
import com.project.velo.dto.response.PageResponse;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Chat;
import com.project.velo.entity.User;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.ChatMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.ChatRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private ChatMapper mapper;

    @InjectMocks
    private ChatService chatService;

    @Test
    void findAllByUsername_Success_ShouldReturnPageResponse() {
        String username = "testUser";
        int page = 0;
        int size = 10;

        Chat chat = new Chat();
        chat.setId(1L);

        List<Chat> chatList = List.of(chat);
        long totalElements = 25L;

        given(chatRepository.findAllByUsernameWithPagination(username, page, size)).willReturn(chatList);
        given(chatRepository.countByUsername(username)).willReturn(totalElements);

        ChatListResponseDto dto = new ChatListResponseDto(
                1L, 10L, "Велосипед", "Привет",
                LocalDateTime.now(), "interlocutor", "url"
        );

        given(mapper.toListDto(chat, username)).willReturn(dto);

        PageResponse<ChatListResponseDto> result = chatService.findAllByUsername(username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(totalElements, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(page, result.page());

        verify(chatRepository).findAllByUsernameWithPagination(username, page, size);
        verify(chatRepository).countByUsername(username);
    }

    @Test
    void findAllByUsername_ZeroChats_ShouldReturnPageResponse() {
        String username = "username";
        given(chatRepository.findAllByUsernameWithPagination(username, 0, 10)).willReturn(List.of());
        given(chatRepository.countByUsername(username)).willReturn(0L);
        PageResponse<ChatListResponseDto> result = chatService.findAllByUsername(username, 0, 10);

        assertNotNull(result);
        assertEquals(0, result.content().size());
        verify(chatRepository).countByUsername(username);
        verify(chatRepository).findAllByUsernameWithPagination(username, 0, 10);
        verifyNoInteractions(mapper);
    }

    @Test
    void getOrCreate_ChatAlreadyExists_ShouldReturnChatDto() {
        String username = "username";

        User buyer = new User();
        buyer.setUsername(username);
        buyer.setId(1L);
        Chat chat = new Chat();
        chat.setId(1L);
        chat.setBuyer(buyer);
        User seller = new User();
        seller.setUsername("username2");
        seller.setId(2L);
        chat.setSeller(seller);
        ChatResponseDto dto =  new ChatResponseDto(1L, 1L, "sellerUsername", "buyerUsername");
        given(userRepository.findByUsername(username)).willReturn(Optional.of(buyer));
        given(chatRepository.findByAdvertisementIdAndBuyerId(1L,  buyer.getId())).willReturn(Optional.of(chat));
        given(mapper.toResponseDto(chat)).willReturn(dto);

        ChatResponseDto result = chatService.getOrCreate(1L, username);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(userRepository).findByUsername(username);
        verify(mapper).toResponseDto(chat);
        verify(chatRepository).findByAdvertisementIdAndBuyerId(1L, buyer.getId());
    }

    @Test
    void getOrCreate_NewChat_AdvertisementExists_ShouldReturnChatDto() {
        String username = "username";

        User buyer = new User();
        buyer.setUsername(username);
        buyer.setId(1L);

        User seller = new User();
        seller.setUsername("username2");
        seller.setId(2L);
        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);
        advertisement.setId(1L);
        ChatResponseDto dto = new ChatResponseDto(1L, 1L, "sellerUsername", "buyerUsername");
        given(userRepository.findByUsername(username)).willReturn(Optional.of(buyer));
        given(chatRepository.findByAdvertisementIdAndBuyerId(1L, buyer.getId())).willReturn(Optional.empty());
        given(advertisementRepository.findById(1L)).willReturn(Optional.of(advertisement));
        given(chatRepository.save(any(Chat.class))).willAnswer(invocation -> invocation.getArgument(0));

        given(mapper.toResponseDto(any(Chat.class))).willReturn(dto);

        ChatResponseDto result = chatService.getOrCreate(1L, username);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(userRepository).findByUsername(username);
        verify(mapper).toResponseDto(any(Chat.class));
        verify(chatRepository).save(any(Chat.class));
        verify(advertisementRepository).findById(1L);
        verify(chatRepository).findByAdvertisementIdAndBuyerId(1L, buyer.getId());
    }


    @Test
    void getOrCreate_NewChat_AdvertisementDoesNotExist_ShouldThrowENFException() {
        String username = "username";

        User buyer = new User();
        buyer.setUsername(username);
        buyer.setId(1L);

        User seller = new User();
        seller.setUsername("username2");
        seller.setId(2L);
        ChatResponseDto dto = new ChatResponseDto(1L, 1L, "sellerUsername", "buyerUsername");
        given(userRepository.findByUsername(username)).willReturn(Optional.of(buyer));
        given(chatRepository.findByAdvertisementIdAndBuyerId(1L, buyer.getId())).willReturn(Optional.empty());
        given(advertisementRepository.findById(1L)).willReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> chatService.getOrCreate(1L, username));

        assertEquals("Объявления с id 1 не найдено", entityNotFoundException.getMessage());

        verify(userRepository).findByUsername(username);
        verifyNoInteractions(mapper);
        verify(advertisementRepository).findById(1L);
        verify(chatRepository).findByAdvertisementIdAndBuyerId(1L, buyer.getId());
    }

    @Test
    void getOrCreate_NewChat_WhenUserIsSeller_ShouldThrowValidationException() {
        String username = "username";

        User buyer = new User();
        buyer.setUsername(username);
        buyer.setId(1L);

        User seller = new User();
        seller.setUsername("username2");
        seller.setId(2L);
        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(buyer);
        advertisement.setId(1L);
        given(userRepository.findByUsername(username)).willReturn(Optional.of(buyer));
        given(chatRepository.findByAdvertisementIdAndBuyerId(1L, buyer.getId())).willReturn(Optional.empty());
        given(advertisementRepository.findById(1L)).willReturn(Optional.of(advertisement));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> chatService.getOrCreate(1L, username));

        assertEquals("Вы не можете начать чат с самим собой", validationException.getMessage());

        verify(userRepository).findByUsername(username);
        verifyNoInteractions(mapper);
        verify(chatRepository, never()).save(any(Chat.class));
        verify(advertisementRepository).findById(1L);
        verify(chatRepository).findByAdvertisementIdAndBuyerId(1L, buyer.getId());
    }
}
