package com.example.holidayplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

// Main application class to bootstrap the Spring Boot application
@EnableCaching
@SpringBootApplication
public class
HolidayPlannerApiApplication{

    public static void main(String[] args) {
        SpringApplication.run(HolidayPlannerApiApplication.class, args);
    }

}
