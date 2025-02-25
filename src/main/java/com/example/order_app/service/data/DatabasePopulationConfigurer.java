package com.example.order_app.service.data;

import com.example.order_app.service.data.DatabasePopulationServiceV2;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Handles database population AFTER the application is fully initialized.
 * This ensures all beans are available and the database is ready.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabasePopulationConfigurer {

    private final DatabasePopulationServiceV2 populationService;
    private final IUserService userService;

    @Value("${app.db.populate:true}")
    private boolean shouldPopulate;

    /**
     * Populate the database with test data after the application is fully initialized.
     * Using ApplicationReadyEvent ensures all initialization is complete.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void populateDatabase() {
        try {
            if (shouldPopulate && shouldPopulateData()) {
                log.info("Starting database population...");
                populationService.populateDatabase();
                log.info("Database population completed successfully.");
            } else {
                log.info("Database population skipped.");
            }
        } catch (Exception e) {
            log.error("Error during database population: ", e);
            // Don't throw exception here - let the application continue running
        }
    }

    private boolean shouldPopulateData() {
        return userService.countUsers() <= 2;
    }
}
