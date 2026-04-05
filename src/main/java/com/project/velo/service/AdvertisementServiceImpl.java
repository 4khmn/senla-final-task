package com.project.velo.service;

import com.project.velo.entity.Advertisement;
import com.project.velo.dto.AdvertisementCreateDto;
import com.project.velo.dto.AdvertisementResponseDto;
import com.project.velo.exception.NotEnoughRights;
import com.project.velo.mapper.AdvertisementMapper;
import com.project.velo.repository.AdvertisementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementMapper mapper;

    @Override
    public AdvertisementResponseDto create(AdvertisementCreateDto dto, String username) {
        return null;
    }

    @Override
    public AdvertisementResponseDto getById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено.")
        );
        return mapper.toDto(advertisement);
    }

    @Override
    public List<AdvertisementResponseDto> getAll() {
        return advertisementRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public void updateStatus(Long id, String status, String username) {

    }

    @Override
    public void delete(Long id, String username) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено.")
        );
        if (advertisement.getSeller().getUsername().equals(username) || advertisement.getSeller().getRole().equals("ROLE_ADMIN")) {
            advertisementRepository.delete(advertisement);
        }
        else {
            throw new NotEnoughRights("Недостаточно прав для этого действия: Вы не можете удалить чужое объявление");
        }
    }
}
