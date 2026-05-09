package com.project.velo.service.advertisement;

import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.salesHistory.SalesHistoryPrivateResponseDto;
import com.project.velo.dto.response.salesHistory.SalesHistoryPublicResponseDto;
import com.project.velo.entity.SalesHistory;
import com.project.velo.entity.User;
import com.project.velo.mapper.SalesHistoryMapper;
import com.project.velo.repository.SalesHistoryRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class SalesHistoryServiceTest {

    @Mock
    private SalesHistoryRepository salesHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SalesHistoryMapper mapper;

    @InjectMocks
    private SalesHistoryService salesHistoryService;

    @Test
    void getPublicSales_ShouldReturnPageResponse_Success() {
        String username = "sellerUser";
        int page = 0;
        int size = 5;

        SalesHistory salesHistory = new SalesHistory();
        SalesHistoryPublicResponseDto dto = new SalesHistoryPublicResponseDto(
                1L, "Bike", LocalDateTime.now()
        );
        User user = new User();
        user.setUsername(username);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(salesHistoryRepository.findSalesByUserOrderBySoldAt(username, page, size))
                .willReturn(List.of(salesHistory));
        given(salesHistoryRepository.countSalesByUser(username)).willReturn(12L);
        given(mapper.toPublicDto(salesHistory)).willReturn(dto);

        PageResponse<SalesHistoryPublicResponseDto> result = salesHistoryService.getPublicSales(username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(12L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(salesHistoryRepository).findSalesByUserOrderBySoldAt(username, page, size);
        verify(salesHistoryRepository).countSalesByUser(username);
    }

    @Test
    void getPublicSales_ShouldThrowENFException_WhenUserNotFound() {
        String username = "username";
        given(userRepository.existsByUsername(username)).willReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                salesHistoryService.getPrivateSales(username, 0, 10)
        );

        assertEquals("Пользователя с username " + username + " не найдено", ex.getMessage());
        verifyNoInteractions(salesHistoryRepository);
        verifyNoInteractions(mapper);
    }


    @Test
    void getPrivateSales_ShouldReturnPageResponse_Success() {
        String username = "sellerUser";
        int page = 0;
        int size = 5;

        SalesHistory salesHistory = new SalesHistory();
        SalesHistoryPrivateResponseDto dto = new SalesHistoryPrivateResponseDto(
                1L,
                "Bike",
                1L,
                new BigDecimal("120.00"),
                "buyer-username",
                "seller-username",
                LocalDateTime.now(),
                true
        );

        given(userRepository.existsByUsername(username)).willReturn(true);
        given(salesHistoryRepository.findSalesByUserOrderBySoldAt(username, page, size))
                .willReturn(List.of(salesHistory));
        given(salesHistoryRepository.countSalesByUser(username)).willReturn(12L);
        given(mapper.toPrivateDto(salesHistory)).willReturn(dto);

        PageResponse<SalesHistoryPrivateResponseDto> result = salesHistoryService.getPrivateSales(username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(12L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(userRepository).existsByUsername(username);
        verify(salesHistoryRepository).findSalesByUserOrderBySoldAt(username, page, size);
        verify(salesHistoryRepository).countSalesByUser(username);
    }

    @Test
    void getPrivateSales_ShouldThrowENFException_WhenUserNotFound() {
        String username = "username";
        given(userRepository.existsByUsername(username)).willReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                salesHistoryService.getPrivateSales(username, 0, 10)
        );

        assertEquals("Пользователя с username " + username + " не найдено", ex.getMessage());
        verifyNoInteractions(salesHistoryRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void getPurchases_ShouldReturnPageResponse_Success() {
        String username = "buyer";
        SalesHistoryPrivateResponseDto dto = new SalesHistoryPrivateResponseDto(
                1L,
                "title",
                1L,
                BigDecimal.ONE,
                "buyer",
                "seller",
                LocalDateTime.now(),
                true
        );

        PageResponse<SalesHistoryPrivateResponseDto> pageResponse = new PageResponse<>(List.of(dto), 1, 1, 0, 10);
        given(salesHistoryRepository.findPurchasesByUserOrderBySoldAt(username, 0, 10))
                .willReturn(List.of(new SalesHistory()));
        given(salesHistoryRepository.countPurchasesByUser(username)).willReturn(1L);
        given(mapper.toPrivateDto(any())).willReturn(dto);
        PageResponse<SalesHistoryPrivateResponseDto> result = salesHistoryService.getPurchases("buyer", 0, 10);
        assertNotNull(result);
        assertEquals(result, pageResponse);

        verify(salesHistoryRepository).findPurchasesByUserOrderBySoldAt("buyer", 0, 10);
        verify(mapper).toPrivateDto(any());
    }
}
