package com.example.holidayplanner.service.contract;

import java.util.List;

import com.example.holidayplanner.model.CountryHolidayCount;
import com.example.holidayplanner.model.Holiday;
import com.example.holidayplanner.model.SharedHoliday;

// Service contract interface defining methods for holiday-related operations supported by HolidayService
public interface HolidayServiceContract {

    List<Holiday> getLastNumberOfHolidays(String countryCode, String inputNumberOfHolidays);
    List<CountryHolidayCount> getNonWeekendHolidayCounts(String year, String countryCodes);
    List<SharedHoliday> getSharedHolidays(String year, String countryCode1, String countryCode2);
}
