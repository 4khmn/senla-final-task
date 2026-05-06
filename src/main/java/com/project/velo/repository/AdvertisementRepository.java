package com.project.velo.repository;

import com.project.velo.dto.request.AdvertisementFilterDto;
import com.project.velo.entity.Advertisement;
import com.project.velo.entity.User;
import com.project.velo.entity.enums.AdStatus;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;


@Repository
public class AdvertisementRepository extends BaseRepository<Advertisement, Long> {

    public AdvertisementRepository() {
        super(Advertisement.class);
    }

    public long countByUsernameAndStatus(String username, AdStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(a) FROM Advertisement a WHERE a.seller.username = :username " +
                                "AND a.status = :status", Long.class)
                .setParameter("username", username)
                .setParameter("status", status)
                .getSingleResult();
    }

    public int resetExpiredTopFlags(LocalDateTime now) {
        return entityManager.createQuery(
                        "UPDATE Advertisement a SET a.top = false " +
                                "WHERE a.top = true AND a.topUntil < :now")
                .setParameter("now", now)
                .executeUpdate();
    }

    public List<Advertisement> findAllFiltered(AdvertisementFilterDto filter, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Advertisement> cq = cb.createQuery(Advertisement.class);
        Root<Advertisement> root = cq.from(Advertisement.class);
        Join<Advertisement, User> authorJoin = root.join("seller");

        cq.where(buildPredicates(cb, root, authorJoin, filter.query(), filter.categoryId(), filter.minPrice(), filter.maxPrice()));

        Expression<Object> effectiveRating = cb.selectCase()
                .when(cb.equal(authorJoin.get("rating"), BigDecimal.ZERO), new BigDecimal("3.5"))
                .otherwise(authorJoin.get("rating"));

        Expression<Boolean> trueTop = cb.selectCase()
                .when(cb.isTrue(root.get("top")), cb.greaterThan(root.get("topUntil"), LocalDateTime.now()))
                .otherwise(false)
                .as(Boolean.class);

        List<Order> orders = new ArrayList<>();

        boolean isPriceSort = filter.sortDirection() != null && !filter.sortDirection().isBlank();
        if (isPriceSort) {
            if ("asc".equalsIgnoreCase(filter.sortDirection())) {
                orders.add(cb.asc(root.get("price")));
            } else {
                orders.add(cb.desc(root.get("price")));
            }
            orders.add(cb.desc(trueTop));
        } else {
            orders.add(cb.desc(trueTop));

            if (filter.query() != null && !filter.query().isBlank()) {
                orders.add(cb.desc(buildRelevance(cb, root, filter.query())));
            }
        }
        orders.add(cb.desc(effectiveRating));
        orders.add(cb.desc(root.get("createdAt")));

        cq.orderBy(orders);

        TypedQuery<Advertisement> typedQuery = entityManager.createQuery(cq);
        return applyPagination(typedQuery, page, size).getResultList();
    }


    public long countFiltered(AdvertisementFilterDto filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Advertisement> root = cq.from(Advertisement.class);
        Join<Advertisement, User> authorJoin = root.join("seller");

        cq.select(cb.count(root)).where(buildPredicates(cb, root, authorJoin, filter.query(), filter.categoryId(), filter.minPrice(), filter.maxPrice()));

        return entityManager.createQuery(cq).getSingleResult();
    }

    public List<Advertisement> findAllByUsername(String username, int page, int size) {
        TypedQuery<Advertisement> q = entityManager.createQuery(
                        "SELECT a FROM Advertisement a WHERE a.seller.username = :username " +
                                "AND a.status = :status ORDER BY a.createdAt DESC", Advertisement.class)
                .setParameter("username", username)
                .setParameter("status", AdStatus.ACTIVE);
        return applyPagination(q, page, size).getResultList();
    }

    public List<Advertisement> findAllForAdmin(int page, int size) {
        return entityManager.createQuery(
                        "SELECT a FROM Advertisement a ORDER BY a.createdAt DESC", Advertisement.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countAll() {
        return entityManager.createQuery(
                        "SELECT COUNT(a) FROM Advertisement a", Long.class)
                .getSingleResult();
    }


    private Predicate[] buildPredicates(CriteriaBuilder cb,
                                        Root<Advertisement> root,
                                        Join<Advertisement, User> authorJoin,
                                        String query,
                                        Long categoryId,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("status"), AdStatus.ACTIVE));
        predicates.add(cb.isTrue(authorJoin.get("enabled")));

        if (query != null && !query.isBlank()) {
            String[] words = query.toLowerCase().split("\\s+");
            List<Predicate> wordPredicates = new ArrayList<>();

            for (String word : words) {
                String pattern = "%" + word + "%";
                wordPredicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("category").get("name")), pattern),
                        cb.like(cb.lower(root.get("category").get("displayName")), pattern)
                ));
            }
            predicates.add(cb.and(wordPredicates.toArray(new Predicate[0])));
        }

        if (categoryId != null) {
            predicates.add(cb.equal(root.get("category").get("id"), categoryId));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private Expression<Integer> buildRelevance(CriteriaBuilder cb, Root<Advertisement> root, String query) {
        if (query == null || query.isBlank()) {
            return cb.literal(0);
        }

        Expression<Integer> totalRelevance = cb.literal(0);
        String[] words = query.toLowerCase().split("\\s+");

        for (String word : words) {
            String pattern = "%" + word + "%";

            Expression<Integer> wordWeight = cb.selectCase()
                    .when(cb.like(cb.lower(root.get("title")), pattern), 10)
                    .when(cb.or(
                            cb.like(cb.lower(root.get("category").get("name")), pattern),
                            cb.like(cb.lower(root.get("category").get("displayName")), pattern)
                    ), 5)
                    .when(cb.like(cb.lower(root.get("description")), pattern), 1)
                    .otherwise(0)
                    .as(Integer.class);

            totalRelevance = cb.sum(totalRelevance, wordWeight);
        }
        return totalRelevance;
    }

    private <T> TypedQuery<T> applyPagination(TypedQuery<T> query, int page, int size) {
        return query.setFirstResult(page * size).setMaxResults(size);
    }

}
