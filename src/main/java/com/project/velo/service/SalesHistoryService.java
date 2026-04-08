package com.project.velo.service;

import com.project.velo.dto.response.SalesHistoryResponseDto;

import java.util.List;

public interface SalesHistoryService {

    List<SalesHistoryResponseDto> getSales(String username);
}
