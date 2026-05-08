package com.project.velo.service.advertisement;

import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.salesHistory.SalesHistoryPrivateResponseDto;
import com.project.velo.dto.response.salesHistory.SalesHistoryPublicResponseDto;
import com.project.velo.entity.User;
import com.project.velo.exception.UserDisabledException;
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
        List<SalesHistoryPrivateResponseDto> entities = salesHistoryRepository.findAllBySellerOrderBySoldAt(username, page, size)
                .stream().map(mapper::toPrivateDto).toList();

        long totalElements = salesHistoryRepository.countSalesBySeller(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(entities, totalElements, totalPages, page, size);
    }

    @Transactional(readOnly = true)
    public PageResponse<SalesHistoryPublicResponseDto> getPublicSales(String username, int page, int size) {
        User user =  userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        if (!user.isEnabled()) {
            throw new UserDisabledException("Пользователь с username " + username + " деактивирован");
        }
        List<SalesHistoryPublicResponseDto> entities = salesHistoryRepository.findAllBySellerOrderBySoldAt(username, page, size)
                .stream().map(mapper::toPublicDto).toList();

        long totalElements = salesHistoryRepository.countSalesBySeller(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(entities, totalElements, totalPages, page, size);
    }
}
