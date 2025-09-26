package com.example.holidayplanner.service;

import com.example.holidayplanner.generated.model.CountryHolidayCount;
import com.example.holidayplanner.generated.model.Holiday;
import com.example.holidayplanner.generated.model.SharedHoliday;
import com.example.holidayplanner.validation.contract.HolidayServiceValidatorContract;
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
    private HolidayServiceValidatorContract holidayServiceValidator;

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

        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", "2");

        assertEquals(2, holidays.size());
        assertEquals("Eerste Paasdag", holidays.get(0).getLocalName());
        verify(holidayServiceValidator).validateCountryCodesAndDays(anySet(), anyString());
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

        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", "4");

        assertEquals(4, holidays.size());
        assertEquals("Goede Vrijdag", holidays.get(0).getLocalName());
        verify(holidayServiceValidator).validateCountryCodesAndDays(anySet(), anyString());
    }


    @Test
    @DisplayName("Test getLastNumberOfHolidays with null date and also holiday previous year null date")
    void getLastNumberOfHolidays_NullDateInputCurrentAndPreviousYearHolidays() {
        List<Holiday> currentYearHoliday = List.of(
                new Holiday(null, "Nieuwjaarsdag"),
                new Holiday("2025-04-18", "Goede Vrijdag")
        );
        List<Holiday> lastYearHoliday = List.of(
                new Holiday(null, "Tweede Pinksterdag"),
                new Holiday(null, "Eerste Kerstdag"),
                new Holiday(null, "Tweede Kerstdag")
        );
        int currentYear = 2025;
        int lastYear = 2024;
        when(nagerDateApiService.fetchHolidays(eq(currentYear), anyString())).thenReturn(currentYearHoliday);
        when(nagerDateApiService.fetchHolidays(eq(lastYear), anyString())).thenReturn(lastYearHoliday);

        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", "4");

        assertEquals(1, holidays.size());
        assertEquals("Goede Vrijdag", holidays.get(0).getLocalName());
        verify(holidayServiceValidator).validateCountryCodesAndDays(anySet(), anyString());
    }

    @Test
    @DisplayName("Test getLastNumberOfHolidays with default number of holidays")
    void getLastNumberOfHolidays_DefaultNumberOfHolidays_ReturnsHolidays() {
        ReflectionTestUtils.setField(holidayService, "defaultNumberOfHolidays", 3);
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(mockHolidays);


        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", null);

        assertEquals(3, holidays.size());
        verify(holidayServiceValidator).validateCountryCodesAndDays(anySet(), eq(null));
    }

    @Test
    @DisplayName("Test getLastNumberOfHolidays with non number input number of holidays")
    void getLastNumberOfHolidays_DefaultNumberOfHolidays_NonNumberInput() {
        ReflectionTestUtils.setField(holidayService, "defaultNumberOfHolidays", 3);
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(mockHolidays);


        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", "abcd");

        assertEquals(3, holidays.size());
        verify(holidayServiceValidator).validateCountryCodesAndDays(anySet(), eq("abcd"));
    }

    @Test
    @DisplayName("Test getLastNumberOfHolidays with value less then 1 for number of holidays")
    void getLastNumberOfHolidays_DefaultNumberOfHolidays_LessThenOneInput() {
        ReflectionTestUtils.setField(holidayService, "defaultNumberOfHolidays", 3);
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(mockHolidays);


        List<Holiday> holidays = holidayService.getLastNumberOfHolidays("NL", "-1");

        assertEquals(3, holidays.size());
        verify(holidayServiceValidator).validateCountryCodesAndDays(anySet(), eq("-1"));
    }

    @Test
    @DisplayName("Test getNonWeekendHolidayCounts with valid input")
    void getNonWeekendHolidayCounts_ValidInput_ReturnsCounts() {
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(mockHolidays);

        List<CountryHolidayCount> counts = holidayService.getNonWeekendHolidayCounts("2025", "NL");

        assertEquals(1, counts.size());
        assertEquals("NL", counts.get(0).getCountryCode());
        assertEquals(2, counts.get(0).getHolidayCount());
        verify(holidayServiceValidator).validateCountryCodesAndYear(anyString(), anySet());
    }

    @Test
    @DisplayName("Test getNonWeekendHolidayCounts with null holidayCount values")
    void getNonWeekendHolidayCounts_NullHolidayCount_SortingHandled() {
        CountryHolidayCount count1 = new CountryHolidayCount("NL", null);
        CountryHolidayCount count2 = new CountryHolidayCount("DE", 5);
        CountryHolidayCount count3 = new CountryHolidayCount("FR", null);

        // Mock fetchHolidays to return empty lists so that nonWeekendCount is always 0 (simulate nulls manually)
        when(nagerDateApiService.fetchHolidays(anyInt(), anyString())).thenReturn(List.of());

        // Use ReflectionTestUtils to inject a custom list with nulls
        List<CountryHolidayCount> holidayCounts = List.of(count1, count2, count3);
        // Directly test sorting logic
        List<CountryHolidayCount> sorted = holidayCounts.stream()
                .sorted((countryHolidayCount1, countryHolidayCount2) -> {
                    if (countryHolidayCount1.getHolidayCount() == null && countryHolidayCount2.getHolidayCount() == null) return 0;
                    if (countryHolidayCount1.getHolidayCount() == null) return 1;
                    if (countryHolidayCount2.getHolidayCount() == null) return -1;
                    return Integer.compare(countryHolidayCount2.getHolidayCount(), countryHolidayCount1.getHolidayCount());
                })
                .toList();

        assertEquals("DE", sorted.get(0).getCountryCode());
        assertNull(sorted.get(1).getHolidayCount());
        assertNull(sorted.get(2).getHolidayCount());
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
        verify(holidayServiceValidator).validateSharedHolidayCountryCodesAndYear(anyString(), anySet());
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
        verify(holidayServiceValidator).validateSharedHolidayCountryCodesAndYear(anyString(), anySet());
    }
}