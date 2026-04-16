package com.project.velo.repository;

import com.project.velo.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository extends BaseRepository<Comment, Long> {
    protected CommentRepository() {
        super(Comment.class);
    }


    public List<Comment> getCommentsByUserWithPagination(String username, int page, int size) {
        return entityManager.createQuery(
                        "SELECT c FROM Comment c WHERE c.author.username = :username ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("username", username)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countByAuthor(String username) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM Comment c WHERE c.author.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }


    public List<Comment> getCommentsByAdvertisementWithPagination(Long adId, int page, int size) {
        return entityManager.createQuery(
                        "SELECT c FROM Comment c WHERE c.advertisement.id = :adId ORDER BY c.createdAt DESC", Comment.class)
                .setParameter("adId", adId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countByAdvertisementId(Long adId) {
        return entityManager.createQuery(
                        "SELECT COUNT(c) FROM Comment c WHERE c.advertisement.id = :adId", Long.class)
                .setParameter("adId", adId)
                .getSingleResult();
    }


}
