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
import com.example.holidayplanner.validation.InputParameterValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayService {

    @Value("${default.number.of.holidays.to-return:3}")
    private int numberOfHolidays;


    private final NagerDateApiService nagerDateApiService;
    private final InputParameterValidator inputParameterValidator;
    private final  ObjectMapper objectMapper;

    private List<Holiday> fetchHolidays(int year, String countryCode) {
        return nagerDateApiService.fetchHolidays(year, countryCode.toUpperCase());
    }

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
                .filter(holiday -> LocalDate.parse(holiday.getDate()).isBefore(today.plusDays(1)))
                .sorted((holiday1, holiday2) -> LocalDate.parse(holiday2.getDate()).compareTo(LocalDate.parse(holiday1.getDate())))
                .limit(numberOfHolidaysRequired)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Holiday> getLastNumberOfHolidays(String countryCode, Integer inputNumberOfHolidays) {
        if (inputNumberOfHolidays == null || inputNumberOfHolidays <= 0) {
            inputNumberOfHolidays = numberOfHolidays;
        }
        inputParameterValidator.validateCountryCodesAndDays(new HashSet<>(Collections.singletonList(countryCode.toUpperCase())), inputNumberOfHolidays);
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        // Fetch holidays for current and previous year
        List<Holiday> currentYearAllHolidays = new ArrayList<>(fetchHolidays(currentYear, countryCode));
        List<Holiday> holidaysToReturn = getLastGivenNumberOfHolidays(currentYearAllHolidays, today, inputNumberOfHolidays);
        if (holidaysToReturn.size() < inputNumberOfHolidays) {
            // Fetch holidays for previous year
            List<Holiday> lastYearAllHolidays = fetchHolidays(currentYear - 1, countryCode);
            int remainingHolidays = inputNumberOfHolidays - holidaysToReturn.size();
            holidaysToReturn.addAll(getLastGivenNumberOfHolidays(lastYearAllHolidays, today, remainingHolidays));
        }
        log.info("last {} holidays for country code '{}' are: {}", inputNumberOfHolidays, countryCode.toUpperCase(), getJsonString(holidaysToReturn, Holiday.class));
        return holidaysToReturn;
    }

    public List<CountryHolidayCount> getNonWeekendHolidayCounts(int year, String countryCodes) {
        HashSet<String> countryCodesSet = new HashSet<>(List.of(countryCodes.toUpperCase().split(",")));
        inputParameterValidator.validateCountryCodesAndYear(year, countryCodesSet);
        List<CountryHolidayCount> holidayCounts = new ArrayList<>();

        for (String countryCode : countryCodesSet) {
            List<Holiday> holidays = fetchHolidays(year, countryCode);
            long nonWeekendCount = holidays.stream()
                    .map(holiday -> LocalDate.parse(holiday.getDate()))
                    .filter(date -> date.getDayOfWeek().getValue() <= 5) // Monday to Friday
                    .count();
            holidayCounts.add(new CountryHolidayCount(countryCode.toUpperCase(), (int) nonWeekendCount));
        }
        // Sort in descending order by holiday count
        List<CountryHolidayCount> countryHolidayCounts = holidayCounts.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getHolidayCount(), c1.getHolidayCount()))
                .toList();
        log.info("non weekend holiday counts for country codes '{}', are: {}", String.join(",", countryCodesSet)
                ,getJsonString(countryHolidayCounts, CountryHolidayCount.class));
        return countryHolidayCounts;
    }

    public List<SharedHoliday> getSharedHolidays(String year, String countryCode1, String countryCode2) {
        inputParameterValidator.validateSharedHolidayCountryCodesAndYear(year, new HashSet<>(List.of(countryCode1.toUpperCase(), countryCode2.toUpperCase())));
        List<Holiday> holidays1 = fetchHolidays(Integer.parseInt(year), countryCode1);
        List<Holiday> holidays2 = fetchHolidays(Integer.parseInt(year), countryCode2);

        Map<String, String> holidayMap1 = new HashMap<>();
        for (Holiday holiday : holidays1) {
            holidayMap1.putIfAbsent(holiday.getDate(), holiday.getLocalName());
        }
        List<SharedHoliday> sharedHolidays = holidays2.stream()
                .filter(holiday -> holidayMap1.containsKey(holiday.getDate()))
                .map(holiday -> new SharedHoliday(holiday.getDate(), holidayMap1.get(holiday.getDate()), holiday.getLocalName()))
                .sorted(Comparator.comparing(SharedHoliday::getDate))
                .toList();
        log.info("shared holiday for country code '{}' and '{}' are: {}", countryCode1.toUpperCase(), countryCode2.toUpperCase()
                ,getJsonString(sharedHolidays, SharedHoliday.class));
        return sharedHolidays;
    }
}