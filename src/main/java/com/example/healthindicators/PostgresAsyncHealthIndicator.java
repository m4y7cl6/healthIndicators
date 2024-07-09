package com.example.healthindicators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PostgresAsyncHealthIndicator implements HealthIndicator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Async
    @Override
    public Health health() {
        CompletableFuture<Health> futureHealth = CompletableFuture.supplyAsync(() -> {
            try {
                // 使用特定的 SQL 查詢來檢查數據庫健康狀態
                jdbcTemplate.execute("SELECT 1");
                return Health.up().build();
            } catch (Exception e) {
                return Health.down(e).build();
            }
        });

        try {
            return futureHealth.get(); // 確保我們等待結果
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}

