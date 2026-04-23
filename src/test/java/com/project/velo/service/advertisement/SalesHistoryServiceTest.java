package com.project.velo.service.advertisement;

import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.SalesPrivateHistoryResponseDto;
import com.project.velo.dto.response.SalesPublicHistoryResponseDto;
import com.project.velo.entity.SalesHistory;
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

import static org.junit.jupiter.api.Assertions.*;
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
        SalesPublicHistoryResponseDto dto = new SalesPublicHistoryResponseDto(
                1L, "Bike", LocalDateTime.now()
        );

        given(userRepository.existsByUsername(username)).willReturn(true);
        given(salesHistoryRepository.findAllBySellerOrderBySoldAt(username, page, size))
                .willReturn(List.of(salesHistory));
        given(salesHistoryRepository.countSalesBySeller(username)).willReturn(12L);
        given(mapper.toPublicDto(salesHistory)).willReturn(dto);

        PageResponse<SalesPublicHistoryResponseDto> result = salesHistoryService.getPublicSales(username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(12L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(userRepository).existsByUsername(username);
        verify(salesHistoryRepository).findAllBySellerOrderBySoldAt(username, page, size);
        verify(salesHistoryRepository).countSalesBySeller(username);
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
        SalesPrivateHistoryResponseDto dto = new SalesPrivateHistoryResponseDto(
                1L, "Bike", 1L, new BigDecimal("120.00"), "buyer-username", LocalDateTime.now()
        );

        given(userRepository.existsByUsername(username)).willReturn(true);
        given(salesHistoryRepository.findAllBySellerOrderBySoldAt(username, page, size))
                .willReturn(List.of(salesHistory));
        given(salesHistoryRepository.countSalesBySeller(username)).willReturn(12L);
        given(mapper.toPrivateDto(salesHistory)).willReturn(dto);

        PageResponse<SalesPrivateHistoryResponseDto> result = salesHistoryService.getPrivateSales(username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(12L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(userRepository).existsByUsername(username);
        verify(salesHistoryRepository).findAllBySellerOrderBySoldAt(username, page, size);
        verify(salesHistoryRepository).countSalesBySeller(username);
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
}
