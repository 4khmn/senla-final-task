package com.project.velo.service.social;

import com.project.velo.dto.response.ChatListResponseDto;
import com.project.velo.dto.response.ChatResponseDto;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Chat;
import com.project.velo.entity.User;
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
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ChatMapper mapper;

    @Override
    public List<ChatListResponseDto> findAllByUser(String username) {
        List<Chat> chats = chatRepository.findAllByUsername(username);

        return chats.stream()
                .map(chat -> mapper.toListDto(chat, username))
                .toList();
    }

    @Override
    @Transactional
    public ChatResponseDto getOrCreate(Long adId, String username) {
        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с username " + username + " не найдено."));

        return chatRepository.findByAdvertisementIdAndBuyerId(adId, buyer.getId())
                .map(mapper::toResponseDto)
                .orElseGet(() -> createNewChat(adId, buyer));
    }

    private ChatResponseDto createNewChat(Long adId, User buyer) {
        Advertisement advertisement = advertisementRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Объявления с id " + adId + " не найдено."));

        if (advertisement.getSeller().getUsername().equals(buyer.getUsername())) {
            throw new IllegalArgumentException("Вы не можете начать чат с самим собой");
        }

        Chat newChat = Chat.builder()
                .advertisement(advertisement)
                .seller(advertisement.getSeller())
                .buyer(buyer)
                .build();

        return mapper.toResponseDto(chatRepository.save(newChat));
    }
}
