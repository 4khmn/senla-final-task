package com.project.velo.repository;

import com.project.velo.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepository extends BaseRepository<Message, Long>{

    protected MessageRepository() {
        super(Message.class);
    }


    public List<Message> findByChatWithPagination(Long chatId, int page, int size) {
        return entityManager.createQuery(
                        "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt DESC", Message.class)
                .setParameter("chatId", chatId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countByChat(Long chatId) {
        return entityManager.createQuery(
                        "SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId", Long.class)
                .setParameter("chatId", chatId)
                .getSingleResult();
    }

}
