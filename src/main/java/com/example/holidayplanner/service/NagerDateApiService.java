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
import com.example.holidayplanner.model.Holiday;

// This class would contain methods to interact with the Nager.Date API
// For example, fetching public holidays for a given country and year
@Component
@RequiredArgsConstructor
public class NagerDateApiService {

    @Value("${nager.date.api.url:https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}}")
    private String nagerDateApi;
    @Value("${nagar.available.countries.api.url:https://date.nager.at/api/v3/AvailableCountries}")
    private String availableCountriesApi;

    private final RestClient restClient;

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
