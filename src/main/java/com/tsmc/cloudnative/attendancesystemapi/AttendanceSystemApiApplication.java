package com.tsmc.cloudnative.attendancesystemapi;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class AttendanceSystemApiApplication {

    private final JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(AttendanceSystemApiApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testDatabaseConnection() {
        // 打印環境變數
        log.info("環境變數檢查：");
        log.info("SERVER_PORT: {}", System.getenv("SERVER_PORT"));
        log.info("DB_HOST: {}", System.getenv("DB_HOST"));
        log.info("DB_PORT: {}", System.getenv("DB_PORT"));
        log.info("DB_NAME: {}", System.getenv("DB_NAME"));
        log.info("DB_USER: {}", System.getenv("DB_USER"));
        log.info("DB_PASS: {}", System.getenv("DB_PASS"));
        log.info("JWT_SECRET 長度: {}", System.getenv("JWT_SECRET") != null ? System.getenv("JWT_SECRET").length() : "null");
        log.info("GCS_BUCKET_NAME: {}", System.getenv("GCS_BUCKET_NAME"));

        try {
            log.info("嘗試連接資料庫...");
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            log.info("資料庫連線成功: {}", result);
        } catch (Exception e) {
            log.error("資料庫連線失敗，錯誤訊息如下：", e);
        }
    }

}
