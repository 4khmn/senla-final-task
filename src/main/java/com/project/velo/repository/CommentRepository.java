package com.project.velo.repository;

import com.project.velo.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository extends BaseRepository<Comment, Long> {
    protected CommentRepository() {
        super(Comment.class);
    }


    public List<Comment> getCommentsByAdvertisement(Long id) {
        return entityManager.createQuery(
                "SELECT c FROM Comment c WHERE c.advertisement.id = :id", Comment.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<Comment> getCommentsByUser(String username) {
        return entityManager.createQuery(
                "SELECT c FROM Comment c WHERE c.author.username = :username", Comment.class)
                .setParameter("username", username)
                .getResultList();
    }


}
