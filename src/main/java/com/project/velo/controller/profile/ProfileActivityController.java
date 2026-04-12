package com.project.velo.controller.profile;

import com.project.velo.dto.response.AdvertisementResponseDto;
import com.project.velo.dto.response.SalesHistoryResponseDto;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.SalesHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileActivityController {

    private final SalesHistoryService salesHistoryService;
    private final AdvertisementService advertisementService;

    @GetMapping("/my/sales")
    public ResponseEntity<List<SalesHistoryResponseDto>> getMySales(
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("GET /api/profiles/my/sales - Fetching sales by user: {}", user.getUsername());
        List<SalesHistoryResponseDto> mySales = salesHistoryService.getSales(user.getUsername());
        log.info("GET /api/profiles/my/sales - Found {} sales by user: {}", mySales.size(), user.getUsername());
        return ResponseEntity.ok(mySales);
    }

    @GetMapping("/{username}/sales")
    public ResponseEntity<List<SalesHistoryResponseDto>> getUserSales(
            @PathVariable String username
    ) {
        log.info("GET /api/profiles/{}/sales - Fetching sales by user: {}", username, username);
        List<SalesHistoryResponseDto> mySales = salesHistoryService.getSales(username);
        log.info("GET /api/profiles/{}/sales - Found {} sales by user: {}", username, mySales.size(), username);
        return ResponseEntity.ok(mySales);
    }

    @GetMapping("/my/advertisements")
    public ResponseEntity<List<AdvertisementResponseDto>> getMyActiveAdvertisements(
            @AuthenticationPrincipal UserDetails user
    ) {
        log.info("GET /api/profiles/my/advertisements - Fetching advertisements by user: {}", user.getUsername());
        List<AdvertisementResponseDto> advertisements = advertisementService.findAdvertisementsByUsername(user.getUsername());
        log.info("GET /api/profiles/my/advertisements - Found {} advertisements by user: {}", advertisements.size(), user.getUsername());
        return ResponseEntity.ok(advertisements);
    }

    @GetMapping("/{username}/advertisements")
    public ResponseEntity<List<AdvertisementResponseDto>> getUserActiveAdvertisements(
            @PathVariable String username
    ) {
        log.info("GET /api/profiles/{}/advertisements - Fetching advertisements by user: {}", username, username);
        List<AdvertisementResponseDto> mySales = advertisementService.findAdvertisementsByUsername(username);
        log.info("GET /api/profiles/{}/advertisements - Found {} advertisements by user: {}", username, mySales.size(), username);
        return ResponseEntity.ok(mySales);
    }
}
