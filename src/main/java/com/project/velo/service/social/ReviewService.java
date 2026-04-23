package com.project.velo.service.social;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.PageResponse;
import com.project.velo.dto.response.ReviewResponseDto;
import com.project.velo.entity.Review;
import com.project.velo.entity.SalesHistory;
import com.project.velo.entity.User;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.exception.ResourceAlreadyProcessedException;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.ReviewMapper;
import com.project.velo.repository.ReviewRepository;
import com.project.velo.repository.SalesHistoryRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public PageResponse<ReviewResponseDto> getReviewsByUser(String username, Integer rating, String sortDirection, int page, int size) {
        if (!userRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Пользователя с username " + username + " не найдено");
        }
        List<Review> reviews = reviewRepository.getBySellerWithPagination(username, rating, sortDirection, page, size);

        long totalElements = reviewRepository.countBySeller(username, rating);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<ReviewResponseDto> dtos = reviews.stream()
                .map(mapper::toDto)
                .toList();

        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }


    @Transactional(readOnly = true)
    public PageResponse<ReviewResponseDto> getAllReviews(int page, int size) {
        List<Review> reviews = reviewRepository.findAll(page, size);

        long totalElements = reviewRepository.countAll();

        List<ReviewResponseDto> dtos = reviews.stream()
                .map(mapper::toDto)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);


        return new PageResponse<>(
                dtos,
                totalElements,
                totalPages,
                page,
                size
        );
    }

    @Transactional
    public void deleteReview(Long id, String username) {
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Отзыва с id " + id + " не найдено")
        );
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );

        boolean isAuthor = review.getAuthor().getUsername().equals(username);
        boolean isAdmin = user.getRole().name().equals("ROLE_ADMIN");

        if (!isAuthor && !isAdmin) {
            log.warn("Security Alert! User {} tried to delete review {} without rights", username, id);
            throw new NotEnoughRightsException("У вас нет прав на удаление этого отзыва");
        }

        reviewRepository.delete(review);

        if (isAdmin) {
            log.info("ADMIN ACTION: Admin: {} deleted review: {} (Author: {}, Target Seller: {})",
                    username, id, review.getAuthor().getUsername(), review.getSeller().getUsername());
        } else {
            log.info("USER ACTION: Author: {} deleted their own review: {}", username, id);
        }

    }
}
