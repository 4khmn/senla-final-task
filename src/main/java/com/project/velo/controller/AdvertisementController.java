package com.project.velo.controller;

import com.project.velo.dto.create.AdvertisementCreateDto;
import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.update.AdvertisementPromoteDto;
import com.project.velo.dto.update.AdvertisementUpdateDto;
import com.project.velo.service.AdvertisementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @GetMapping
    public ResponseEntity<List<AdvertisementResponseDto>> getAllAdvertisements() {
        log.info("GET /api/advertisements - Fetching all advertisements");
        List<AdvertisementResponseDto> advertisements = advertisementService.getAll();
        log.info("GET /api/advertisements - Found {} advertisements", advertisements.size());
        return ResponseEntity.ok(advertisements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertisementResponseDto> getAdvertisementById(@PathVariable Long id) {
        log.info("GET /api/advertisements/{} - Fetching advertisement by id: {}", id, id);
        AdvertisementResponseDto advertisement = advertisementService.getById(id);
        log.info("GET /api/advertisements/{} - advertisement with id: {} successfully retrieved", id, id);
        return ResponseEntity.ok(advertisement);
    }

    @PostMapping
    public ResponseEntity<AdvertisementResponseDto> createAdvertisement(
            @RequestBody @Valid AdvertisementCreateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("POST /api/advertisements - User: {} trying to create an advertisement: {}", user.getUsername(), dto);
        AdvertisementResponseDto advertisement = advertisementService.create(dto, user.getUsername());
        log.info("POST /api/advertisements - Advertisement with id: {} successfully created for user: {}", advertisement.id(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(advertisement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        log.info("DELETE /api/advertisements/{} - Deleting advertisement with id: {}", id, id);
        advertisementService.delete(id, user.getUsername());
        log.info("DELETE /api/advertisements/{} - Advertisement with id: {} was successfully deleted", id, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdvertisementResponseDto> updateAdvertisement(
            @PathVariable Long id,
            @RequestBody @Valid AdvertisementUpdateDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("PATCH /api/advertisement/{} - Updating advertisement with id: {}", id, id);
        AdvertisementResponseDto advertisement = advertisementService.update(id, dto, user.getUsername());
        log.info("PATCH /api/advertisement/{} - Advertisement with id: {} was successfully updated", id, id);
        return ResponseEntity.ok(advertisement);
    }

    @PostMapping("/{adId}/buy")
    public ResponseEntity<Void> buyAdvertisement(
            @PathVariable Long adId,
            @AuthenticationPrincipal UserDetails buyer
    ) {
        log.info("POST /api/advertisements/{}/buy - User {} is buying by advertisement {}", adId, buyer.getUsername(), adId);
        advertisementService.processPurchase(adId, buyer.getUsername());
        log.info("POST /api/advertisements/{}/buy - User {} successfully purchased by advertisement {}", adId,buyer.getUsername(), adId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{adId}/promote")
    public ResponseEntity<Void> promote(
            @PathVariable Long adId,
            @RequestBody AdvertisementPromoteDto dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        advertisementService.promote(adId, dto, user.getUsername());
        return ResponseEntity.noContent().build();
    }


}
