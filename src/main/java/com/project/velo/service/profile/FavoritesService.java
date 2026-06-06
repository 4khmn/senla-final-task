package com.project.velo.service.profile;

import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Favorite;
import com.project.velo.entity.FavoriteId;
import com.project.velo.entity.User;
import com.project.velo.mapper.FavoriteMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.FavoriteRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final FavoriteRepository favoriteRepository;
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final FavoriteMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<AdvertisementShortResponseDto> getAllByUser(String username, int page, int size){
        List<Favorite> entities = favoriteRepository.getAllByUser(username, page, size);

        long totalElements = favoriteRepository.countByUser(username);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<AdvertisementShortResponseDto> dtos = entities.stream()
                .map(mapper::toDto)
                .toList();

        return new PageResponse<>(
                dtos,
                totalElements,
                totalPages,
                page,
                size
        );
    }

    @Transactional
    public AdvertisementShortResponseDto addToFavorites(String username, long adId) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        Advertisement advertisement = advertisementRepository.findById(adId).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + adId + " не найдено")
        );
        FavoriteId id = new FavoriteId(user.getId(), advertisement.getId());

        Favorite favorite = null;
        if (!favoriteRepository.existsById(id)) {
            favorite = Favorite.builder()
                    .user(user)
                    .advertisement(advertisement)
                    .build();
            favoriteRepository.save(favorite);
        } else {
            favorite = favoriteRepository.findById(id).orElseThrow();
        }
        return mapper.toDto(favorite);
    }

    @Transactional
    public void deleteFromFavorites(String username, long adId) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        FavoriteId id = new FavoriteId(user.getId(), adId);

        favoriteRepository.findById(id).ifPresent(favoriteRepository::delete);
    }
}
