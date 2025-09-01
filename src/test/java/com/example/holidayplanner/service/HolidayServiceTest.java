package com.example.holidayplanner.service;

import com.example.holidayplanner.model.CountryHolidayCount;
import com.example.holidayplanner.model.Holiday;
import com.example.holidayplanner.model.SharedHoliday;
import com.example.holidayplanner.validation.InputParameterValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HolidayServiceTest {

    @Mock
    private NagerDateApiService nagerDateApiService;

    @Mock
    private InputParameterValidator inputParameterValidator;

    @InjectMocks
    private HolidayService holidayService;

    private List<Holiday> mockHolidays;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockHolidays = List.of(
                new Holiday("2025-01-01", "Nieuwjaarsdag"),
                new Holiday("2025-04-18", "Goede Vrijdag"),
                new Holiday("2025-04-20", "Eerste Paasdag")
        );
        ReflectionTestUtils.setField(holidayService, "objectMapper", Mockito.mock(ObjectMapper.class));
    }

    @AfterEach
    void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Test getLastNumberOfHolidays with valid input")
    void getLastNumberOfHolidays_ValidInput_ReturnsHolidays() {
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(mockHolidays);

        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", 2);

        assertEquals(2, holidays.size());
        assertEquals("Eerste Paasdag", holidays.get(0).getLocalName());
        verify(inputParameterValidator).validateCountryCodesAndDays(anySet(), anyInt());
    }

    @Test
    @DisplayName("Test getLastNumberOfHolidays with valid input and also holiday of previous year")
    void getLastNumberOfHolidays_ValidInput_ReturnsCurrentAndPreviousYearHolidays() {
        List<Holiday> currentYearHoliday = List.of(
                new Holiday("2025-01-01", "Nieuwjaarsdag"),
                new Holiday("2025-04-18", "Goede Vrijdag")
        );
        List<Holiday> lastYearHoliday = List.of(
                new Holiday("2024-05-20", "Tweede Pinksterdag"),
                new Holiday("2024-12-25", "Eerste Kerstdag"),
                new Holiday("2024-12-26", "Tweede Kerstdag")
                );
        int currentYear = 2025;
        int lastYear = 2024;
        when(nagerDateApiService.fetchHolidays(eq(currentYear), anyString())).thenReturn(currentYearHoliday);
        when(nagerDateApiService.fetchHolidays(eq(lastYear), anyString())).thenReturn(lastYearHoliday);

        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", 4);

        assertEquals(4, holidays.size());
        assertEquals("Goede Vrijdag", holidays.get(0).getLocalName());
        verify(inputParameterValidator).validateCountryCodesAndDays(anySet(), anyInt());
    }

    @Test
    @DisplayName("Test getLastNumberOfHolidays with default number of holidays")
    void getLastNumberOfHolidays_DefaultNumberOfHolidays_ReturnsHolidays() {
        ReflectionTestUtils.setField(holidayService, "numberOfHolidays", 3);
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(mockHolidays);


        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", null);

        assertEquals(3, holidays.size());
        verify(inputParameterValidator).validateCountryCodesAndDays(anySet(), anyInt());
    }

    @Test
    @DisplayName("Test getNonWeekendHolidayCounts with valid input")
    void getNonWeekendHolidayCounts_ValidInput_ReturnsCounts() {
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(mockHolidays);

        List<CountryHolidayCount> counts = holidayService.getNonWeekendHolidayCounts(2025, "NL");

        assertEquals(1, counts.size());
        assertEquals("NL", counts.get(0).getCountryCode());
        assertEquals(2, counts.get(0).getHolidayCount());
        verify(inputParameterValidator).validateCountryCodesAndYear(anyInt(), anySet());
    }

    @Test
    @DisplayName("Test getSharedHolidays with valid input")
    void getSharedHolidays_ValidInput_ReturnsSharedHolidays() {
        List<Holiday> holidays1 = List.of(new Holiday("2025-01-01", "Nieuwjaarsdag"));
        List<Holiday> holidays2 = List.of(new Holiday("2025-01-01", "Neujahr"));

        when(nagerDateApiService.fetchHolidays(anyInt(), eq("NL"))).thenReturn(holidays1);
        when(nagerDateApiService.fetchHolidays(anyInt(), eq("DE"))).thenReturn(holidays2);

        List<SharedHoliday> sharedHolidays = holidayService.getSharedHolidays("2025", "NL", "DE");

        assertEquals(1, sharedHolidays.size());
        assertEquals("2025-01-01", sharedHolidays.get(0).getDate());
        verify(inputParameterValidator).validateSharedHolidayCountryCodesAndYear(anyString(), anySet());
    }

    @Test
    @DisplayName("Test getSharedHolidays with no shared holidays should return empty list")
    void getSharedHolidays_NoSharedHolidays_ReturnsEmptyList() {
        List<Holiday> holidays1 = List.of(new Holiday("2025-12-25", "Eerste Kerstdag"));
        List<Holiday> holidays2 = List.of(new Holiday("2025-12-26", "Tweede Kerstdag"));

        when(nagerDateApiService.fetchHolidays(anyInt(), eq("NL"))).thenReturn(holidays1);
        when(nagerDateApiService.fetchHolidays(anyInt(), eq("DE"))).thenReturn(holidays2);

        List<SharedHoliday> sharedHolidays = holidayService.getSharedHolidays("2025", "NL", "DE");

        assertTrue(sharedHolidays.isEmpty());
        verify(inputParameterValidator).validateSharedHolidayCountryCodesAndYear(anyString(), anySet());
    }
}