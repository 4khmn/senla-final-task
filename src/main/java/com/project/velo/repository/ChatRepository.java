package com.project.velo.repository;

import com.project.velo.entity.Chat;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatRepository extends BaseRepository<Chat, Long> {

    protected ChatRepository() {
        super(Chat.class);
    }

    public Optional<Chat> findByAdvertisementIdAndBuyerId(Long advertisementId, Long buyerId) {
        return entityManager.createQuery(
                "SELECT c FROM Chat c WHERE c.advertisement.id = :advertisementId AND c.buyer.id = :buyerId", Chat.class)
                .setParameter("advertisementId", advertisementId)
                .setParameter("buyerId", buyerId)
                .getResultList()
                .stream()
                .findFirst();
    }


    public List<Chat> findAllByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT c FROM Chat c WHERE c.buyer.username = :username OR c.seller.username = :username " +
                                "ORDER BY c.updatedAt DESC", Chat.class)
                .setParameter("username", username)
                .getResultList();
    }
}
