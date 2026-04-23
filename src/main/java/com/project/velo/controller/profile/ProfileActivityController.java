package com.project.velo.controller.profile;

import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.SalesPrivateHistoryResponseDto;
import com.project.velo.dto.response.SalesPublicHistoryResponseDto;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.SalesHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileActivityController {

    private final SalesHistoryService salesHistoryService;
    private final AdvertisementService advertisementService;

    @GetMapping("/my/sales")
    public ResponseEntity<PageResponse<SalesPrivateHistoryResponseDto>> getMySales(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/profiles/my/sales - Fetching sales by user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<SalesPrivateHistoryResponseDto> mySales = salesHistoryService.getPrivateSales(user.getUsername(), page, size);
        log.info("GET /api/profiles/my/sales - Found {} sales by user: {}, page: {}, size: {}", mySales.content().size(), user.getUsername(), page, size);
        return ResponseEntity.ok(mySales);
    }

    @GetMapping("/{username}/sales")
    public ResponseEntity<PageResponse<SalesPublicHistoryResponseDto>> getUserSales(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/profiles/{}/sales - Fetching sales by user: {}, page: {}, size: {}", username, username, page, size);
        PageResponse<SalesPublicHistoryResponseDto> mySales = salesHistoryService.getPublicSales(username, page, size);
        log.info("GET /api/profiles/{}/sales - Found {} sales by user: {}, page: {}, size: {}", username, mySales.content().size(), username, page, size);
        return ResponseEntity.ok(mySales);
    }

    @GetMapping("/my/advertisements")
    public ResponseEntity<PageResponse<AdvertisementResponseDto>> getMyActiveAdvertisements(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/profiles/my/advertisements - Fetching advertisements by user: {}, page: {}, size: {}",
                user.getUsername(), page, size);
        PageResponse<AdvertisementResponseDto> advertisements = advertisementService.findAdvertisementsByUsername(user.getUsername(), page, size);
        log.info("GET /api/profiles/my/advertisements - Found {} advertisements by user: {}, page: {}, size: {}",
                advertisements.content().size(), user.getUsername(), page, size);
        return ResponseEntity.ok(advertisements);
    }

    @GetMapping("/{username}/advertisements")
    public ResponseEntity<PageResponse<AdvertisementResponseDto>> getUserActiveAdvertisements(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/profiles/{}/advertisements - Fetching advertisements by user: {}, page: {}, size: {}",
                username, username, page, size);
        PageResponse<AdvertisementResponseDto> mySales = advertisementService.findAdvertisementsByUsername(username, page, size);
        log.info("GET /api/profiles/{}/advertisements - Found {} advertisements by user: {}, page: {}, size: {}",
                username, mySales.content().size(), username, page, size);
        return ResponseEntity.ok(mySales);
    }
}
