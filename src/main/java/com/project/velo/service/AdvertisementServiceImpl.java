package com.project.velo.service;

import com.project.velo.dto.update.AdvertisementUpdateDto;
import com.project.velo.entity.*;
import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.entity.enums.AdStatus;
import com.project.velo.exception.AdvertisementNotAvailableException;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.exception.ResourceAlreadyProcessedException;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.AdvertisementMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.CategoryRepository;
import com.project.velo.repository.SalesHistoryRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementMapper mapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SalesHistoryRepository salesHistoryRepository;


    @Override
    @Transactional
    public AdvertisementResponseDto create(AdvertisementCreateDto dto, String username) {
        Advertisement advertisement = mapper.toEntity(dto);
        User seller = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено.")
        );
        advertisement.setSeller(seller);

        Category category = categoryRepository.findById(dto.categoryId()).orElseThrow(
                () -> new EntityNotFoundException("Категория не найдена"));
        advertisement.setCategory(category);

        if (dto.imageUrls() != null && !dto.imageUrls().isEmpty()) {
            List<AdImage> images = new ArrayList<>();
            for (int i = 0; i < dto.imageUrls().size(); i++) {
                images.add(AdImage.builder()
                        .imageUrl(dto.imageUrls().get(i))
                        .advertisement(advertisement)
                        .isPrimary(i == 0)
                        .build());
            }
            advertisement.setImages(images);
        }

        Advertisement savedAd = advertisementRepository.save(advertisement);
        return mapper.toDto(savedAd);
    }

    @Override
    public AdvertisementResponseDto getById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено.")
        );
        if (advertisement.getStatus() != AdStatus.ACTIVE) {
            throw new AdvertisementNotAvailableException("Объявление с id " + id + " не доступно.");
        }
        return mapper.toDto(advertisement);
    }

    @Override
    public List<AdvertisementResponseDto> getAll() {
        return advertisementRepository.findAll().stream()
                .filter(ad -> ad.getStatus().equals(AdStatus.ACTIVE))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AdvertisementResponseDto update(Long id, AdvertisementUpdateDto dto, String username) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено.")
        );
        if (!advertisement.getSeller().getUsername().equals(username)) {
            throw new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете изменить чужое объявление");
        }

        mapper.updateEntityFromDto(dto, advertisement);

        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Категория не найдена"));
            advertisement.setCategory(category);
        }

        if (dto.imageUrls() != null) {
            advertisement.getImages().clear();
            advertisementRepository.saveAndFlush(advertisement);

            List<String> urls = dto.imageUrls();
            for (int i = 0; i < urls.size(); i++) {
                AdImage newImg = new AdImage();
                newImg.setImageUrl(urls.get(i));
                newImg.setAdvertisement(advertisement);
                newImg.setPrimary(i == 0);
                advertisement.getImages().add(newImg);
            }
        }

        return mapper.toDto(advertisement);
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено.")
        );
        if (advertisement.getSeller().getUsername().equals(username) || advertisement.getSeller().getRole().equals("ROLE_ADMIN")) {
            advertisement.setStatus(AdStatus.ARCHIVED);
        }
        else {
            throw new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете удалить чужое объявление");
        }
    }

    @Override
    @Transactional
    public void processPurchase(Long adId, String username) {
        Advertisement advertisement = advertisementRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));

        User buyer = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено.")
        );
        if (advertisement.getSeller().getUsername().equals(username)) {
            throw new ValidationException("Нельзя купить свой собственный товар");
        }
        if (advertisement.getStatus() == AdStatus.SOLD) {
            throw new ResourceAlreadyProcessedException("Этот товар уже продан");
        }

        advertisement.setStatus(AdStatus.SOLD);

        SalesHistory history = SalesHistory.builder()
                .advertisement(advertisement)
                .seller(advertisement.getSeller())
                .buyer(buyer)
                .finalPrice(advertisement.getPrice())
                .build();

        salesHistoryRepository.save(history);
    }

    @Override
    public List<AdvertisementResponseDto> findAdvertisementsByUsername(String username) {
        return advertisementRepository.findAllByUsername(username).stream().map(mapper::toDto).toList();
    }
}
