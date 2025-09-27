package com.example.holidayplanner.controller;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.holidayplanner.generated.model.SharedHoliday;
import com.example.holidayplanner.service.contract.HolidayServiceContract;
import com.example.holidayplanner.validation.HolidayServiceValidator;
import com.jayway.jsonpath.JsonPath;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = HolidayControllerIntegrationTest.ContextConfiguration.class)
class HolidayControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HolidayServiceContract holidayServiceContract;

    @Autowired
    private HolidayServiceValidator holidayServiceValidator;

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        HolidayServiceContract holidayServiceContract(){
            return Mockito.mock(HolidayServiceContract.class);
        }

        @Bean
        HolidayServiceValidator holidayServiceValidator() {return Mockito.mock(HolidayServiceValidator.class); }
    }


    @BeforeEach
    void resetMocks() {
        Mockito.reset(holidayServiceContract);
    }

    @Test
    @DisplayName("with valid parameters returns holidays")
    void getLastNumberOfHolidays_ExceedsMaxLimit_ReturnsBadRequest() throws Exception {
        when(holidayServiceContract.getLastNumberOfHolidays(anyString(), anyString()))
                .thenThrow(new com.example.holidayplanner.exception.InvalidParameterException("non-supported numberOfHolidays '20', must be between 1 and 12 inclusive"));
        mockMvc.perform(get("/api/holidays/last-number-of-holidays/NL")
                        .param("numberOfHolidays", "20") // Exceeds max limit of 12
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("non-supported numberOfHolidays '20', must be between 1 and 12 inclusive"));
    }

    @Test
    @DisplayName("with valid parameters returns holidays")
    void getLastNumberOfHolidays_ExceedsMaxLimit_ReturnsOK() throws Exception {
        doNothing().when(holidayServiceValidator).validateCountryCodesAndDays(anySet(), anyString());

        mockMvc.perform(get("/api/holidays/last-number-of-holidays/NL")
                        .param("numberOfHolidays", "20") // Exceeds max limit of 12
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("with valid country code returns ok")
    void getNonWeekendHolidayCounts_ValidCountryCodes_ReturnsBadRequest() throws Exception {
        doNothing().when(holidayServiceValidator).validateCountryCodesAndYear(anyString(), anySet());

        mockMvc.perform(get("/api/holidays/non-weekend/2025")
                        .param("countryCodes", "NL") // Valid country code
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("with invalid country code returns bad request")
    void getNonWeekendHolidayCounts_InvalidCountryCodes_ReturnsBadRequest() throws Exception {
        when(holidayServiceContract.getNonWeekendHolidayCounts(anyString(), anyString()))
                .thenThrow(new com.example.holidayplanner.exception.InvalidParameterException("non ISO 3166-1 alpha-2 compliant country code(s) 'XYZ'"));

        mockMvc.perform(get("/api/holidays/non-weekend/2025")
                        .param("countryCodes", "XYZ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("getNonWeekendHolidayCounts.countryCodes: must match \"^([A-Za-z]{2})(,[A-Za-z]{2})*$\""));
    }

    @Test
    @DisplayName("with invalid year format returns bad request")
    void getSharedHolidays_InvalidYearFormat_ReturnsBadRequest() throws Exception {
        Mockito.doThrow(new com.example.holidayplanner.exception.InvalidParameterException("year 'invalidYear' is not a valid year, must be a number between 1975 and 2075 inclusive"))
                .when(holidayServiceContract)
                .getSharedHolidays(anyString(), anyString(), anyString());

        mockMvc.perform(get("/api/holidays/shared/invalidYear/NL/FR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(result -> {
            String message = JsonPath.read(result.getResponse().getContentAsString(), "$.message");
            org.assertj.core.api.Assertions.assertThat(message).startsWith("getSharedHolidays.year:");
        });
    }

    @Test
    @DisplayName("with invalid year 1800 returns bad request")
    void getNonWeekendHolidayCounts_InvalidYear_ReturnsBadRequest() throws Exception {
        when(holidayServiceContract.getNonWeekendHolidayCounts(anyString(), anyString()))
                .thenThrow(new com.example.holidayplanner.exception.InvalidParameterException("non-supported year '1800', must be between 1975 and 2075 inclusive"));

        mockMvc.perform(get("/api/holidays/non-weekend/1800?countryCodes=NL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("non-supported year '1800', must be between 1975 and 2075 inclusive"));
    }

    @Test
    @DisplayName("with valid input returns shared holidays")
    void getSharedHolidays_ValidInput_ReturnsSharedHolidays() throws Exception {
        SharedHoliday[] sharedHolidays = new SharedHoliday[]{
                new SharedHoliday("2025-01-01", "Nieuwjaarsdag", "Neujahr")
        };
        when(holidayServiceContract.getSharedHolidays(anyString(), anyString(), anyString())).thenReturn(Arrays.asList(sharedHolidays));

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
        when(holidayServiceContract.getSharedHolidays(anyString(), anyString(), anyString()))
                .thenThrow(new com.example.holidayplanner.exception.InvalidParameterException("non ISO 3166-1 alpha-2 compliant country code(s) 'XX'"));

        mockMvc.perform(get("/api/holidays/shared/2025/XX/GB")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("non ISO 3166-1 alpha-2 compliant country code(s) 'XX'"));
    }

    @Test
    @DisplayName("when no resource found returns not found")
    void getSharedHolidays_NoResourceFoundException() throws Exception {
        doAnswer(invocation -> {
            throw new NoResourceFoundException(HttpMethod.GET, "/invalid/path");
        }).when(holidayServiceContract).getSharedHolidays(anyString(), anyString(), anyString());

        mockMvc.perform(get("/api/holidays/shared1/2025/XX/GB")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No static resource api/holidays/shared1/2025/XX/GB."));
    }

    @Test
    @DisplayName("when no shared holidays returns empty list")
    void getSharedHolidays_NoSharedHolidays_ReturnsEmptyList() throws Exception {
        when(holidayServiceContract.getSharedHolidays(anyString(), anyString(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/holidays/shared/2025/NL/FR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
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