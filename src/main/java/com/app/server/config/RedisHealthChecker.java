package com.app.server.config;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Component;

@Component
public class RedisHealthChecker {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisHealthChecker(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public boolean isRedisAvailable() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            return connection.ping() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
