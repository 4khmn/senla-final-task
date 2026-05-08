package com.project.velo.service.advertisement;

import com.project.velo.dto.request.AdvertisementFilterDto;
import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.update.AdvertisementPromoteDto;
import com.project.velo.dto.update.AdvertisementUpdateDto;
import com.project.velo.entity.*;
import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.response.advertisement.AdvertisementResponseDto;
import com.project.velo.entity.enums.AdStatus;
import com.project.velo.exception.*;
import com.project.velo.mapper.AdvertisementMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.CategoryRepository;
import com.project.velo.repository.SalesHistoryRepository;
import com.project.velo.repository.UserRepository;
import com.project.velo.service.storage.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementMapper mapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final FileStorageService storageService;


    @Transactional
    public AdvertisementResponseDto create(AdvertisementCreateDto dto, List<MultipartFile> files, String username) {
        Advertisement advertisement = mapper.toEntity(dto);
        User seller = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        advertisement.setSeller(seller);


        Category category = categoryRepository.findById(dto.categoryId()).orElseThrow(
                () -> new EntityNotFoundException("Категория с id " + dto.categoryId() + " не найдена"));
        advertisement.setCategory(category);


        if (files != null && !files.isEmpty()) {
            List<AdImage> images = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String savedPath = storageService.save(file, "advertisements");

                images.add(AdImage.builder()
                        .imageUrl(savedPath)
                        .advertisement(advertisement)
                        .isPrimary(i == 0)
                        .build());
            }
            advertisement.setImages(images);
        }


        Advertisement savedAd = advertisementRepository.save(advertisement);
        return mapper.toDto(savedAd);
    }

    @Transactional(readOnly = true)
    public AdvertisementResponseDto getById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено")
        );
        User user =  advertisement.getSeller();
        if (!user.isEnabled()) {
            throw new EntityNotFoundException("Объявление с id " + id + " не найдено или недоступно");
        }
        if (advertisement.getStatus() != AdStatus.ACTIVE) {
            throw new AdvertisementNotAvailableException("Объявление с id " + id + " не доступно");
        }
        return mapper.toDto(advertisement);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdvertisementShortResponseDto> getAll(AdvertisementFilterDto filter, int page, int size) {
        List<Advertisement> entities = advertisementRepository.findAllFiltered(filter, page, size);

        long totalElements = advertisementRepository.countFiltered(filter);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<AdvertisementShortResponseDto> dtos = entities.stream()
                .map(mapper::toShortDto)
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
    public AdvertisementResponseDto update(Long id, AdvertisementUpdateDto dto, List<MultipartFile> files, String username) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено")
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

        if (files != null && !files.isEmpty()) {
            advertisement.getImages().clear();
            advertisementRepository.flush();
            List<AdImage> newImages = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String savedPath = storageService.save(file, "advertisements");

                newImages.add(AdImage.builder()
                        .imageUrl(savedPath)
                        .advertisement(advertisement)
                        .isPrimary(i == 0)
                        .build());
            }
            advertisement.getImages().addAll(newImages);
        }

        return mapper.toDto(advertisement);
    }

    @Transactional
    public void delete(Long id, String username) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено")
        );

        if (advertisement.getSeller().getUsername().equals(username)) {
            if (advertisement.getStatus().equals(AdStatus.ACTIVE)) {
                advertisement.setStatus(AdStatus.ARCHIVED);
            } else {
                throw new NotEnoughRightsException("Вы не можете удалить неактивное объявление");
            }
        }
        else {
            throw new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете удалить чужое объявление");
        }
    }

    @Transactional
    public void deleteByAdmin(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено")
        );
        advertisement.setStatus(AdStatus.BANNED);
    }

    @Transactional
    public void processPurchase(Long adId, String username) {
        Advertisement advertisement = advertisementRepository.findById(adId).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + adId + " не найдено")
        );
        User buyer = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
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
                .wasTop(advertisement.isTop())
                .seller(advertisement.getSeller())
                .buyer(buyer)
                .finalPrice(advertisement.getPrice())
                .build();

        advertisement.setTop(false);

        salesHistoryRepository.save(history);
    }

    @Transactional
    public void promote(Long adId, AdvertisementPromoteDto dto, String username) {
        Advertisement advertisement = advertisementRepository.findById(adId).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + adId + " не найдено")
        );

        User currentUser = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не найден")
        );

        boolean isOwner = advertisement.getSeller().getUsername().equals(username);
        boolean isAdmin = currentUser.getRole().name().equals("ROLE_ADMIN");

        if (!isOwner && !isAdmin) {
            log.warn("Security Alert! User {} tried to promote advertisement: {} without rights", username, adId);
            throw new NotEnoughRightsException("У вас нет прав на продвижение этого объявления");
        }


        if (advertisement.getStatus() !=AdStatus.ACTIVE) {
            throw new AdvertisementNotAvailableException("Объявление с id " + adId + " не доступно");
        }

        LocalDateTime currentTopUntil = advertisement.getTopUntil();
        LocalDateTime baseTime = (currentTopUntil != null && currentTopUntil.isAfter(LocalDateTime.now()))
                ? currentTopUntil
                : LocalDateTime.now();

        advertisement.setTopUntil(baseTime.plusDays(dto.days()));
        advertisement.setTop(true);


        if (isAdmin && !isOwner) {
            log.info("ADMIN ACTION: Administrator: {} manually promoted advertisement: {} for {} days (Seller: {})",
                    username, adId, dto.days(), advertisement.getSeller().getUsername());
        } else {
            log.info("USER ACTION: Owner: {} promoted their advertisement: {} for {} days",
                    username, adId, dto.days());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<AdvertisementResponseDto> findAdvertisementsByUsername(String username, int page, int size) {
        User user =  userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        if (!user.isEnabled()) {
            throw new EntityNotFoundException("Пользователь с username " + username + " не найден или деактивирован");
        }
        List<Advertisement> advertisements = advertisementRepository.findAllByUsername(username, page, size);
        long totalElements = advertisementRepository.countByUsernameAndStatus(username, AdStatus.ACTIVE);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<AdvertisementResponseDto> dtos = advertisements.stream().map(mapper::toDto).toList();

        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }


    @Transactional(readOnly = true)
    public PageResponse<AdvertisementResponseDto> getAllForAdmin(int page, int size) {
        List<Advertisement> entities = advertisementRepository.findAllForAdmin(page, size);

        long totalElements = advertisementRepository.countAll();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<AdvertisementResponseDto> dtos = entities.stream()
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
}
