package com.project.velo.service.social;

import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.comment.CommentDetailsResponseDto;
import com.project.velo.dto.response.common.PageResponse;
import com.project.velo.dto.response.profile.UserCommentResponseDto;
import com.project.velo.dto.update.CommentUpdateDto;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Comment;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.AdStatus;
import com.project.velo.exception.AdvertisementNotAvailableException;
import com.project.velo.exception.NotEnoughRightsException;
import com.project.velo.exception.ValidationException;
import com.project.velo.mapper.CommentMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.CommentRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CommentMapper mapper;

    @Transactional
    public CommentDetailsResponseDto postComment(Long adId, CommentCreateDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );
        Advertisement advertisement = advertisementRepository.findById(adId).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + adId + " не найдено")
        );
        if (advertisement.getStatus() != AdStatus.ACTIVE) {
            throw new AdvertisementNotAvailableException("Объявление с id " + adId + " не доступно");
        }
        Comment comment = mapper.toEntity(dto);
        advertisement.addComment(comment);
        comment.setAuthor(user);
        commentRepository.save(comment);
        return mapper.toDetailsDto(comment);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentDetailsResponseDto> getCommentsByAdvertisement(Long id, int page, int size) {
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + id + " не найдено")
        );
        if (advertisement.getStatus() != AdStatus.ACTIVE) {
            throw new AdvertisementNotAvailableException("Объявление с id " + id + " не доступно");
        }
        List<Comment> comments = commentRepository.getCommentsByAdvertisementWithPagination(id, page, size);
        long totalElements = commentRepository.countByAdvertisementId(id);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<CommentDetailsResponseDto> dtos = comments.stream()
                .map(mapper::toDetailsDto)
                .toList();

        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }

    @Transactional
    public void delete(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментария с id " + commentId + " не найдено")
        );
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с username " + username + " не найдено")
        );

        if (comment.getAdvertisement().getStatus() != AdStatus.ACTIVE) {
            throw new AdvertisementNotAvailableException("Объявление с id " + comment.getAdvertisement().getId() + " не доступно");
        }

        boolean isAuthor = comment.getAuthor().getUsername().equals(username);
        boolean isAdmin = user.getRole().name().equals("ROLE_ADMIN");
        if (isAuthor || isAdmin) {
            commentRepository.delete(comment);
            log.info("Comment: {} deleted by {}", commentId, isAdmin ? "ADMIN" : "AUTHOR");
        } else {
            throw new NotEnoughRightsException("Недостаточно прав для удаления чужого комментария");
        }
    }


    @Transactional(readOnly = true)
    public PageResponse<UserCommentResponseDto> getCommentsByUser(String username, int page, int size) {
        if (!userRepository.existsByUsername(username)) {
            throw new EntityNotFoundException("Пользователя с username " + username + " не найдено");
        }

        List<Comment> comments = commentRepository.getCommentsByUserWithPagination(username, page, size);
        long totalElements = commentRepository.countByAuthor(username);

        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<UserCommentResponseDto> dtos = comments.stream().map(mapper::toShortDto).toList();

        return new PageResponse<>(dtos, totalElements, totalPages, page, size);
    }

    @Transactional
    public CommentDetailsResponseDto update(Long commentId, CommentUpdateDto dto, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментария с id " + commentId + " не найдено")
        );
        if (comment.getAdvertisement().getStatus() != AdStatus.ACTIVE) {
            throw new AdvertisementNotAvailableException("Объявление с id " + comment.getAdvertisement().getId() + " не доступно");
        }

        if (!comment.getAuthor().getUsername().equals(username)) {

            throw new NotEnoughRightsException("Недостаточно прав для этого действия: Вы не можете удалять чужие комментарии");
        }
        comment.setContent(dto.content());
        return mapper.toDetailsDto(comment);
    }

    @Transactional
    public void togglePin(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        if (!comment.getAdvertisement().getSeller().getUsername().equals(username)) {
            throw new ValidationException("Вы не можете закрепить этот комментарий");
        }

        comment.setPinned(!comment.isPinned());
    }
}
