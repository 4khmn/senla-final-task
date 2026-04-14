package com.project.velo.repository;

import com.project.velo.entity.Advertisement;
import com.project.velo.entity.enums.AdStatus;
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

    public List<Advertisement> findAllByUsername(String username) {
        return entityManager.createQuery(
                "SELECT a FROM Advertisement a WHERE seller.username = :username " +
                        "AND a.status = 'ACTIVE' ORDER BY a.createdAt ASC", Advertisement.class)
                .setParameter("username", username)
                .getResultList();
    }


    public int resetExpiredTopFlags(LocalDateTime now) {
        return entityManager.createQuery(
                        "UPDATE Advertisement a SET a.top = false " +
                                "WHERE a.top = true AND a.topUntil < :now")
                .setParameter("now", now)
                .executeUpdate();
    }

    public List<Advertisement> findAllFiltered(String query, String category, int page, int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Advertisement> cq = cb.createQuery(Advertisement.class);
        Root<Advertisement> root = cq.from(Advertisement.class);
        Join<Object, Object> authorJoin = root.join("seller");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("status"), AdStatus.ACTIVE));

        Expression<Integer> searchRelevance = cb.literal(0);

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
                Expression<Integer> categoryMatch = cb.selectCase()
                        .when(cb.or(
                                cb.like(cb.lower(root.get("category").get("name")), pattern),
                                cb.like(cb.lower(root.get("category").get("displayName")), pattern)
                        ), 5)
                        .otherwise(0)
                        .as(Integer.class);

                Expression<Integer> caseExpression = cb.selectCase()
                        .when(cb.like(cb.lower(root.get("title")), pattern), 10)
                        .when(cb.greaterThan(categoryMatch, 0), 5)
                        .when(cb.like(cb.lower(root.get("description")), pattern), 1)
                        .otherwise(0)
                        .as(Integer.class);

                searchRelevance = cb.sum(searchRelevance, caseExpression);
            }
            predicates.add(cb.and(wordPredicates.toArray(new Predicate[0])));
        }

        if (category != null && !category.isBlank()) {
            predicates.add(cb.equal(root.get("category").get("name"), category));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        Expression<Object> effectiveRating = cb.selectCase()
                .when(cb.equal(authorJoin.get("rating"), BigDecimal.ZERO), new BigDecimal("3.5"))
                .otherwise(authorJoin.get("rating"));

        cq.orderBy(
                cb.desc(root.get("top")),
                cb.desc(searchRelevance),
                cb.desc(effectiveRating),
                cb.desc(root.get("createdAt"))
        );

        return entityManager.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }


    public long countFiltered(String query, String category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Advertisement> root = cq.from(Advertisement.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("status"), AdStatus.ACTIVE));

        if (query != null && !query.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase() + "%"));
        }

        if (category != null && !category.isBlank()) {
            predicates.add(cb.equal(root.get("category").get("name"), category));
        }

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getSingleResult();
    }


}
