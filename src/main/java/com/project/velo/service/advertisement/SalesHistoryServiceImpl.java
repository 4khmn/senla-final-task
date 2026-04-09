package com.project.velo.service.advertisement;

import com.project.velo.dto.response.SalesHistoryResponseDto;
import com.project.velo.mapper.SalesHistoryMapper;
import com.project.velo.repository.SalesHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesHistoryServiceImpl implements SalesHistoryService {

    private final SalesHistoryRepository salesHistoryRepository;
    private final SalesHistoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<SalesHistoryResponseDto> getSales(String username) {
        return salesHistoryRepository.findAllBySellerUsernameOrderBySoldAt(username)
                .stream().map(mapper::toDto).toList();
    }
}
