package com.example.holidayplanner.controller;

import com.example.holidayplanner.model.SharedHoliday;
import com.example.holidayplanner.service.HolidayService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HolidayControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private HolidayService holidayService;

    @Test
    @DisplayName("with valid parameters returns holidays")
    void getLastNumberOfHolidays_ExceedsMaxLimit_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/holidays/last-number-of-holidays/NL")
                        .param("numberOfHolidays", "20") // Exceeds max limit of 12
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("non-supported numberOfHolidays '20', must be between 1 and 12 inclusive."));
    }

    @Test
    @DisplayName("with invalid country code returns bad request")
    void getNonWeekendHolidayCounts_InvalidCountryCodes_ReturnsBadRequest() throws Exception {
        when(holidayService.getNonWeekendHolidayCounts(anyInt(), anyString()))
                .thenThrow(new com.example.holidayplanner.exception.InvalidParameterException("non ISO 3166-1 alpha-2 compliant country code(s) 'XYZ'."));

        mockMvc.perform(get("/api/holidays/non-weekend/2025")
                        .param("countryCodes", "XYZ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("non ISO 3166-1 alpha-2 compliant country code(s) 'XYZ'."));
    }

    @Test
    @DisplayName("with invalid year format returns bad request")
    void getSharedHolidays_InvalidYearFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/holidays/shared/invalidYear/NL/FR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("year 'invalidYear' is not a valid year, must be a number between 1975 and 2075 inclusive."));
    }

    @Test
    @DisplayName("with invalid year 1800 returns bad request")
    void getNonWeekendHolidayCounts_InvalidYear_ReturnsBadRequest() throws Exception {
        when(holidayService.getNonWeekendHolidayCounts(anyInt(), anyString()))
                .thenThrow(new com.example.holidayplanner.exception.InvalidParameterException("non-supported year '1800', must be between 1975 and 2075 inclusive."));

        mockMvc.perform(get("/api/holidays/non-weekend/1800?countryCodes=NL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("non-supported year '1800', must be between 1975 and 2075 inclusive."));
    }

    @Test
    @DisplayName("with valid input returns shared holidays")
    void getSharedHolidays_ValidInput_ReturnsSharedHolidays() throws Exception {
        SharedHoliday[] sharedHolidays = new SharedHoliday[]{
                new SharedHoliday("2025-01-01", "Nieuwjaarsdag", "Neujahr")
        };
        when(holidayService.getSharedHolidays(anyString(), anyString(), anyString())).thenReturn(Arrays.asList(sharedHolidays));

        mockMvc.perform(get("/api/holidays/shared/2025/NL/DE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2025-01-01"))
                .andExpect(jsonPath("$[0].localNameCountry1").value("Nieuwjaarsdag"))
                .andExpect(jsonPath("$[0].localNameCountry2").value("Neujahr"));
    }

    @Test
    @DisplayName("with invalid country code returns bad request")
    void getSharedHolidays_InvalidCountry_ReturnsBadRequest() throws Exception {
        when(holidayService.getSharedHolidays(anyString(), anyString(), anyString()))
                .thenThrow(new com.example.holidayplanner.exception.InvalidParameterException("non ISO 3166-1 alpha-2 compliant country code(s) 'XX'."));

        mockMvc.perform(get("/api/holidays/shared/2025/XX/GB")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("non ISO 3166-1 alpha-2 compliant country code(s) 'XX'."));
    }

    @Test
    @DisplayName("when no resource found returns not found")
    void getSharedHolidays_NoResourceFoundException() throws Exception {
        doAnswer(invocation -> {
            throw new NoResourceFoundException(HttpMethod.GET, "/invalid/path");
        }).when(holidayService).getSharedHolidays(anyString(), anyString(), anyString());

        mockMvc.perform(get("/api/holidays/shared1/2025/XX/GB")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No static resource api/holidays/shared1/2025/XX/GB."));
    }

    @Test
    @DisplayName("when no shared holidays returns empty list")
    void getSharedHolidays_NoSharedHolidays_ReturnsEmptyList() throws Exception {
        when(holidayService.getSharedHolidays(anyString(), anyString(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/holidays/shared/2025/NL/FR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    @DisplayName("health endpoint returns UP status")
    void getHealthEndpoint_ReturnsUpStatus() throws Exception {
        mockMvc.perform(get("/management/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

}