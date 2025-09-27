package com.example.holidayplanner.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.holidayplanner.generated.contract.HolidayPlannerApi;
import com.example.holidayplanner.generated.model.CountryHolidayCount;
import com.example.holidayplanner.generated.model.Holiday;
import com.example.holidayplanner.generated.model.SharedHoliday;
import com.example.holidayplanner.service.contract.HolidayServiceContract;

/**
 * REST controller for Holiday Planner API endpoints.
 * <p>
 * This controller exposes endpoints to fetch last N holidays, non-weekend holiday counts,
 * and shared holidays between countries using the HolidayServiceContract.
 */
@RestController
@RequiredArgsConstructor
public class HolidayController implements HolidayPlannerApi {

    /**
     * Service contract for holiday-related operations.
     */
    private final HolidayServiceContract holidayServiceContract;

    /**
     * Endpoint to fetch the last N holidays for a given country code.
     * <p>
     * Delegates to HolidayServiceContract to retrieve the holidays.
     *
     * @param countryCode ISO country code for which holidays are to be fetched
     * @param numberOfHolidays Number of holidays to return
     * @return ResponseEntity containing a list of Holiday objects
     */
    @Override
    public ResponseEntity<List<Holiday>> getLastNumberOfHolidays(String countryCode, String numberOfHolidays) {
        return ResponseEntity.ok(holidayServiceContract.getLastNumberOfHolidays(countryCode, numberOfHolidays));
    }

    /**
     * Endpoint to fetch non-weekend holiday counts for given year and country codes.
     * <p>
     * Delegates to HolidayServiceContract to retrieve the counts.
     *
     * @param year Year for which holiday counts are to be fetched
     * @param countryCodes Comma-separated ISO country codes
     * @return ResponseEntity containing a list of CountryHolidayCount objects
     */
    @Override
    public ResponseEntity<List<CountryHolidayCount>> getNonWeekendHolidayCounts(String year, String countryCodes) {
        return ResponseEntity.ok(holidayServiceContract.getNonWeekendHolidayCounts(year, countryCodes));
    }

    /**
     * Endpoint to fetch shared holidays for a given year and two country codes.
     * <p>
     * Delegates to HolidayServiceContract to retrieve shared holidays between both countries.
     *
     * @param year Year for which shared holidays are to be fetched
     * @param countryCode1 First ISO country code
     * @param countryCode2 Second ISO country code
     * @return ResponseEntity containing a list of SharedHoliday objects
     */
    @Override
    public ResponseEntity<List<SharedHoliday>> getSharedHolidays(String year, String countryCode1, String countryCode2) {
        return ResponseEntity.ok(holidayServiceContract.getSharedHolidays(year, countryCode1, countryCode2));

    }
}
