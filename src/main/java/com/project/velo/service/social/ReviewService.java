package com.project.velo.service.social;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.entity.Review;
import com.project.velo.entity.SalesHistory;
import com.project.velo.entity.User;
import com.project.velo.exception.ResourceAlreadyProcessedException;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.ReviewMapper;
import com.project.velo.repository.ReviewRepository;
import com.project.velo.repository.SalesHistoryRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper mapper;
    private final SalesHistoryRepository salesHistoryRepository;


    @Transactional
    public ReviewResponseDto leaveReview(Long adId, ReviewCreateDto dto, String username) {
        if (reviewRepository.existsByAdvertisementId(adId)) {
            throw new ResourceAlreadyProcessedException("Отзыв на это объявление уже оставлен");
        }

        SalesHistory sale = salesHistoryRepository.findByAdvertisementId(adId)
                .orElseThrow(() -> new EntityNotFoundException("Нельзя оставить отзыв: товар еще не продан или сделка не зафиксирована"));

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );


        if (!sale.getBuyer().getUsername().equals(username)) {
            throw new ValidationException("Только покупатель может оставить отзыв");
        }

        Review review = mapper.toEntity(dto);
        review.setAdvertisement(sale.getAdvertisement());
        review.setAuthor(user);
        review.setSeller(sale.getSeller());
        Review saved = reviewRepository.save(review);

        User seller = sale.getSeller();
        BigDecimal avgRating = reviewRepository.calculateAverageRating(seller.getId());
        seller.setRating(avgRating);
        userRepository.save(seller);

        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByUser(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Пользователя с username " + username + " не найдено");
        }
        List<Review> reviews = reviewRepository.getBySeller(username);
        return reviews.stream().map(mapper::toDto).toList();
    }
}
