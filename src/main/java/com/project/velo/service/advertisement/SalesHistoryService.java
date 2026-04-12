package com.project.velo.service.advertisement;

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
    public List<SalesHistoryResponseDto> getSales(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Пользователя с username " + username + " не найдено");
        }
        return salesHistoryRepository.findAllBySellerUsernameOrderBySoldAt(username)
                .stream().map(mapper::toDto).toList();
    }
}
