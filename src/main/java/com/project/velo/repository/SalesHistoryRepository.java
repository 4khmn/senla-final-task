package com.project.velo.repository;

import com.project.velo.entity.SalesHistory;
import com.project.velo.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SalesHistoryRepository extends BaseRepository<SalesHistory, Long> {

    protected SalesHistoryRepository() {
        super(SalesHistory.class);
    }

    public List<SalesHistory> findAllBySellerUsernameOrderBySoldAt(String username){
        return entityManager.createQuery(
                "SELECT s FROM SalesHistory s WHERE s.seller.username = :username ORDER BY s.soldAt DESC", SalesHistory.class)
                .setParameter("username", username)
                .getResultList();

    }

    public Optional<SalesHistory> findByAdvertisementId(Long adId) {
        return entityManager.createQuery(
                        "SELECT s FROM SalesHistory s WHERE s.advertisement.id = :id", SalesHistory.class)
                .setParameter("id", adId)
                .getResultList()
                .stream()
                .findFirst();
    }
}
