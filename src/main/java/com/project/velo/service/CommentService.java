package com.project.velo.service;

import com.project.velo.dto.create.CommentCreateDto;
import com.project.velo.dto.response.CommentDetailsResponseDto;
import com.project.velo.dto.response.UserCommentResponseDto;
import com.project.velo.dto.update.CommentUpdateDto;

import java.util.List;

public interface CommentService {

    CommentDetailsResponseDto postComment(Long adId, CommentCreateDto dto, String username);

    List<CommentDetailsResponseDto> getCommentsByAdvertisement(Long adId);


    void delete(Long commentId, String username);

    List<UserCommentResponseDto> getCommentsByUser(String username);


    CommentDetailsResponseDto update(Long commentId, CommentUpdateDto dto, String username);
}
