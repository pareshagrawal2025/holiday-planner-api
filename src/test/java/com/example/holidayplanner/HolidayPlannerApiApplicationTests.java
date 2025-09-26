package com.example.holidayplanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class HolidayPlannerApiApplicationTests {

    @Test
    @DisplayName("application context load test")
    void contextLoads() {
        // This is just a context load test so  no implementation is needed here.
    }

    @Test
    @DisplayName("Main method runs without exceptions")
    void mainMethodRuns() {
        assertDoesNotThrow(() -> HolidayPlannerApiApplication.main(new String[]{}));
    }

}
