package com.project.velo.service.advertisement;

import com.project.velo.repository.AdvertisementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class AdvertisementCleanUpServiceTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @InjectMocks
    private AdvertisementCleanUpService cleanUpService;

    @Test
    void cleanupExpiredTops_ShouldInvokeRepository() {

        given(advertisementRepository.resetExpiredTopFlags(any(LocalDateTime.class)))
                .willReturn(5);

        cleanUpService.cleanupExpiredTops();

        verify(advertisementRepository).resetExpiredTopFlags(any(LocalDateTime.class));
    }
}
