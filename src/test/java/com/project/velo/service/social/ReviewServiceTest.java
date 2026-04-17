package com.project.velo.service.social;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.PageResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper mapper;

    @Mock
    private SalesHistoryRepository salesHistoryRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void leaveReview_ShouldReturnPageResponse_WhenSuccess() {
        ReviewCreateDto request = new ReviewCreateDto(BigDecimal.ONE, "content");
        String username = "username";
        User buyer = new User();
        buyer.setUsername(username);

        User seller = new User();
        seller.setId(2L);
        SalesHistory sale = new SalesHistory();
        sale.setBuyer(buyer);
        sale.setSeller(seller);
        sale.setId(1L);

        Long adId = 1L;
        Review review = new Review();
        review.setContent(request.content());
        review.setScore(request.score());

        ReviewResponseDto dto = new ReviewResponseDto(1L,
                "title",
                1L,
                "username",
                BigDecimal.ONE,
                "content",
                LocalDateTime.now()
        );
        given(reviewRepository.existsByAdvertisementId(adId)).willReturn(false);
        given(salesHistoryRepository.findByAdvertisementId(adId)).willReturn(Optional.of(sale));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(buyer));
        given(mapper.toEntity(any())).willReturn(review);
        given(mapper.toDto(any())).willReturn(dto);
        given(reviewRepository.save(review)).willAnswer(i -> i.getArguments()[0]);
        given(reviewRepository.calculateAverageRating(2L)).willReturn(BigDecimal.ONE);
        given(userRepository.save(seller)).willAnswer(i -> i.getArguments()[0]);

        ReviewResponseDto result = reviewService.leaveReview(1L, request, username);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(mapper).toDto(any());
        verify(reviewRepository).calculateAverageRating(any());
        verify(reviewRepository).save(any());
        verify(userRepository).save(seller);
    }

    @Test
    void leaveReview_ShouldThrowResourceAlreadyProcessedException_WhenReviewAlreadyExist() {
        ReviewCreateDto request = new ReviewCreateDto(BigDecimal.ONE, "content");
        String username = "username";

        given(reviewRepository.existsByAdvertisementId(any())).willReturn(true);

        ResourceAlreadyProcessedException result = assertThrows(ResourceAlreadyProcessedException.class,
                () -> reviewService.leaveReview(1L, request, username));

        assertEquals("Отзыв на это объявление уже оставлен", result.getMessage());
        verifyNoInteractions(mapper);
        verifyNoInteractions(salesHistoryRepository);
        verifyNoInteractions(userRepository);
        verify(reviewRepository, never()).save(any());
        verify(reviewRepository, never()).calculateAverageRating(any());
    }

    @Test
    void leaveReview_ShouldThrowENFException_WhenSaleDoesNotExist() {
        ReviewCreateDto request = new ReviewCreateDto(BigDecimal.ONE, "content");
        String username = "username";
        Long adId = 1L;

        given(reviewRepository.existsByAdvertisementId(adId)).willReturn(false);
        given(salesHistoryRepository.findByAdvertisementId(adId)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> reviewService.leaveReview(1L, request, username));

        assertEquals("Нельзя оставить отзыв: товар еще не продан или сделка не зафиксирована", result.getMessage());

        verifyNoInteractions(mapper);
        verifyNoInteractions(userRepository);
        verify(reviewRepository, never()).save(any());
        verify(reviewRepository, never()).calculateAverageRating(any());
    }

    @Test
    void leaveReview_ShouldThrowENFException_WhenUserDoesNotExist() {
        ReviewCreateDto request = new ReviewCreateDto(BigDecimal.ONE, "content");
        String username = "username";
        SalesHistory sale = new SalesHistory();
        Long adId = 1L;

        given(reviewRepository.existsByAdvertisementId(adId)).willReturn(false);
        given(salesHistoryRepository.findByAdvertisementId(adId)).willReturn(Optional.of(sale));
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> reviewService.leaveReview(1L, request, username));
        assertEquals("Пользователя с username " + username + " не найдено",  result.getMessage());

        verifyNoInteractions(mapper);
        verify(reviewRepository, never()).save(any());
        verify(reviewRepository, never()).calculateAverageRating(any());
    }

    @Test
    void leaveReview_ShouldThrowValidationException_WhenUserIsNotBuyer() {
        ReviewCreateDto request = new ReviewCreateDto(BigDecimal.ONE, "content");
        String username = "username";
        User buyer = new User();
        buyer.setUsername(username);

        User seller = new User();
        seller.setId(2L);
        SalesHistory sale = new SalesHistory();
        User unknown = new User();
        unknown.setUsername("unknown");
        sale.setBuyer(unknown);

        Long adId = 1L;
        given(reviewRepository.existsByAdvertisementId(adId)).willReturn(false);
        given(salesHistoryRepository.findByAdvertisementId(adId)).willReturn(Optional.of(sale));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(buyer));

        ValidationException result = assertThrows(ValidationException.class,
                () -> reviewService.leaveReview(1L, request, username));

        assertEquals("Только покупатель может оставить отзыв", result.getMessage());
        verifyNoInteractions(mapper);
        verify(reviewRepository, never()).save(any());
        verify(reviewRepository, never()).calculateAverageRating(any());
    }

    @Test
    void getReviewsByUser_ShouldReturnPageResponse_WhenSuccess() {
        String username = "sellerUser";
        Integer rating = 5;
        String sort = "desc";
        int page = 0;
        int size = 10;

        Review review = new Review();
        review.setId(1L);
        review.setContent("Excellent!");

        ReviewResponseDto dto = new ReviewResponseDto(1L, "Ad", 1L, "Buyer", BigDecimal.valueOf(5), "Excellent!", LocalDateTime.now());

        given(userRepository.existsByUsername(username)).willReturn(true);
        given(reviewRepository.getBySellerWithPagination(username, rating, sort, page, size)).willReturn(List.of(review));
        given(reviewRepository.countBySeller(username, rating)).willReturn(25L);
        given(mapper.toDto(review)).willReturn(dto);

        PageResponse<ReviewResponseDto> result = reviewService.getReviewsByUser(username, rating, sort, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(25L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(userRepository).existsByUsername(username);
        verify(reviewRepository).getBySellerWithPagination(username, rating, sort, page, size);
        verify(reviewRepository).countBySeller(username, rating);
    }

    @Test
    void getReviewsByUser_ShouldThrowException_WhenUserNotFound() {
        String username = "nonExistent";
        given(userRepository.existsByUsername(username)).willReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reviewService.getReviewsByUser(username, 5, "asc", 0, 10));

        assertEquals("Пользователя с username " + username + " не найдено", exception.getMessage());

        verifyNoInteractions(reviewRepository);
        verifyNoInteractions(mapper);
    }
}
