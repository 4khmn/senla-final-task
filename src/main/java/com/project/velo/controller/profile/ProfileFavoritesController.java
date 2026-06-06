package com.project.velo.controller.profile;

import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.service.profile.FavoritesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile: Favorites", description = "Управление вкладкой избранное: добавление, удаление, просмотр")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/profiles/my/favorites")
public class ProfileFavoritesController {

    private final FavoritesService favoritesService;


    @GetMapping
    public ResponseEntity<PageResponse<AdvertisementShortResponseDto>> getMyFavorites(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/profiles/my/favorites - Fetching all favorites for user: {}, page: {}, size: {}", user.getUsername(), page, size);
        PageResponse<AdvertisementShortResponseDto> favorites = favoritesService.getAllByUser(user.getUsername(), page, size);
        log.info("GET /api/profiles/my/favorites - Found {} favorites", favorites.content().size());
        return ResponseEntity.ok(favorites);
    }

    @PostMapping
    public ResponseEntity<Void> addToFavorite(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam long adId
    ) {
        log.info("POST /api/profiles/my/favorites - User: {} trying to add to his favorites advertisement with id: {}", user.getUsername(), adId);
        favoritesService.addToFavorites(user.getUsername(), adId);
        log.info("POST /api/profiles/my/favorites - Advertisement: {} was successfully added to favorites by user: {}", adId, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFromFavorite(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam long adId
    ) {
        log.info("DELETE /api/profiles/my/favorites - User: {} trying to remove advertisement with id: {} from his favorites", user.getUsername(), adId);
        favoritesService.deleteFromFavorites(user.getUsername(), adId);
        log.info("DELETE /api/profiles/my/favorites - Advertisement: {} was successfully removed from favorites by user: {}", adId, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
