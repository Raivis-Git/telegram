package com.alphabot.telegram.message.config;

import org.springframework.cache.*;
import org.springframework.cache.annotation.*;
import org.springframework.cache.concurrent.*;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
public class RateLimitConfig {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("rateLimits");
    }
}
