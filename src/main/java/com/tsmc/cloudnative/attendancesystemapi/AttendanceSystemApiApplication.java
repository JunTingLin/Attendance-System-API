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
        try {
            log.info("嘗試連接資料庫...");
            if (jdbcTemplate == null) {
                log.error("JdbcTemplate 未正確注入，請檢查應用設定");
                return;
            }
            String result = jdbcTemplate.queryForObject("SELECT 1", String.class);
            log.info("資料庫連線成功: {}", result);
        } catch (Exception e) {
            log.error("資料庫連線失敗，錯誤訊息如下：", e);
        }
    }

}
