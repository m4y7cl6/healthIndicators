package com.example.healthindicators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class RedisAsyncHealthIndicator implements HealthIndicator {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Async
    @Override
    public Health health() {
        CompletableFuture<Health> futureHealth = CompletableFuture.supplyAsync(() -> {
            try {
                // 執行 Redis PING 命令
                String pingResponse = redisTemplate.getConnectionFactory().getConnection().ping();
                if ("PONG".equals(pingResponse)) {
                    return Health.up().build();
                } else {
                    return Health.down().withDetail("ping", pingResponse).build();
                }
            } catch (Exception e) {
                return Health.down(e).build();
            }
        });

        try {
            return futureHealth.get();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
