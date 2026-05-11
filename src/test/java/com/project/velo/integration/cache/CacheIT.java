package com.project.velo.integration.cache;

import com.project.velo.dto.request.AdvertisementFilterDto;
import com.project.velo.dto.update.CategoryUpdateDto;
import com.project.velo.integration.BaseIT;
import com.project.velo.service.advertisement.AdvertisementService;
import com.project.velo.service.advertisement.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class CacheIT extends BaseIT {

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @BeforeEach
    void clearCache() {
        connectionFactory.getConnection().serverCommands().flushAll();
    }

    @Test
    @Sql("/sql/cache/init_cache.sql")
    void testAdvertisementDetailsCacheFlow() {
        Long adId = 100L;
        String cacheName = "advertisement_details";
        assertNull(cacheManager.getCache(cacheName).get(adId));

        advertisementService.getById(adId);
        assertNotNull(cacheManager.getCache(cacheName).get(adId));


        advertisementService.delete(adId, "seller1");

        assertNull(cacheManager.getCache(cacheName).get(adId));
    }

    @Test
    @Sql("/sql/cache/init_cache.sql")
    void testAdvertisementsCacheFlow() {
        String cacheName = "advertisements";
        AdvertisementFilterDto filterDto = new AdvertisementFilterDto(null, null, null, null, null);
        Object[] expectedKey = new Object[] {"no_filter", 0, 10};

        assertNull(cacheManager.getCache(cacheName).get(expectedKey));
        advertisementService.getAll(filterDto, 0, 10);

        assertNotNull(cacheManager.getCache(cacheName).get(expectedKey));

        advertisementService.delete(100L, "seller1");

        assertNull(cacheManager.getCache(cacheName).get(expectedKey));
    }

    @Test
    @Sql("/sql/cache/init_cache.sql")
    void testCategoryCacheFlow() {

        CategoryUpdateDto dto = new CategoryUpdateDto("name", "displayName");
        String cacheName = "categories";

        categoryService.getAll();

        assertNotNull(cacheManager.getCache(cacheName).get(org.springframework.cache.interceptor.SimpleKey.EMPTY));

        categoryService.update(1L, dto);

        assertNull(cacheManager.getCache(cacheName).get(org.springframework.cache.interceptor.SimpleKey.EMPTY));
    }
}
