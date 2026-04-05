package com.project.velo.repository;

import com.project.velo.entity.Advertisement;
import org.springframework.stereotype.Repository;


@Repository
public class AdvertisementRepository extends BaseRepository<Advertisement, Long> {

    public AdvertisementRepository() {
        super(Advertisement.class);
    }


}
