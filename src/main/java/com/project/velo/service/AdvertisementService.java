package com.project.velo.service;

import com.project.velo.dto.AdvertisementCreateDto;
import com.project.velo.dto.AdvertisementResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdvertisementService {


    AdvertisementResponseDto create(AdvertisementCreateDto dto, String username);

    AdvertisementResponseDto getById(Long id);

    List<AdvertisementResponseDto> getAll();

    void updateStatus(Long id, String status, String username);

    void delete(Long id, String username);
}
