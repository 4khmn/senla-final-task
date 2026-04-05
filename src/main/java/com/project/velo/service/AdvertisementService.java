package com.project.velo.service;

import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.update.AdvertisementUpdateDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdvertisementService {


    AdvertisementResponseDto create(AdvertisementCreateDto dto, String username);

    AdvertisementResponseDto getById(Long id);

    List<AdvertisementResponseDto> getAll();

    AdvertisementResponseDto update(Long id, AdvertisementUpdateDto dto, String username);

    void delete(Long id, String username);
}
