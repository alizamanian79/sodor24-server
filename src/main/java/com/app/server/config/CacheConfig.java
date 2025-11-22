package com.app.server.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
public class CacheConfig {

    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisHealthChecker redisHealthChecker;

    public CacheConfig(RedisConnectionFactory redisConnectionFactory, RedisHealthChecker redisHealthChecker) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.redisHealthChecker = redisHealthChecker;
    }

    @Bean
    public CacheManager cacheManager() {
        if (redisHealthChecker.isRedisAvailable()) {
            System.out.println("✅ Redis is available. Using RedisCacheManager.");
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .disableCachingNullValues();
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(config)
                    .build();
        } else {
            System.out.println("⚠ Redis is NOT available. Using in-memory cache.");
            return new ConcurrentMapCacheManager("users", "userById", "userByUsername");
        }
    }
}
