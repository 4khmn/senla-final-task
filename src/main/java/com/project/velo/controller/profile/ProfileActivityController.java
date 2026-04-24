package com.project.velo.controller.profile;

import com.project.velo.dto.response.advertisement.AdvertisementResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.salesHistory.SalesHistoryPrivateResponseDto;
import com.project.velo.dto.response.salesHistory.SalesHistoryPublicResponseDto;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.SalesHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile: Activity", description = "Управление активностью пользователей")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileActivityController {

    private final SalesHistoryService salesHistoryService;
    private final AdvertisementService advertisementService;

    @Operation(
            summary = "Получить историю продаж текущего пользователя",
            description = "Получение полной информации о завершенных сделках текущего пользователя",
            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/my/sales")
    public ResponseEntity<PageResponse<SalesHistoryPrivateResponseDto>> getMySales(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/profiles/my/sales - Fetching sales by user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<SalesHistoryPrivateResponseDto> mySales = salesHistoryService.getPrivateSales(user.getUsername(), page, size);
        log.info("GET /api/profiles/my/sales - Found {} sales by user: {}, page: {}, size: {}", mySales.content().size(), user.getUsername(), page, size);
        return ResponseEntity.ok(mySales);
    }

    @Operation(
            summary = "Получить историю продаж пользователя по username",
            description = "Получение публичной информации о завершенных сделках пользователя"
    )
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @GetMapping("/{username}/sales")
    public ResponseEntity<PageResponse<SalesHistoryPublicResponseDto>> getUserSales(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/profiles/{}/sales - Fetching sales by user: {}, page: {}, size: {}", username, username, page, size);
        PageResponse<SalesHistoryPublicResponseDto> mySales = salesHistoryService.getPublicSales(username, page, size);
        log.info("GET /api/profiles/{}/sales - Found {} sales by user: {}, page: {}, size: {}", username, mySales.content().size(), username, page, size);
        return ResponseEntity.ok(mySales);
    }

    @Operation(summary = "Получить список активных объявдений текущего пользователя", security = @SecurityRequirement(name = "JWT"))
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

    @Operation(summary = "Получить историю продаж текущего пользователя")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
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
