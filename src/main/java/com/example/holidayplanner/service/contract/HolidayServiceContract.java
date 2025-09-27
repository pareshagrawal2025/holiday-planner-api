package com.example.holidayplanner.service.contract;

import java.util.List;

import com.example.holidayplanner.generated.model.CountryHolidayCount;
import com.example.holidayplanner.generated.model.Holiday;
import com.example.holidayplanner.generated.model.SharedHoliday;

/**
 * Service contract interface defining methods for holiday-related operations supported by HolidayService.
 * <p>
 * This interface provides methods to fetch holidays for the current and previous year,
 * retrieve non-weekend holiday counts for given countries and year, and find shared holidays
 * between two countries for a specific year.
 */
public interface HolidayServiceContract {

    /**
     * Fetch holidays for current and previous year to get last N holidays.
     * <p>
     * Retrieves the last N holidays for the specified country code by combining holidays
     * from the current and previous year.
     *
     * @param countryCode ISO country code for which holidays are to be fetched
     * @param inputNumberOfHolidays Number of holidays to return
     * @return List of Holiday objects representing the last N holidays
     */
    List<Holiday> getLastNumberOfHolidays(String countryCode, String inputNumberOfHolidays);

    /**
     * Fetch holidays for given year and comma separated country codes and total count of non-weekend holidays.
     * <p>
     * Retrieves the count of non-weekend holidays for each specified country code in the given year.
     *
     * @param year Year for which holiday counts are to be fetched
     * @param countryCodes Comma-separated ISO country codes
     * @return List of CountryHolidayCount objects containing non-weekend holiday counts per country
     */
    List<CountryHolidayCount> getNonWeekendHolidayCounts(String year, String countryCodes);

    /**
     * Fetch holidays for given year and two country codes and find shared holidays.
     * <p>
     * Finds holidays that are shared between two countries for the specified year.
     *
     * @param year Year for which shared holidays are to be found
     * @param countryCode1 First ISO country code
     * @param countryCode2 Second ISO country code
     * @return List of SharedHoliday objects representing holidays common to both countries
     */
    List<SharedHoliday> getSharedHolidays(String year, String countryCode1, String countryCode2);
}
