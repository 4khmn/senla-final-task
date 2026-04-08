package com.project.velo.repository;

import com.project.velo.entity.Advertisement;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class AdvertisementRepository extends BaseRepository<Advertisement, Long> {

    public AdvertisementRepository() {
        super(Advertisement.class);
    }

    public List<Advertisement> findAllByUsername(String username) {
        return entityManager.createQuery(
                "SELECT a FROM Advertisement a WHERE seller.username = :username AND a.status = 'ACTIVE' ORDER BY a.createdAt ASC", Advertisement.class)
                .setParameter("username", username)
                .getResultList();
    }


}
