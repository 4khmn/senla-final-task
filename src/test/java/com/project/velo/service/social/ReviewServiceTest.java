package com.project.velo.service.social;

import com.project.velo.dto.create.ReviewCreateDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.review.ReviewFullResponseDto;
import com.project.velo.dto.response.review.ReviewReceivedResponseDto;
import com.project.velo.dto.response.review.ReviewSentResponseDto;
import com.project.velo.entity.Review;
import com.project.velo.entity.SalesHistory;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.Role;
import com.project.velo.exception.NotEnoughRightsException;
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
    void leaveReview_ShouldReturnPageResponse_Success() {
        ReviewCreateDto request = new ReviewCreateDto(1, "content");
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

        ReviewReceivedResponseDto dto = new ReviewReceivedResponseDto(1L,
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
        given(mapper.toReceivedDto(any())).willReturn(dto);
        given(reviewRepository.save(review)).willAnswer(i -> i.getArguments()[0]);
        given(reviewRepository.calculateAverageRating(2L)).willReturn(BigDecimal.ONE);
        given(userRepository.save(seller)).willAnswer(i -> i.getArguments()[0]);

        ReviewReceivedResponseDto result = reviewService.leaveReview(1L, request, username);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(mapper).toReceivedDto(any());
        verify(reviewRepository).calculateAverageRating(any());
        verify(reviewRepository).save(any());
        verify(userRepository).save(seller);
    }

    @Test
    void leaveReview_ShouldThrowResourceAlreadyProcessedException_WhenReviewAlreadyExist() {
        ReviewCreateDto request = new ReviewCreateDto(1, "content");
        String username = "username";
        User buyer = new User();
        buyer.setUsername(username);
        User seller = new User();
        seller.setEnabled(true);
        SalesHistory sale = new SalesHistory();
        sale.setSeller(seller);
        sale.setBuyer(buyer);
        given(userRepository.findByUsername(username)).willReturn(Optional.of(buyer));
        given(reviewRepository.existsByAdvertisementId(any())).willReturn(true);
        given(salesHistoryRepository.findByAdvertisementId(any())).willReturn(Optional.of(sale));

        ResourceAlreadyProcessedException result = assertThrows(ResourceAlreadyProcessedException.class,
                () -> reviewService.leaveReview(1L, request, username));

        assertEquals("Отзыв на это объявление уже оставлен", result.getMessage());
        verifyNoInteractions(mapper);
        verify(reviewRepository, never()).save(any());
        verify(reviewRepository, never()).calculateAverageRating(any());
    }

    @Test
    void leaveReview_ShouldThrowENFException_WhenSaleDoesNotExist() {
        ReviewCreateDto request = new ReviewCreateDto(1, "content");
        String username = "username";
        Long adId = 1L;

        given(salesHistoryRepository.findByAdvertisementId(adId)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class,
                () -> reviewService.leaveReview(1L, request, username));

        assertEquals("Нельзя оставить отзыв: товар не найден или еще не продан", result.getMessage());

        verifyNoInteractions(mapper);
        verifyNoInteractions(userRepository);
        verify(reviewRepository, never()).save(any());
        verify(reviewRepository, never()).calculateAverageRating(any());
    }

    @Test
    void leaveReview_ShouldThrowENFException_WhenUserDoesNotExist() {
        ReviewCreateDto request = new ReviewCreateDto(1, "content");
        String username = "username";
        SalesHistory sale = new SalesHistory();
        Long adId = 1L;

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
        ReviewCreateDto request = new ReviewCreateDto(1, "content");
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
    void getReceivedByUser_ShouldReturnPageResponse_Success() {
        String username = "sellerUser";
        User user = new User();
        user.setUsername(username);
        user.setEnabled(true);
        Integer rating = 5;
        String sort = "desc";
        int page = 0;
        int size = 10;

        Review review = new Review();
        review.setId(1L);
        review.setContent("Excellent!");

        ReviewReceivedResponseDto dto = new ReviewReceivedResponseDto(1L, "Ad", 1L, "Buyer", BigDecimal.valueOf(5), "Excellent!", LocalDateTime.now());

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(reviewRepository.getBySellerWithPagination(username, rating, sort, page, size)).willReturn(List.of(review));
        given(reviewRepository.countBySeller(username, rating)).willReturn(25L);
        given(mapper.toReceivedDto(review)).willReturn(dto);

        PageResponse<ReviewReceivedResponseDto> result = reviewService.getReceivedByUser(username, rating, sort, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(25L, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(userRepository).findByUsername(username);
        verify(reviewRepository).getBySellerWithPagination(username, rating, sort, page, size);
        verify(reviewRepository).countBySeller(username, rating);
    }

    @Test
    void getReceivedByUser_ShouldThrowException_WhenUserNotFound() {
        String username = "nonExistent";

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reviewService.getReceivedByUser(username, 5, "asc", 0, 10));

        assertEquals("Пользователя с username " + username + " не найдено", exception.getMessage());

        verifyNoInteractions(reviewRepository);
        verifyNoInteractions(mapper);
    }


    @Test
    void deleteReview_Author_Success() {
        String username = "authorUsername";
        Review review = new Review();
        review.setId(1L);
        User admin = new User();
        admin.setUsername(username);

        User author = new User();
        author.setUsername("authorUsername");

        User seller = new User();
        seller.setUsername("sellerUsername");
        review.setAuthor(author);
        review.setSeller(seller);
        admin.setRole(Role.ROLE_ADMIN);

        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(admin));

        reviewService.deleteReview(1L,  username);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_Admin_Success() {
        String adminUsername = "adminUsername";
        Review review = new Review();
        review.setId(1L);
        User seller = new User();
        seller.setUsername("seller");
        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setRole(Role.ROLE_ADMIN);
        User author = new User();
        author.setUsername("authorUsername");
        review.setAuthor(author);
        review.setSeller(seller);

        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(userRepository.findByUsername(adminUsername)).willReturn(Optional.of(admin));

        reviewService.deleteReview(1L,  adminUsername);
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_ShouldThrowENFException_WhenReviewDoesNotExist() {
        given(reviewRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> reviewService.deleteReview(1L,  "nonExistent"));

        verifyNoInteractions(userRepository);
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void deleteReview_ShouldThrowENFException_WhenUserDoesNotExist() {
        Review review = new Review();
        review.setId(1L);
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(userRepository.findByUsername("username")).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> reviewService.deleteReview(1L,  "username"));

        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void deleteReview_ShouldThrowNotEnoughRightsException_WhenNotAdminOrAdmin() {
        String username = "randomUser";
        Review review = new Review();
        review.setId(1L);
        User randomUser = new User();
        randomUser.setUsername(username);
        User author = new User();
        author.setUsername("authorUsername");
        review.setAuthor(author);

        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(randomUser));

        assertThrows(NotEnoughRightsException.class,
                () -> reviewService.deleteReview(1L,  username));
        verify(reviewRepository, never()).delete(review);
    }

    @Test
    void getAllReviews_ShouldReturnPageResponse_Success() {
        int page = 0;
        int size = 5;
        List<Review> reviews = List.of(new Review(), new Review());

        when(reviewRepository.findAll(page, size)).thenReturn(reviews);
        when(reviewRepository.countAll()).thenReturn(10L);
        when(mapper.toReceivedDto(any(Review.class))).thenReturn(new ReviewReceivedResponseDto(
                1L,
                "title",
                1L,
                "username",
                new BigDecimal("5"),
                "content",
                LocalDateTime.now())
        );

        PageResponse<ReviewFullResponseDto> result = reviewService.getAllReviews(page, size);

        assertEquals(10L, result.totalElements());
        assertEquals(2, result.totalPages());
        assertEquals(2, result.content().size());
        assertEquals(page, result.page());
        assertEquals(size, result.size());

        verify(reviewRepository).findAll(page, size);
        verify(reviewRepository).countAll();
        verify(mapper, times(2)).toReceivedDto(any());
    }

    @Test
    void getAllReviews_ShouldReturnEmptyPage_WhenNoReviewsExist() {
        int page = 0;
        int size = 10;

        when(reviewRepository.findAll(page, size)).thenReturn(List.of());
        when(reviewRepository.countAll()).thenReturn(0L);

        PageResponse<ReviewFullResponseDto> result = reviewService.getAllReviews(page, size);

        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    @Test
    void getSentByUser_ShouldReturnPageResponse_Success() {
        User user = User.builder().id(1L).username("username").enabled(true).build();

        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(reviewRepository.getByAuthorWithPagination(user.getUsername(), 0, 10)).willReturn(List.of(new Review()));
        when(reviewRepository.countByAuthor(user.getUsername())).thenReturn(1L);
        when(mapper.toReceivedDto(any(Review.class))).thenReturn(new ReviewReceivedResponseDto(
                1L,
                "title",
                1L,
                "username",
                new BigDecimal("5"),
                "content",
                LocalDateTime.now())
        );

        PageResponse<ReviewSentResponseDto> result = reviewService.getSentByUser("username", 0, 10);

        assertEquals("title", result.content().get(0).advertisementTitle());

        verify(mapper, times(1)).toReceivedDto(any(Review.class));
        verify(reviewRepository).getByAuthorWithPagination(anyString(), anyInt(), anyInt());
        verify(reviewRepository).countByAuthor(anyString());
    }
}
