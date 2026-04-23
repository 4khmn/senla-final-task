package com.project.velo.service.social;

import com.project.velo.dto.response.chat.ChatListResponseDto;
import com.project.velo.dto.response.chat.ChatResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Chat;
import com.project.velo.entity.User;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.ChatMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.ChatRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ChatMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<ChatListResponseDto> findAllByUsername(String username, int page, int size) {
        List<Chat> chats = chatRepository.findAllByUsernameWithPagination(username, page, size);

        long totalElements = chatRepository.countByUsername(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        List<ChatListResponseDto> dtos = chats.stream()
                .map(chat -> mapper.toListDto(chat, username))
                .toList();
        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }

    @Transactional
    public ChatResponseDto getOrCreate(Long adId, String username) {
        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с username " + username + " не найдено"));

        return chatRepository.findByAdvertisementIdAndBuyerId(adId, buyer.getId())
                .map(mapper::toResponseDto)
                .orElseGet(() -> createNewChat(adId, buyer));
    }

    private ChatResponseDto createNewChat(Long adId, User buyer) {
        Advertisement advertisement = advertisementRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Объявления с id " + adId + " не найдено"));

        if (advertisement.getSeller().getUsername().equals(buyer.getUsername())) {
            throw new ValidationException("Вы не можете начать чат с самим собой");
        }

        Chat newChat = Chat.builder()
                .advertisement(advertisement)
                .seller(advertisement.getSeller())
                .buyer(buyer)
                .build();

        return mapper.toResponseDto(chatRepository.save(newChat));
    }
}
