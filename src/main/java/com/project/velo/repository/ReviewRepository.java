package com.project.velo.repository;

import com.project.velo.entity.Review;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public List<Review> geyBySellerWithPagination(String sellerUsername, Integer rating, String sortDirection, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Review> cq = cb.createQuery(Review.class);
        Root<Review> root = cq.from(Review.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("seller").get("username"), sellerUsername));

        if (rating != null) {
            predicates.add(cb.equal(root.get("rating"), rating));
        }

        List<Order> orders = new ArrayList<>();

        if (sortDirection != null && !sortDirection.isBlank()) {
            if ("asc".equalsIgnoreCase(sortDirection)) {
                orders.add(cb.asc(root.get("rating")));
            } else if ("desc".equalsIgnoreCase(sortDirection)) {
                orders.add(cb.desc(root.get("rating")));
            }
        }
        orders.add(cb.desc(root.get("createdAt")));

        cq.orderBy(orders);

        cq.orderBy(orders);
        return entityManager.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countBySeller(String sellerUsername, Integer rating) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Review> root = cq.from(Review.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("seller").get("username"), sellerUsername));

        if (rating != null) {
            predicates.add(cb.equal(root.get("rating"), rating));
        }

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getSingleResult();
    }
}
