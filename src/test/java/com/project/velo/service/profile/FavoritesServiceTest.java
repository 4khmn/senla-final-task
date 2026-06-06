package com.project.velo.service.profile;

import com.project.velo.dto.response.advertisement.AdvertisementShortResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Favorite;
import com.project.velo.entity.User;
import com.project.velo.mapper.FavoriteMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.FavoriteRepository;
import com.project.velo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoritesServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FavoriteMapper mapper;

    @InjectMocks
    private FavoritesService favoritesService;


    @Test
    void getAllByUser_Success() {
        String username = "username";
        User user = new User();
        Advertisement advertisement = new Advertisement();
        AdvertisementShortResponseDto advertisementShortResponseDto = new AdvertisementShortResponseDto(
                1L,
            "title",
            BigDecimal.ONE,
            "categoryName",
            null,
            false,
            true,
            LocalDateTime.now(),
            "sellerUsername",
            BigDecimal.ONE
        );
        Favorite favorite = Favorite.builder().user(user).advertisement(advertisement).build();
        given(favoriteRepository.getAllByUser(eq(username), anyInt(), anyInt())).willReturn(List.of(favorite));
        given(favoriteRepository.countByUser(eq(username))).willReturn(1L);
        given(mapper.toDto(favorite)).willReturn(advertisementShortResponseDto);

        PageResponse<AdvertisementShortResponseDto> result = favoritesService.getAllByUser(username, 1, 10);

        assertEquals(1, result.content().size());
        assertEquals(advertisementShortResponseDto, result.content().get(0));


        verify(favoriteRepository).getAllByUser(eq(username), anyInt(), anyInt());
        verify(favoriteRepository).countByUser(eq(username));
        verify(mapper, times(1)).toDto(favorite);
    }

    @Test
    void addToFavorites_WhenAdvertisementDoesNotInFavorites() {
        String username = "username";
        User user = new User();
        user.setUsername(username);
        long adId = 1L;
        Advertisement advertisement = new Advertisement();
        advertisement.setId(adId);
        AdvertisementShortResponseDto advertisementShortResponseDto = new AdvertisementShortResponseDto(
                1L,
                "title",
                BigDecimal.ONE,
                "categoryName",
                null,
                false,
                true,
                LocalDateTime.now(),
                "sellerUsername",
                BigDecimal.ONE
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(advertisementRepository.findById(adId)).willReturn(Optional.of(advertisement));
        given(favoriteRepository.existsById(any())).willReturn(false);
        given(mapper.toDto(any())).willReturn(advertisementShortResponseDto);

        AdvertisementShortResponseDto result = favoritesService.addToFavorites(username, adId);

        assertEquals(result, advertisementShortResponseDto);
        verify(favoriteRepository).existsById(any());
        verify(favoriteRepository).save(any());
        verify(favoriteRepository, never()).findById(any());
    }

    @Test
    void addToFavorites_WhenAdvertisementAlreadyInFavorites() {
        String username = "username";
        User user = new User();
        user.setUsername(username);
        long adId = 1L;
        Advertisement advertisement = new Advertisement();
        advertisement.setId(adId);

        Favorite favorite = Favorite.builder().user(user).advertisement(advertisement).build();
        AdvertisementShortResponseDto advertisementShortResponseDto = new AdvertisementShortResponseDto(
                1L,
                "title",
                BigDecimal.ONE,
                "categoryName",
                null,
                false,
                true,
                LocalDateTime.now(),
                "sellerUsername",
                BigDecimal.ONE
        );

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(advertisementRepository.findById(adId)).willReturn(Optional.of(advertisement));
        given(favoriteRepository.existsById(any())).willReturn(true);
        given(favoriteRepository.findById(any())).willReturn(Optional.of(favorite));
        given(mapper.toDto(any())).willReturn(advertisementShortResponseDto);

        AdvertisementShortResponseDto result = favoritesService.addToFavorites(username, adId);

        assertEquals(result, advertisementShortResponseDto);
        verify(favoriteRepository).existsById(any());
        verify(favoriteRepository, never()).save(any());
        verify(favoriteRepository).findById(any());
    }

    @Test
    void deleteFromFavorites_WhenAdvertisementAlreadyInFavorites() {
        String username = "username";
        User user = new User();
        user.setUsername(username);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        Favorite favorite = Favorite.builder().user(user).advertisement(null).build();

        given(favoriteRepository.findById(any())).willReturn(Optional.of(favorite));

        favoritesService.deleteFromFavorites(username, 1);
        verify(userRepository).findByUsername(username);
        verify(favoriteRepository).delete(any());
    }

    @Test
    void deleteFromFavorites_WhenAdvertisementDoesNotInFavorites() {
        String username = "username";
        User user = new User();
        user.setUsername(username);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        given(favoriteRepository.findById(any())).willReturn(Optional.empty());

        favoritesService.deleteFromFavorites(username, 1);
        verify(userRepository).findByUsername(username);
        verify(favoriteRepository, never()).delete(any());
    }
}
