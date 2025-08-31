package com.example.holidayplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication//(exclude = {ErrorMvcAutoConfiguration.class})
public class HolidayPlannerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidayPlannerApiApplication.class, args);
    }

}
