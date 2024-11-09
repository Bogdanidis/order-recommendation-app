package com.example.order_app.service.data;

import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializationListener {
    private final DatabasePopulationService populationService;
    private final IUserService userService;

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            if (shouldPopulateData()) {
                log.info("Starting database population...");
                populationService.populateDatabase();
                log.info("Database population completed successfully.");
            } else {
                log.info("Database already populated, skipping...");
            }
        } catch (Exception e) {
            log.error("Error during database population: ", e);
            throw e;
        }
    }

    private boolean shouldPopulateData() {
        return userService.countUsers() <= 2;
    }
}
