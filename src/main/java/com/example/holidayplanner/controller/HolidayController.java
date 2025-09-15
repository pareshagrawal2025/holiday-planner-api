package com.example.holidayplanner.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.holidayplanner.contract.HolidayPlannerApi;
import com.example.holidayplanner.model.CountryHolidayCount;
import com.example.holidayplanner.model.Holiday;
import com.example.holidayplanner.model.SharedHoliday;
import com.example.holidayplanner.service.HolidayService;


@RestController
@RequiredArgsConstructor
public class HolidayController implements HolidayPlannerApi {

    private final HolidayService holidayService;

    @Override
    public ResponseEntity<List<Holiday>> getLastNumberOfHolidays(String countryCode, String numberOfHolidays) {
        return ResponseEntity.ok(holidayService.getLastNumberOfHolidays(countryCode, numberOfHolidays));
    }

    @Override
    public ResponseEntity<List<CountryHolidayCount>> getNonWeekendHolidayCounts(String year, String countryCodes) {
        return ResponseEntity.ok(holidayService.getNonWeekendHolidayCounts(year, countryCodes));
    }

    @Override
    public ResponseEntity<List<SharedHoliday>> getSharedHolidays(String year, String countryCode1, String countryCode2) {
        return ResponseEntity.ok(holidayService.getSharedHolidays(year, countryCode1, countryCode2));

    }
}
