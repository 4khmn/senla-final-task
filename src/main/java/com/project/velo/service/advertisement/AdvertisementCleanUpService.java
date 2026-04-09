package com.project.velo.service.advertisement;

import com.project.velo.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementCleanUpService {

    private final AdvertisementRepository advertisementRepository;

    // at 03:00
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTops() {
        log.info("Starting scheduled cleanup of expired TOP statuses...");

        int updatedCount = advertisementRepository.resetExpiredTopFlags(LocalDateTime.now());

        log.info("Cleanup finished. Reset {} advertisements.", updatedCount);
    }



}
