package com.app.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.time.Duration;


@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {


    private final RedisConnectionFactory redisConnectionFactory;


    @Bean
    public CacheManager cacheManager() {

        try {

            // تست اتصال Redis
            redisConnectionFactory
                    .getConnection()
                    .ping();


            RedisCacheManager redisCacheManager =
                    RedisCacheManager.builder(
                            redisConnectionFactory
                    )
                    .cacheDefaults(
                            org.springframework.data.redis.cache
                            .RedisCacheConfiguration
                            .defaultCacheConfig()
                            .entryTtl(Duration.ofMinutes(30))
                    )
                    .build();


            System.out.println(
                    "CACHE PROVIDER : REDIS"
            );


            return redisCacheManager;


        } catch (Exception e) {


            System.out.println(
                    "CACHE PROVIDER : LOCAL MEMORY"
            );


            return new ConcurrentMapCacheManager();

        }

    }

}