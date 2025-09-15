package com.example.holidayplanner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.example.holidayplanner.model.CountryHolidayCount;
import com.example.holidayplanner.model.Holiday;
import com.example.holidayplanner.model.SharedHoliday;
import com.example.holidayplanner.service.contract.HolidayServiceContract;
import com.example.holidayplanner.validation.InputParameterValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// This service class contains business logic for all holiday API related operations
@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService implements HolidayServiceContract {

    @Value("${default.number.of.holidays.to-return:3}")
    private int defaultNumberOfHolidays;


    private final NagerDateApiService nagerDateApiService;
    private final InputParameterValidator inputParameterValidator;
    private final  ObjectMapper objectMapper;

    // Fetch holidays for given year and country code from Nager Date API service
    private List<Holiday> fetchHolidays(int year, String countryCode) {
        return nagerDateApiService.fetchHolidays(year, countryCode.toUpperCase());
    }

    // Convert list of objects to JSON string for logging purpose
    public String getJsonString(List<?> listOfObjects, Class<?> classType) {
        StringBuilder stringBuilder = new StringBuilder();
            for (Object inputObject : listOfObjects) {
            try {
                stringBuilder.append(objectMapper.writeValueAsString(inputObject));
            } catch (JsonProcessingException e) {
                log.warn("Failed to convert {} to json error: {}", classType, e.getMessage());
            }
        }
        return stringBuilder.toString();
    }

    // Sort by date (descending) and filter past holidays
    private List<Holiday> getLastGivenNumberOfHolidays(List<Holiday> holidays, LocalDate today, int numberOfHolidaysRequired) {
        return holidays.stream()
                .filter(holiday -> holiday.getDate() != null && LocalDate.parse(holiday.getDate()).isBefore(today.plusDays(1)))
                .sorted((holiday1, holiday2) -> {
                    if (holiday2.getDate() == null && holiday1.getDate() == null) return 0;
                    if (holiday2.getDate() == null) return 1;
                    if (holiday1.getDate() == null) return -1;
                    return LocalDate.parse(holiday2.getDate()).compareTo(LocalDate.parse(holiday1.getDate()));
                })
                .limit(numberOfHolidaysRequired)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Parse input number of holidays, if invalid or less than 1 return default number of holidays
    private int getInputNumberOfHolidays(String inputNumberOfHolidaysStr) {
        int inputNumberOfHolidays = defaultNumberOfHolidays;
        if (inputNumberOfHolidaysStr != null) {
            try {
                inputNumberOfHolidays = Integer.parseInt(inputNumberOfHolidaysStr);
                if (inputNumberOfHolidays < 1) {
                    inputNumberOfHolidays = defaultNumberOfHolidays;
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse input number of holidays '{}', using default: {}", inputNumberOfHolidaysStr, defaultNumberOfHolidays);
            }
        }
        return inputNumberOfHolidays;
    }

    // Fetch holidays for current and previous year to get last N holidays
    @Override
    public List<Holiday> getLastNumberOfHolidays(String countryCode, String inputNumberOfHolidaysStr) {
        inputParameterValidator.validateCountryCodesAndDays(new HashSet<>(Collections.singletonList(countryCode.toUpperCase())), inputNumberOfHolidaysStr);
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int inputNumberOfHolidays = getInputNumberOfHolidays(inputNumberOfHolidaysStr);
        // Fetch holidays for current year
        List<Holiday> currentYearAllHolidays = new ArrayList<>(fetchHolidays(currentYear, countryCode));
        List<Holiday> holidaysToReturn = getLastGivenNumberOfHolidays(currentYearAllHolidays, today, inputNumberOfHolidays);
        // If requested count of holidays in current year is lower than input Number of holidays, fetch from previous year
        if (holidaysToReturn.size() < inputNumberOfHolidays) {
            // Fetch holidays for previous year
            List<Holiday> lastYearAllHolidays = fetchHolidays(currentYear - 1, countryCode);
            int remainingHolidays = inputNumberOfHolidays - holidaysToReturn.size();
            holidaysToReturn.addAll(getLastGivenNumberOfHolidays(lastYearAllHolidays, today, remainingHolidays));
        }
        log.info("last {} holidays for country code '{}' are: {}", inputNumberOfHolidaysStr, countryCode.toUpperCase(), getJsonString(holidaysToReturn, Holiday.class));
        return holidaysToReturn;
    }

    // Fetch holidays for given year and comma separated country codes and count non-weekend holidays
    @Override
    public List<CountryHolidayCount> getNonWeekendHolidayCounts(String yearString, String countryCodes) {
        HashSet<String> countryCodesSet = new HashSet<>(List.of(countryCodes.toUpperCase().split(",")));
        inputParameterValidator.validateCountryCodesAndYear(yearString, countryCodesSet);
        List<CountryHolidayCount> holidayCounts = new ArrayList<>();

        // For each country code fetch holidays and count non-weekend holidays
        for (String countryCode : countryCodesSet) {
            List<Holiday> holidays = fetchHolidays(Integer.parseInt(yearString), countryCode);
            long nonWeekendCount = holidays.stream()
                    .filter(holiday ->  holiday.getDate() != null)
                    .map(holiday -> LocalDate.parse(holiday.getDate()))
                    .filter(date -> date.getDayOfWeek().getValue() <= 5) // Monday to Friday
                    .count();
            holidayCounts.add(new CountryHolidayCount(countryCode.toUpperCase(),  (int)nonWeekendCount));
        }
        // Sort in descending order by holiday count
        List<CountryHolidayCount> countryHolidayCounts = holidayCounts.stream()
                .sorted((countryHolidayCount1, countryHolidayCount2)
                        -> Integer.compare(countryHolidayCount2.getHolidayCount(), countryHolidayCount1.getHolidayCount()))
                .toList();
        log.info("non weekend holiday counts for country codes '{}', are: {}", String.join(",", countryCodesSet)
                ,getJsonString(countryHolidayCounts, CountryHolidayCount.class));
        return countryHolidayCounts;
    }

    // Fetch holidays for given year and two country codes and find shared holidays
    @Override
    public List<SharedHoliday> getSharedHolidays(String year, String countryCode1, String countryCode2) {
        inputParameterValidator.validateSharedHolidayCountryCodesAndYear(year, new HashSet<>(List.of(countryCode1.toUpperCase(), countryCode2.toUpperCase())));
        List<Holiday> holidays1 = fetchHolidays(Integer.parseInt(year), countryCode1);
        List<Holiday> holidays2 = fetchHolidays(Integer.parseInt(year), countryCode2);

        Map<String, String> holidayMap1 = new HashMap<>();
        for (Holiday holiday : holidays1) {
            holidayMap1.putIfAbsent(holiday.getDate(), holiday.getLocalName());
        }
        // Find shared holidays based on date
        List<SharedHoliday> sharedHolidays = holidays2.stream()
                .filter(holiday -> holidayMap1.containsKey(holiday.getDate()))
                .map(holiday -> new SharedHoliday(holiday.getDate(), holidayMap1.get(holiday.getDate()), holiday.getLocalName()))
                .filter(sharedHoliday -> sharedHoliday.getDate() != null)
                .sorted(Comparator.comparing(SharedHoliday::getDate))
                .toList();
        log.info("shared holiday for country code '{}' and '{}' are: {}", countryCode1.toUpperCase(), countryCode2.toUpperCase()
                ,getJsonString(sharedHolidays, SharedHoliday.class));
        return sharedHolidays;
    }
}