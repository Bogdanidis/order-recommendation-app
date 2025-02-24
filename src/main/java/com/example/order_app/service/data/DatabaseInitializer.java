package com.example.order_app.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer {
    private final JdbcTemplate jdbcTemplate;
    private final DatabasePopulationService populationService;

    @Value("${app.db.reinitialize:false}")
    private boolean reinitializeDb;

    @Value("classpath:db/init/init-database.sql")
    private Resource initScript;

    @EventListener(ApplicationStartedEvent.class)
    public void initialize() {
        if (reinitializeDb) {
            try {
                log.info("Reinitializing database...");
                reinitializeDatabase();
                // Your existing population service will run after reinitialization
                // because of the ApplicationStartedEvent
            } catch (IOException e) {
                log.error("Database initialization failed", e);
                throw new RuntimeException("Database initialization failed", e);
            }
        }
    }

    private void reinitializeDatabase() throws IOException {
        String sql = new String(FileCopyUtils.copyToByteArray(initScript.getInputStream()));
        Arrays.stream(sql.split(";"))
                .filter(StringUtils::hasText)
                .forEach(statement -> {
                    try {
                        jdbcTemplate.execute(statement.trim());
                    } catch (Exception e) {
                        log.warn("Error executing statement: {}. Error: {}",
                                statement, e.getMessage());
                    }
                });
    }
}
