package com.example.healthindicators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.concurrent.CompletableFuture;

@Component
public class MsSQLAsyncHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public MsSQLAsyncHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Async
    public Health checkHealth() {
//        try (Connection connection = dataSource.getConnection()) {
//            if (connection.isValid(1)) {
//                return new AsyncResult<>(Health.up().withDetail("MSSQL Database", "Available").build());
//            } else {
//                return new AsyncResult<>(Health.down().withDetail("MSSQL Database", "Not Available").build());
//            }
//        } catch (SQLException e) {
//            return new AsyncResult<>(Health.down().withDetail("MSSQL Database", "Not Available").withException(e).build());
//        }
        CompletableFuture<Health> futureHealth = CompletableFuture.supplyAsync(() -> {
            try {
                // 使用特定的 SQL 查詢來檢查數據庫健康狀態
                jdbcTemplate.execute("select 1 ");
                return Health.up().build();
            } catch (Exception e) {
                return Health.status(String.valueOf(e)).build();
            }
        });

        try {
            return futureHealth.get();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    @Override
    public Health health() {
        // Return immediate UNKNOWN status if the check is still in progress
        Health healthFuture = checkHealth();
        try {
            // 返回立即的健康狀態
            return healthFuture;
        } catch (Exception e) {
            return Health.unknown().withDetail("MSSQL Database", "Health check in progress").build();
        }
    }

}
