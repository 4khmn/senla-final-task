package com.project.velo.repository;

import com.project.velo.entity.SalesHistory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SalesHistoryRepository extends BaseRepository<SalesHistory, Long> {

    protected SalesHistoryRepository() {
        super(SalesHistory.class);
    }

    public List<SalesHistory> findSalesByUserOrderBySoldAt(String username, int page, int size) {
        return entityManager.createQuery(
                        "SELECT s FROM SalesHistory s " +
                                "JOIN FETCH s.advertisement ad " +
                                "JOIN FETCH s.seller sel " +
                                "JOIN FETCH sel.profile " +
                                "JOIN FETCH s.buyer buy " +
                                "JOIN FETCH buy.profile " +
                                "WHERE sel.username = :username " +
                                "ORDER BY s.soldAt DESC", SalesHistory.class)
                .setParameter("username", username)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countSalesByUser(String username) {
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM SalesHistory s WHERE s.seller.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    public Optional<SalesHistory> findByAdvertisementId(Long adId) {
        return entityManager.createQuery(
                        "SELECT s FROM SalesHistory s WHERE s.advertisement.id = :id", SalesHistory.class)
                .setParameter("id", adId)
                .getResultList()
                .stream()
                .findFirst();
    }



    public List<SalesHistory> findPurchasesByUserOrderBySoldAt(String username, int page, int size) {
        return entityManager.createQuery(
                        "SELECT s FROM SalesHistory s " +
                                "JOIN FETCH s.advertisement ad " +
                                "JOIN FETCH s.seller sel " +
                                "JOIN FETCH sel.profile " +
                                "JOIN FETCH s.buyer buy " +
                                "JOIN FETCH buy.profile " +
                                "WHERE s.buyer.username = :username ORDER BY s.soldAt DESC", SalesHistory.class)
                .setParameter("username", username)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countPurchasesByUser(String username) {
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM SalesHistory s WHERE s.buyer.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
    }
}
