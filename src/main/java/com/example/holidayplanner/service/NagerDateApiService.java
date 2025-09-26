package com.example.holidayplanner.service;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.example.holidayplanner.exception.InvalidParameterException;
import com.example.holidayplanner.model.AvailableCountry;
import com.example.holidayplanner.generated.model.Holiday;

/**
 * This class contains methods to interact with the Nager Date API.
 * <p>
 * For example, fetching supported country codes and public holidays for a given country and year.
 */
@Component
@RequiredArgsConstructor
public class NagerDateApiService {

    @Value("${nager.date.api.url:https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}}")
    private String nagerDateApi;
    @Value("${nagar.available.countries.api.url:https://date.nager.at/api/v3/AvailableCountries}")
    private String availableCountriesApi;

    private final RestClient restClient;

    /**
     * Fetch all supported country codes from Nager Date API and cache the result as availableCountries.
     *
     * @return Set of available countries supported by Nager Date API
     */
    @Cacheable("availableCountries")
    public Set<AvailableCountry> getAvailableCountries() {
        try {

            AvailableCountry[] countriesArray = restClient.get()
                    .uri(availableCountriesApi)
                    .retrieve()
                    .body(AvailableCountry[].class);
            return countriesArray != null
                    ? Arrays.stream(countriesArray).collect(Collectors.toSet())
                    : Collections.emptySet();
        } catch (HttpClientErrorException e) {
            throw new InvalidParameterException("No available country found from Nager Date API");
        }
    }

    /**
     * Fetch holidays for given year and country code from Nager Date API and cache the result
     * in holidays as holidays_year_countryCode.
     *
     * @param year Year for which holidays are to be fetched
     * @param countryCode Country code for which holidays are to be fetched
     * @return List of holidays for the given year and country code
     */
    @Cacheable(value = "holidays", key = "#year + '_' + #countryCode")
    public List<Holiday> fetchHolidays(int year, String countryCode) {
        String url = nagerDateApi.replace("{year}", String.valueOf(year)).replace("{countryCode}", countryCode);
        try {
            Holiday[] holidays = restClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status == HttpStatus.NOT_FOUND, (req, res) -> {
                        throw new InvalidParameterException("No holidays found for country: " + countryCode + " in year: " + year);
                    })
                    .body(Holiday[].class);

            return holidays != null ? Arrays.asList(holidays) : Collections.emptyList();
        } catch (HttpClientErrorException e) {
            throw new InvalidParameterException("No holidays found for country: " + countryCode + " in year: " + year);
        }

    }
}
