package com.project.velo.service;

import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.CommentDetailsResponseDto;
import com.project.velo.dto.response.UserCommentResponseDto;
import com.project.velo.dto.update.CommentUpdateDto;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.Comment;
import com.project.velo.entity.User;
import com.project.velo.exception.NotEnoughRights;
import com.project.velo.mapper.CommentMapper;
import com.project.velo.repository.AdvertisementRepository;
import com.project.velo.repository.CommentRepository;
import com.project.velo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final CommentMapper mapper;

    @Override
    @Transactional
    public CommentDetailsResponseDto postComment(Long adId, CommentCreateDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("Пользователя с таким username е найдено.")
        );
        Advertisement advertisement = advertisementRepository.findById(adId).orElseThrow(
                () -> new EntityNotFoundException("Объявления с id " + adId + " не найдено.")
        );
        Comment comment = mapper.toEntity(dto);
        advertisement.addComment(comment);
        comment.setAuthor(user);
        commentRepository.save(comment);
        return mapper.toDetailsDto(comment);
    }

    @Override
    public List<CommentDetailsResponseDto> getCommentsByAdvertisement(Long id) {
        List<Comment> comments = commentRepository.getCommentsByAdvertisement(id);
        return comments.stream().map(mapper::toDetailsDto).toList();
    }

    @Override
    @Transactional
    public void delete(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментария с id " + commentId + " не найдено.")
        );
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new NotEnoughRights("Недостаточно прав для этого действия: Вы не можете удалять чужие комментарии.");
        }
        commentRepository.delete(comment);
    }

    @Override
    public List<UserCommentResponseDto> getCommentsByUser(String username) {
        List<Comment> commentsByUser = commentRepository.getCommentsByUser(username);
        return commentsByUser.stream().map(mapper::toShortDto).toList();
    }

    @Override
    @Transactional
    public CommentDetailsResponseDto update(Long commentId, CommentUpdateDto dto, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Комментария с id " + commentId + " не найдено.")
        );
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new NotEnoughRights("Недостаточно прав для этого действия: Вы не можете удалять чужие комментарии.");
        }
        comment.setContent(dto.content());
        return mapper.toDetailsDto(comment);
    }


}
