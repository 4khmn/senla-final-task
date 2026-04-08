package com.project.velo.repository;

import com.project.velo.entity.Review;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ReviewRepository extends BaseRepository<Review, Long> {

    protected ReviewRepository() {
        super(Review.class);
    }

    public boolean existsByAdvertisementId(Long adId) {
        Long count = entityManager.createQuery("SELECT COUNT(r) FROM Review r WHERE r.advertisement.id = :id", Long.class)
                .setParameter("id", adId)
                .getSingleResult();
        return count > 0;

    }

    public BigDecimal calculateAverageRating(Long sellerId) {
        Double avg = entityManager.createQuery("SELECT AVG(r.score) FROM Review r WHERE r.seller.id = :id", Double.class)
                .setParameter("id", sellerId)
                .getSingleResult();
        return avg != null ? BigDecimal.valueOf(avg) : BigDecimal.ZERO;
    }

    public List<Review> getBySeller(String username) {
        return entityManager.createQuery(
                "SELECT r FROM Review r WHERE r.seller.username = :username", Review.class)
                .setParameter("username", username)
                .getResultList();
    }
}
