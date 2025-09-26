package com.example.holidayplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class to bootstrap the Spring Boot application.
 * <p>
 * This class serves as the entry point for the Holiday Planner API, enabling caching and
 * initializing the Spring Boot context.
 */
@EnableCaching
@SpringBootApplication
public class
HolidayPlannerApiApplication{

    /**
     * Main method to launch the Spring Boot application.
     * <p>
     * Starts the Holiday Planner API by running the Spring application context.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(HolidayPlannerApiApplication.class, args);
    }

}
