package com.example.order_app.service.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Database initialization configuration that runs BEFORE Spring Boot's standard database setup.
 * Ensures the database exists before any JPA or Flyway components try to connect.
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@Slf4j
public class DatabaseInitializer {

    @Value("${app.db.create:true}")
    private boolean shouldCreateDatabase;

    @Value("${app.db.reinitialize:false}")
    private boolean reinitializeDb;

    @Value("classpath:db/init/init-database.sql")
    private Resource initScript;

    /**
     * Initialize the database BEFORE the main DataSource is created.
     * This ensures the database exists before Spring Boot attempts to connect.
     */
    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl();
        String username = properties.getUsername();
        String password = properties.getPassword();
        String driverClassName = properties.getDriverClassName();

        if (shouldCreateDatabase || reinitializeDb) {
            initializeDatabase(url, username, password);
        }

        // Return a standard DataSource for Spring to use
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        return dataSource;
    }

    private void initializeDatabase(String url, String username, String password) {
        // Extract the base URL (MySQL server without database name)
        String baseUrl = url.substring(0, url.lastIndexOf("/"));

        try (Connection conn = DriverManager.getConnection(baseUrl, username, password)) {
            log.info("Connected to MySQL server, executing initialization script...");
            executeInitScript(conn);
            log.info("Database initialization completed successfully.");
        } catch (SQLException | IOException e) {
            log.error("Error initializing database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void executeInitScript(Connection conn) throws SQLException, IOException {
        String sql = new String(FileCopyUtils.copyToByteArray(initScript.getInputStream()));
        try (Statement stmt = conn.createStatement()) {
            // Execute each statement in the script
            Arrays.stream(sql.split(";"))
                    .filter(StringUtils::hasText)
                    .forEach(statement -> {
                        try {
                            stmt.execute(statement.trim());
                            log.debug("Executed SQL: {}", statement.trim());
                        } catch (SQLException e) {
                            log.warn("Error executing statement: {}. Error: {}",
                                    statement.trim(), e.getMessage());
                        }
                    });
        }
    }
}
