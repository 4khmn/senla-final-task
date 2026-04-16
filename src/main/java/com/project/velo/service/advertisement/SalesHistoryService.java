package com.project.velo.service.advertisement;

import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.SalesHistoryResponseDto;
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
    public PageResponse<SalesHistoryResponseDto> getSales(String username, int page, int size) {
        if (!userRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Пользователя с username " + username + " не найдено");
        }
        List<SalesHistoryResponseDto> entities = salesHistoryRepository.findAllBySellerOrderBySoldAt(username, page, size)
                .stream().map(mapper::toDto).toList();

        long totalElements = salesHistoryRepository.countSalesBySeller(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(entities, totalElements, totalPages, page, size);
    }
}
