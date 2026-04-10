package com.project.velo.service.advertisement;

import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.response.AdvertisementShortResponseDto;
import com.project.velo.dto.update.AdvertisementPromoteDto;
import com.project.velo.dto.update.AdvertisementUpdateDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface AdvertisementService {


    AdvertisementResponseDto create(AdvertisementCreateDto dto, List<MultipartFile> files, String username);

    AdvertisementResponseDto getById(Long id);

    List<AdvertisementShortResponseDto> getAll();

    AdvertisementResponseDto update(Long id, AdvertisementUpdateDto dto, String username);

    void delete(Long id, String username);

    void processPurchase(Long adId, String username);

    void promote(Long adId, AdvertisementPromoteDto dto, String username);

    List<AdvertisementResponseDto> findAdvertisementsByUsername(String username);


}
