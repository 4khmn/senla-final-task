package com.project.velo.service.social;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.review.ReviewFullResponseDto;
import com.project.velo.dto.response.review.ReviewReceivedResponseDto;
import com.project.velo.dto.response.review.ReviewSentResponseDto;
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
    public ReviewReceivedResponseDto leaveReview(Long adId, ReviewCreateDto dto, String username) {
        SalesHistory sale = salesHistoryRepository.findByAdvertisementId(adId)
                .orElseThrow(() -> new EntityNotFoundException("Нельзя оставить отзыв: товар не найден или еще не продан"));
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );

        if (!sale.getBuyer().getUsername().equals(username)) {
            throw new ValidationException("Только покупатель может оставить отзыв");
        }

        if (!sale.getSeller().isEnabled()) {
            throw new EntityNotFoundException("Объявление с id " + adId + " не найдено или недоступно");
        }
        if (reviewRepository.existsByAdvertisementId(adId)) {
            throw new ResourceAlreadyProcessedException("Отзыв на это объявление уже оставлен");
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

        return mapper.toReceivedDto(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewReceivedResponseDto> getReceivedByUser(String username, Integer rating, String sortDirection, int page, int size) {
        boolean exist = userRepository.existsByUsernameAndEnabledTrue(username);
        if (!exist) {
            throw new EntityNotFoundException("Пользователь с username " + username + " не найден или деактивирован");
        }
        List<Review> reviews = reviewRepository.getBySellerWithPagination(username, rating, sortDirection, page, size);

        long totalElements = reviewRepository.countBySeller(username, rating);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<ReviewReceivedResponseDto> dtos = reviews.stream()
                .map(mapper::toReceivedDto)
                .toList();

        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }

    public PageResponse<ReviewSentResponseDto> getSentByUser(String username, int page, int size) {
        boolean exist = userRepository.existsByUsernameAndEnabledTrue(username);
        if (!exist) {
            throw new EntityNotFoundException("Пользователь с username " + username + " не найден или деактивирован");
        }
        List<Review> reviews = reviewRepository.getByAuthorWithPagination(username, page, size);
        long totalElements = reviewRepository.countByAuthor(username);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<ReviewSentResponseDto> dtos = reviews.stream()
                .map(mapper::toSentDto)
                .toList();

        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }


    @Transactional(readOnly = true)
    public PageResponse<ReviewFullResponseDto> getAllReviews(int page, int size) {
        List<Review> reviews = reviewRepository.findAll(page, size);

        long totalElements = reviewRepository.countAll();

        List<ReviewFullResponseDto> dtos = reviews.stream()
                .map(mapper::toFullDto)
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
        if (!review.getAuthor().isEnabled()) {
            throw new EntityNotFoundException("Объявление, на которое был оставлен этот отзыв, больше недоступно");
        }

        boolean isAuthor = review.getAuthor().getUsername().equals(username);
        boolean isAdmin = user.getRole().name().equals("ROLE_ADMIN");

        if (!isAuthor && !isAdmin) {
            log.warn("User {} tried to delete review {} without rights", username, id);
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
