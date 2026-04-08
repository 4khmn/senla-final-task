package com.project.velo.repository;

import com.project.velo.entity.SalesHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
