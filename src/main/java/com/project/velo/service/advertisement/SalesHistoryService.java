package com.project.velo.service.advertisement;

import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.salesHistory.SalesHistoryPrivateResponseDto;
import com.project.velo.dto.response.salesHistory.SalesHistoryPublicResponseDto;
import com.project.velo.entity.User;
import com.project.velo.mapper.SalesHistoryMapper;
import com.project.velo.repository.SalesHistoryRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesHistoryService {

    private final SalesHistoryRepository salesHistoryRepository;
    private final UserRepository userRepository;
    private final SalesHistoryMapper mapper;

    @Transactional(readOnly = true)
    public PageResponse<SalesHistoryPrivateResponseDto> getPrivateSales(String username, int page, int size) {
        if (!userRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Пользователя с username " + username + " не найдено");
        }
        List<SalesHistoryPrivateResponseDto> entities = salesHistoryRepository.findSalesByUserOrderBySoldAt(username, page, size)
                .stream().map(mapper::toPrivateDto).toList();

        long totalElements = salesHistoryRepository.countSalesByUser(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(entities, totalElements, totalPages, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<SalesHistoryPublicResponseDto> getPublicSales(String username, int page, int size) {
        boolean exist = userRepository.existsByUsernameAndEnabledTrue(username);
        if (!exist) {
            throw new EntityNotFoundException("Пользователь с username " + username + " не найден или деактивирован");
        }
        List<SalesHistoryPublicResponseDto> entities = salesHistoryRepository.findSalesByUserOrderBySoldAt(username, page, size)
                .stream().map(mapper::toPublicDto).toList();

        long totalElements = salesHistoryRepository.countSalesByUser(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(entities, totalElements, totalPages, page, size);
    }


    @Transactional(readOnly = true)
    public PageResponse<SalesHistoryPrivateResponseDto> getPurchases(String username, int page, int size) {

        List<SalesHistoryPrivateResponseDto> entities = salesHistoryRepository.findPurchasesByUserOrderBySoldAt(username, page, size)
                .stream().map(mapper::toPrivateDto).toList();
        long totalElements = salesHistoryRepository.countPurchasesByUser(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(entities, totalElements, totalPages, page, size);
    }
}
