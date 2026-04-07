package com.project.velo.repository;

import com.project.velo.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepository extends BaseRepository<Message, Long>{

    protected MessageRepository() {
        super(Message.class);
    }


    public List<Message> findAllByChat(Long chatId) {
        return entityManager.createQuery(
                "SELECT m FROM Message m WHERE m.chat.id = :id ORDER BY m.sentAt ASC", Message.class)
                .setParameter("id", chatId)
                .getResultList();
    }

}
