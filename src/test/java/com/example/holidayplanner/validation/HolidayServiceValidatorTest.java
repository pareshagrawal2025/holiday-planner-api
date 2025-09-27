package com.example.holidayplanner.validation;

import com.example.holidayplanner.exception.InvalidParameterException;
import com.example.holidayplanner.model.AvailableCountry;
import com.example.holidayplanner.service.NagerDateApiService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HolidayServiceValidatorTest {

    @Mock
    private NagerDateApiService nagerDateApiService;

    @InjectMocks
    private HolidayServiceValidator holidayServiceValidator;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(holidayServiceValidator, "maxHolidayDays", 12);
        ReflectionTestUtils.setField(holidayServiceValidator, "minHolidaySupportedYear", 1975);
        ReflectionTestUtils.setField(holidayServiceValidator, "maxHolidaySupportedYear", 2075);
        ReflectionTestUtils.setField(holidayServiceValidator, "nagerDateApiService", nagerDateApiService);
    }

    @AfterEach
    void releaseMocks() throws Exception {
        closeable.close();
    }


    @Test
    @DisplayName("Valid country codes and days should not throw an exception")
    void validateCountryCodesAndDays_ValidInput_NoExceptionThrown() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL", "FR"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createAvailableCountries());

        assertDoesNotThrow(() -> holidayServiceValidator.validateCountryCodesAndDays(inputCountryCodes, "5"));
        verify(nagerDateApiService).getAvailableCountries();
    }

    @Test
    @DisplayName("Invalid country code should throw InvalidParameterException")
    void validateCountryCodesAndDays_InvalidCountryCode_ThrowsException() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("XX"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createAvailableCountries());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () ->
                holidayServiceValidator.validateCountryCodesAndDays(inputCountryCodes, "5"));
        assertTrue(exception.getMessage().contains("non ISO 3166-1 alpha-2 compliant country code(s)"));
    }

    @Test
    @DisplayName("Exceeding maximum days should throw InvalidParameterException")
    void validateCountryCodesAndDays_ExceedsMaxDays_ThrowsException() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createNl());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () ->
                holidayServiceValidator.validateCountryCodesAndDays(inputCountryCodes, "20"));
        assertTrue(exception.getMessage().contains("non-supported numberOfHolidays"));
    }

    @Test
    @DisplayName("Validate non number input throw InvalidParameterException")
    void validateCountryCodesAndDays_ValidateNonNumber_ThrowsException() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createNl());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () ->
                holidayServiceValidator.validateCountryCodesAndDays(inputCountryCodes, "abc"));
        assertTrue(exception.getMessage().contains("input number of holiday 'abc' is not a valid number"));
    }

    @Test
    @DisplayName("Validate empty number input throw InvalidParameterException")
    void validateCountryCodesAndDays_ValidateEmptyNumber_ThrowsException() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createNl());

        assertDoesNotThrow(() -> holidayServiceValidator.validateCountryCodesAndDays(inputCountryCodes, "5"));
        verify(nagerDateApiService).getAvailableCountries();
    }

    @Test
    @DisplayName("Valid country codes and year should not throw an exception")
    void validateCountryCodesAndYear_ValidInput_NoExceptionThrown() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createNl());

        assertDoesNotThrow(() -> holidayServiceValidator.validateCountryCodesAndYear("2025", inputCountryCodes));
        verify(nagerDateApiService).getAvailableCountries();
    }

    @Test
    @DisplayName("Valid, Unsupported and Invalid country codes should throw an exception")
    void validateCountryCodesAndYear_ValidUnsupportedAndInvalidInputCode_ExceptionThrown() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL","XX", "AA", "FR", "BD", "PK", "ABED"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createAvailableCountries());

        Assertions.assertThrowsExactly(InvalidParameterException.class, () ->
                holidayServiceValidator.validateCountryCodesAndYear("2025", inputCountryCodes));
        verify(nagerDateApiService).getAvailableCountries();
    }

    @Test
    @DisplayName("Valid country codes and invalid year format throw an exception")
    void validateSharedHolidayCountryCodesAndYear_InvalidYearFormat_ExceptionThrown() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL", "FR"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createAvailableCountries());

        Assertions.assertThrowsExactly(InvalidParameterException.class, () ->
                holidayServiceValidator.validateSharedHolidayCountryCodesAndYear("abcd", inputCountryCodes));
        verify(nagerDateApiService).getAvailableCountries();
    }

    @Test
    @DisplayName("Valid country codes and null year throw an exception")
    void validateSharedHolidayCountryCodesAndYear_NullYear_ExceptionThrown() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL", "FR"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createAvailableCountries());

        Assertions.assertThrowsExactly(InvalidParameterException.class, () ->
                holidayServiceValidator.validateSharedHolidayCountryCodesAndYear(null, inputCountryCodes));
        verify(nagerDateApiService).getAvailableCountries();
    }

    @Test
    @DisplayName("Invalid year 1900 should throw InvalidParameterException")
    void validateCountryCodesAndYear_InvalidYear_ThrowsException() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createNl());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () ->
                holidayServiceValidator.validateCountryCodesAndYear("1900", inputCountryCodes));
        assertTrue(exception.getMessage().contains("non-supported year"));
    }

    @Test
    @DisplayName("Valid shared country codes and year should not throw an exception")
    void validateSharedHolidayCountryCodesAndYear_ValidInput_NoExceptionThrown() {
        Set<String> inputCountryCodes = new HashSet<>(Set.of("NL", "FR"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createAvailableCountries());

        assertDoesNotThrow(() -> holidayServiceValidator.validateSharedHolidayCountryCodesAndYear("2025", inputCountryCodes));
        verify(nagerDateApiService).getAvailableCountries();
    }

    @Test
    @DisplayName("Duplicate country code for validateSharedHolidayCountryCodesAndYear should throw InvalidParameterException")
    void validateSharedHolidayCountryCodesAndYear_SameCountryCodes_ThrowsException() {
        Set<String> inputCountryCodes = new HashSet<>(List.of("NL", "NL"));
        when(nagerDateApiService.getAvailableCountries()).thenReturn(createNl());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () ->
                holidayServiceValidator.validateSharedHolidayCountryCodesAndYear("2025", inputCountryCodes));
        assertTrue(exception.getMessage().contains("two different codes must be provided"));
    }

    private Set<AvailableCountry> createNl(){
        Set<AvailableCountry> countries = new HashSet<>();
        AvailableCountry nl = new AvailableCountry();
        nl.setCountryCode("NL");
        nl.setName("Netherlands");
        countries.add(nl);
        return countries;
    }

    private Set<AvailableCountry> createAvailableCountries() {
        Set<AvailableCountry> countries = new HashSet<>();
        AvailableCountry nl = new AvailableCountry();
        nl.setCountryCode("NL");
        nl.setName("Netherlands");
        countries.add(nl);
        AvailableCountry fr = new AvailableCountry();
        fr.setCountryCode("FR");
        fr.setName("France");
        countries.add(fr);
        return countries;
    }
}