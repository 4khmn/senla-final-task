package com.project.velo.service.social;

import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.profile.AuthorResponseDto;
import com.project.velo.dto.response.comment.CommentDetailsResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.profile.UserCommentResponseDto;
import com.project.velo.dto.update.CommentUpdateDto;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Comment;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.AdStatus;
import com.project.velo.entity.enums.Role;
import com.project.velo.exception.AdvertisementNotAvailableException;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.CommentMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.CommentRepository;
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
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private CommentMapper mapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void postComment_ShouldReturnDto_Success() {
        String username = "username";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        Advertisement advertisement = new Advertisement();
        advertisement.setStatus(AdStatus.ACTIVE);
        advertisement.setId(1L);
        CommentCreateDto commentCreateDto = new CommentCreateDto("content");
        CommentDetailsResponseDto responseDto = new CommentDetailsResponseDto(
                1L,
                "content",
                LocalDateTime.now(),
                new AuthorResponseDto(1L, user.getUsername(), BigDecimal.ONE, "avatar"));
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setAdvertisement(advertisement);
        comment.setContent(commentCreateDto.content());
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(advertisementRepository.findById(1L)).willReturn(Optional.of(advertisement));
        given(mapper.toEntity(any(CommentCreateDto.class))).willReturn(comment);
        given(commentRepository.save(any(Comment.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(mapper.toDetailsDto(comment)).willReturn(responseDto);

        CommentDetailsResponseDto result = commentService.postComment(1L, commentCreateDto, "username");

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(commentRepository).save(any(Comment.class));
        verify(mapper).toEntity(any(CommentCreateDto.class));
        verify(mapper).toDetailsDto(any(Comment.class));

    }

    @Test
    void postComment_ShouldThrowENFException_WhenAdvertisementNotFound() {
        String username = "username";
        User user = new User();
        CommentCreateDto dto = new CommentCreateDto("content");

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(advertisementRepository.findById(1L)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class, () ->
                commentService.postComment(1L, dto, username)
        );

        assertEquals("Объявления с id 1 не найдено", result.getMessage());
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void postComment_ShouldThrowAdvertisementNotAvailableException_WhenAdvertisementNotActive() {
        String username = "username";
        User user = new User();

        Advertisement ad = new Advertisement();
        ad.setStatus(AdStatus.ARCHIVED);

        CommentCreateDto dto = new CommentCreateDto("content");

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(advertisementRepository.findById(1L)).willReturn(Optional.of(ad));

        AdvertisementNotAvailableException result = assertThrows(AdvertisementNotAvailableException.class, () ->
                commentService.postComment(1L, dto, username)
        );

        assertEquals("Объявление с id 1 не доступно", result.getMessage());
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void postComment_ShouldThrowException_WhenUserNotFound() {
        String username = "unknown";
        CommentCreateDto dto = new CommentCreateDto("content");

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(EntityNotFoundException.class, () ->
                commentService.postComment(1L, dto, username)
        );

        assertEquals("Пользователя с username " + username + " не найдено", result.getMessage());
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void getCommentsByAdvertisement_ShouldReturnPageResponse_Success() {
        Long adId = 1L;
        int page = 0;
        int size = 5;

        Advertisement ad = new Advertisement();
        ad.setId(adId);
        ad.setStatus(AdStatus.ACTIVE);

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setContent("content");

        List<Comment> comments = List.of(comment);
        long totalElements = 12L;

        CommentDetailsResponseDto dto = new CommentDetailsResponseDto(
                10L, "content", LocalDateTime.now(), null);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));
        given(commentRepository.getCommentsByAdvertisementWithPagination(adId, page, size)).willReturn(comments);
        given(commentRepository.countByAdvertisementId(adId)).willReturn(totalElements);
        given(mapper.toDetailsDto(comment)).willReturn(dto);

        PageResponse<CommentDetailsResponseDto> result = commentService.getCommentsByAdvertisement(adId, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(totalElements, result.totalElements());
        assertEquals(3, result.totalPages());
        assertEquals(page, result.page());
        assertEquals(size, result.size());

        verify(commentRepository).getCommentsByAdvertisementWithPagination(adId, page, size);
        verify(commentRepository).countByAdvertisementId(adId);
    }

    @Test
    void getCommentsByAdvertisement_ShouldThrowENFException_WhenAdvertisementDoesNotExist() {
        Long adId = 99L;
        given(advertisementRepository.findById(adId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                commentService.getCommentsByAdvertisement(adId, 0, 10)
        );
        verifyNoInteractions(commentRepository);
    }

    @Test
    void getCommentsByAdvertisement_ShouldThrowAdvertisementNotAvailableException_WhenAdvertisementNotActive() {
        Long adId = 1L;
        Advertisement ad = new Advertisement();
        ad.setId(adId);
        ad.setStatus(AdStatus.ARCHIVED);

        given(advertisementRepository.findById(adId)).willReturn(Optional.of(ad));

        assertThrows(AdvertisementNotAvailableException.class, () ->
                commentService.getCommentsByAdvertisement(adId, 0, 10)
        );

        verifyNoInteractions(commentRepository);
    }

    @Test
    void delete_Author_Success() {
        Long commentId = 1L;
        String username = "authorUser";

        User author = new User();
        author.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdStatus.ACTIVE);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(author);
        comment.setAdvertisement(ad);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(author));
        commentService.delete(commentId, username);

        verify(commentRepository).delete(comment);
    }

    @Test
    void delete_Admin_Success() {
        Long commentId = 1L;
        String username = "adminUser";

        User author = new User();
        author.setUsername("authorUser");

        Advertisement ad = new Advertisement();
        ad.setStatus(AdStatus.ACTIVE);

        User admin = new User();
        admin.setUsername(username);
        admin.setRole(Role.ROLE_ADMIN);
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(author);
        comment.setAdvertisement(ad);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(admin));
        commentService.delete(commentId, username);

        verify(commentRepository).delete(comment);
    }

    @Test
    void delete_ShouldThrowNotEnoughRightsException_WhenUserIsNotAuthor() {
        Long commentId = 1L;
        String currentUsername = "hackerUser";
        String authorUsername = "actualAuthor";

        User author = new User();
        author.setUsername(authorUsername);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdStatus.ACTIVE);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setAdvertisement(ad);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userRepository.findByUsername(currentUsername)).willReturn(Optional.of(author));

        assertThrows(NotEnoughRightsException.class, () ->
                commentService.delete(commentId, currentUsername)
        );

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void delete_ShouldThrowAdvertisementNotAvailableException_WhenAdvertisementDoesNotActive() {
        Long commentId = 1L;
        String username = "authorUser";

        User author = new User();
        author.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdStatus.ARCHIVED);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setAdvertisement(ad);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(userRepository.findByUsername(username)).willReturn(Optional.of(author));


        assertThrows(AdvertisementNotAvailableException.class, () ->
                commentService.delete(commentId, username)
        );

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void delete_ShouldThrowENFException_WhenCommentDoesNotExist() {
        Long commentId = 999L;
        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                commentService.delete(commentId, "anyUser")
        );

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void delete_ShouldThrowENFException_WhenUserDoesNotExist() {
        Comment comment = new Comment();
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(userRepository.findByUsername("anyUser")).willReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                commentService.delete(1L, "anyUser")
        );

        verify(commentRepository, never()).delete(any());
    }


    @Test
    void getCommentsByUser_ShouldReturnPageResponse_Success() {
        String username = "activeUser";
        int page = 0;
        int size = 10;

        Comment comment = new Comment();
        comment.setId(50L);
        comment.setContent("Хороший велосипед!");

        List<Comment> comments = List.of(comment);
        long totalElements = 1L;

        UserCommentResponseDto dto = new UserCommentResponseDto(
                50L, "Хороший велосипед!", LocalDateTime.now(), 1L, "Название объявления");

        given(userRepository.existsByUsername(username)).willReturn(true);
        given(commentRepository.getCommentsByUserWithPagination(username, page, size)).willReturn(comments);
        given(commentRepository.countByAuthor(username)).willReturn(totalElements);
        given(mapper.toShortDto(comment)).willReturn(dto);

        PageResponse<UserCommentResponseDto> result = commentService.getCommentsByUser(username, page, size);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(totalElements, result.totalElements());
        assertEquals(1, result.totalPages());
        assertEquals(dto, result.content().get(0));

        verify(userRepository).existsByUsername(username);
        verify(commentRepository).getCommentsByUserWithPagination(username, page, size);
        verify(commentRepository).countByAuthor(username);
    }

    @Test
    void getCommentsByUser_ShouldThrowENFException_WhenUserDoesNotExist() {
        String username = "unknown";
        given(userRepository.existsByUsername(username)).willReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                commentService.getCommentsByUser(username, 0, 10)
        );

        verifyNoInteractions(commentRepository);
        verifyNoInteractions(mapper);
    }


    @Test
    void update_ShouldReturnUpdatedDto_Success() {
        Long commentId = 1L;
        String username = "authorUser";
        String newContent = "new content";

        User author = new User();
        author.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdStatus.ACTIVE);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(author);
        comment.setAdvertisement(ad);
        comment.setContent("old content");

        CommentUpdateDto updateDto = new CommentUpdateDto(newContent);
        CommentDetailsResponseDto expectedDto = new CommentDetailsResponseDto(
                commentId, newContent, LocalDateTime.now(), null);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(mapper.toDetailsDto(comment)).willReturn(expectedDto);

        CommentDetailsResponseDto result = commentService.update(commentId, updateDto, username);

        assertNotNull(result);
        assertEquals(newContent, comment.getContent());
        assertEquals(expectedDto, result);

        verify(mapper).toDetailsDto(comment);
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNotAuthor() {
        Long commentId = 1L;
        String currentUsername = "stranger";

        User author = new User();
        author.setUsername("realAuthor");

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setAdvertisement(new Advertisement());
        comment.getAdvertisement().setStatus(AdStatus.ACTIVE);

        CommentUpdateDto updateDto = new CommentUpdateDto("New text");

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        assertThrows(NotEnoughRightsException.class, () ->
                commentService.update(commentId, updateDto, currentUsername)
        );
    }

    @Test
    void update_ShouldThrowException_WhenAdvertisementNotActive() {
        Long commentId = 1L;
        String username = "authorUser";

        User author = new User();
        author.setUsername(username);

        Advertisement ad = new Advertisement();
        ad.setStatus(AdStatus.ARCHIVED);

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setAdvertisement(ad);

        CommentUpdateDto updateDto = new CommentUpdateDto("New text");

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        assertThrows(AdvertisementNotAvailableException.class, () ->
                commentService.update(commentId, updateDto, username)
        );
    }

    @Test
    void update_ShouldThrowException_WhenCommentNotFound() {
        Long commentId = 99L;
        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                commentService.update(commentId, new CommentUpdateDto("text"), "user")
        );
    }

    @Test
    void togglePin_ShouldPinComment_WhenUnpinnedAndSeller() {
        User seller = new User();
        seller.setUsername("seller");
        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);
        Long commentId = 99L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAdvertisement(advertisement);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));


        commentService.togglePin(commentId, "seller");

        assertTrue(comment.isPinned());
        verify(commentRepository).findById(commentId);
    }


    @Test
    void togglePin_ShouldThrowValidationException_WhenNotSeller() {
        User seller = new User();
        seller.setUsername("seller");
        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);
        Long commentId = 99L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAdvertisement(advertisement);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));


        assertThrows(ValidationException.class,
                () -> commentService.togglePin(commentId, "random_kid"));

        verify(commentRepository).findById(commentId);
    }

}
