package com.example.microservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if column exists, if not, add it
            jdbcTemplate.execute("ALTER TABLE conversations ADD COLUMN font VARCHAR(50) DEFAULT NULL");
            System.out.println("[ChatService] Successfully added column 'font' to 'conversations' table.");
        } catch (Exception e) {
            // Column already exists or other error
            System.out.println("[ChatService] Column 'font' in 'conversations' might already exist: " + e.getMessage());
        }
    }
}
