package com.project.velo.service;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.ReviewResponseDto;

import java.util.List;

public interface ReviewService {

    ReviewResponseDto leaveReview(Long adId, ReviewCreateDto dto, String username);

    List<ReviewResponseDto> getReviewsByUser(String username);
}
