package com.example.holidayplanner.service;

import com.example.holidayplanner.exception.InvalidParameterException;
import com.example.holidayplanner.model.AvailableCountry;
import com.example.holidayplanner.model.Holiday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NagerDateApiServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private NagerDateApiService nagerDateApiService;

    private static final String AVAILABLE_COUNTRIES_URL = "https://date.nager.at/api/v3/AvailableCountries";
    private static final String HOLIDAYS_URL = "https://date.nager.at/api/v3/PublicHolidays/2023/NL";

    @BeforeEach
    void setUp() {
        // Set up the mock chain for restClient
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(String.class));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        ReflectionTestUtils.setField(nagerDateApiService, "nagerDateApi", HOLIDAYS_URL);
        ReflectionTestUtils.setField(nagerDateApiService, "availableCountriesApi", AVAILABLE_COUNTRIES_URL);
    }

    @Test
    @DisplayName("Should return a set of available countries when API response is successful")
    void getAvailableCountries_Success_ReturnsCountrySet() {
        // Arrange
        AvailableCountry[] countries = new AvailableCountry[]{
                createCountry("NL", "Netherlands"),
                createCountry("FR", "France")
        };
        when(responseSpec.body(AvailableCountry[].class)).thenReturn(countries);

        // Act
        Set<AvailableCountry> result = nagerDateApiService.getAvailableCountries();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getCountryCode().equals("NL")));
        assertTrue(result.stream().anyMatch(c -> c.getCountryCode().equals("FR")));
        verify(requestHeadersUriSpec).uri(AVAILABLE_COUNTRIES_URL);
        verify(responseSpec).body(AvailableCountry[].class);
    }

    @Test
    @DisplayName("Should return an empty set when API response is null")
    void getAvailableCountries_NullResponse_ReturnsEmptySet() {
        // Arrange
        when(responseSpec.body(AvailableCountry[].class)).thenReturn(null);

        // Act
        Set<AvailableCountry> result = nagerDateApiService.getAvailableCountries();

        // Assert
        assertTrue(result.isEmpty());
        verify(requestHeadersUriSpec).uri(AVAILABLE_COUNTRIES_URL);
        verify(responseSpec).body(AvailableCountry[].class);
    }

    @Test
    @DisplayName("Should throw InvalidParameterException when API returns 400 Bed Request")
    void getAvailableCountries_HttpClientError_ThrowsInvalidParameterException() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "Bad Request", null, null, StandardCharsets.UTF_8
        );
        when(responseSpec.body(AvailableCountry[].class)).thenThrow(exception);

        // Act & Assert
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,
                () -> nagerDateApiService.getAvailableCountries()
        );
        assertEquals("No available country found from Nager Date API", thrown.getMessage());
        verify(requestHeadersUriSpec).uri(AVAILABLE_COUNTRIES_URL);
    }

    @Test
    @DisplayName("Should return a list of holidays when API response is successful")
    void fetchHolidays_Success_ReturnsHolidayList() {
        // Arrange
        Holiday[] holidays = new Holiday[]{
                new Holiday("2025-01-01", "Nieuwjaarsdag"),
                new Holiday("2025-04-18", "Goede Vrijdag"),
        };
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(Holiday[].class)).thenReturn(holidays);

        // Act
        List<Holiday> result = nagerDateApiService.fetchHolidays(2025, "NL");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(h -> h.getLocalName().equals("Nieuwjaarsdag")));
        assertTrue(result.stream().anyMatch(h -> h.getLocalName().equals("Goede Vrijdag")));
        verify(requestHeadersUriSpec).uri(HOLIDAYS_URL);
        verify(responseSpec).body(Holiday[].class);
    }

    @Test
    @DisplayName("Should return an empty list when API response is null")
    void fetchHolidays_NullResponse_ReturnsEmptyList() {
        // Arrange
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(Holiday[].class)).thenReturn(null);

        // Act
        List<Holiday> result = nagerDateApiService.fetchHolidays(2023, "NL");

        // Assert
        assertTrue(result.isEmpty());
        verify(requestHeadersUriSpec).uri(HOLIDAYS_URL);
        verify(responseSpec).body(Holiday[].class);
    }

    @Test
    @DisplayName("Should throw InvalidParameterException when API returns 404 Not Found")
    void fetchHolidays_NotFoundStatus_ThrowsInvalidParameterException() {
        // Arrange
        when(responseSpec.onStatus(any(), any())).thenThrow(
                new InvalidParameterException("No holidays found for country: NL in year: 2023")
        );

        // Act & Assert
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,
                () -> nagerDateApiService.fetchHolidays(2023, "NL")
        );
        assertEquals("No holidays found for country: NL in year: 2023", thrown.getMessage());
        verify(requestHeadersUriSpec).uri(HOLIDAYS_URL);
    }

    @Test
    @DisplayName("Should throw InvalidParameterException when API returns 400 Bad Request")
    void fetchHolidays_HttpClientError_ThrowsInvalidParameterException() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.BAD_REQUEST, "Bad Request", null, null, StandardCharsets.UTF_8
        );
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(Holiday[].class)).thenThrow(exception);

        // Act & Assert
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,
                () -> nagerDateApiService.fetchHolidays(2023, "NL")
        );
        assertEquals("No holidays found for country: NL in year: 2023", thrown.getMessage());
        verify(requestHeadersUriSpec).uri(HOLIDAYS_URL);
    }


    @Test
    @DisplayName("Should throw NoResourceFoundException when API returns 500 Internal Server Error")
    void fetchHolidays_NoResourceFoundException() {
        // Arrange
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        doAnswer(invocation -> {
            throw new NoResourceFoundException(HttpMethod.GET, "/invalid/path");
        }).when(responseSpec).body(Holiday[].class);

        // Act & Assert
        NoResourceFoundException thrown = assertThrows(
                NoResourceFoundException.class,
                () -> nagerDateApiService.fetchHolidays(2023, "NL")
        );
        assertEquals("No static resource /invalid/path.", thrown.getMessage());
        verify(requestHeadersUriSpec).uri(HOLIDAYS_URL);
    }

    @Test
    @DisplayName("Should throw ResourceAccessException when API returns 500 Internal Server Error")
    void fetchHolidays_ResourceAccessException() {
        // Arrange
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        doAnswer(invocation -> {
            throw new ResourceAccessException(
                    "I/O error: Failed to connect to Nager Date API",
                    new IOException("Connection timed out")
            );
        }).when(responseSpec).body(Holiday[].class);

        // Act & Assert
        ResourceAccessException thrown = assertThrows(
                ResourceAccessException.class,
                () -> nagerDateApiService.fetchHolidays(2023, "NL")
        );
        assertEquals("I/O error: Failed to connect to Nager Date API", thrown.getMessage());
        verify(requestHeadersUriSpec).uri(HOLIDAYS_URL);
    }

    private AvailableCountry createCountry(String code, String name) {
        AvailableCountry country = new AvailableCountry();
        country.setCountryCode(code);
        country.setName(name);
        return country;
    }
}