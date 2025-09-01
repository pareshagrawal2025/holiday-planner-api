package com.example.holidayplanner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.holidayplanner.model.CountryHolidayCount;
import com.example.holidayplanner.model.Holiday;
import com.example.holidayplanner.model.SharedHoliday;
import com.example.holidayplanner.service.HolidayService;

// REST controller to handle holiday-related API requests with Swagger documentation
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
@Tag(name = "Holiday API", description = "API for retrieving holiday information from Nager Date API service")
public class HolidayController {

    private final HolidayService holidayService;

    @Operation(summary = "Get last given celebrated holidays for a country",
            description = "Returns the most recent provided number of holidays (default ${default.number.of.holidays.to-return:3}) max ${max.number.of.holidays.can-be-returned:12} celebrated in the specified country, including date and local name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved holidays",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Holiday.class))),
            @ApiResponse(responseCode = "400", description = "Invalid country code", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/last-number-of-holidays/{countryCode}")
    public List<Holiday> getLastNumberOfHolidays(
            @Parameter(description = "ISO 3166-1 alpha-2 country code case insensitive", example = "NL", required = true)
            @PathVariable String countryCode,
            @Parameter(description = "Number of holidays required default ${default.number.of.holidays.to-return:3} max ${max.number.of.holidays.can-be-returned:12}", example = "3")
            @RequestParam(name = "numberOfHolidays", required = false) Integer numberOfHolidays) {
        return holidayService.getLastNumberOfHolidays(countryCode, numberOfHolidays);
    }

    @Operation(summary = "Get count of non-weekend holidays",
            description = "Returns the number of public holidays not falling on weekends for each country in the given year, sorted in descending order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved holiday counts",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryHolidayCount.class))),
            @ApiResponse(responseCode = "400", description = "Invalid year or country codes", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/non-weekend/{year}")
    public List<CountryHolidayCount> getNonWeekendHolidayCounts(
            @Parameter(description = "Year for which to retrieve holidays supported value ${min.holiday.search.supported.year:1975} to ${max.holiday.search.supported.year:2075} inclusive", example = "2025", required = true)
            @PathVariable int year,
            @Parameter(description = "List of ISO 3166-1 alpha-2 country codes case insensitive comma separated", example = "NL,DE", required = true)
            @RequestParam String countryCodes) {
        return holidayService.getNonWeekendHolidayCounts(year, countryCodes);
    }

    @Operation(summary = "Get shared holidays between two countries",
            description = "Returns a deduplicated list of holidays celebrated on the same date in both countries for the given year, including local names.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved shared holidays",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SharedHoliday.class))),
            @ApiResponse(responseCode = "400", description = "Invalid year or country codes", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/shared/{year}/{countryCode1}/{countryCode2}")
    public List<SharedHoliday> getSharedHolidays(
            @Parameter(description = "Year for which to retrieve holidays supported value ${min.holiday.search.supported.year:1975} to ${max.holiday.search.supported.year:2075} inclusive", example = "2025", required = true)
            @PathVariable String year,
            @Parameter(description = "First ISO 3166-1 alpha-2 country code case insensitive", example = "NL", required = true)
            @PathVariable String countryCode1,
            @Parameter(description = "Second ISO 3166-1 alpha-2 country code case insensitive", example = "FR", required = true)
            @PathVariable String countryCode2) {
        return holidayService.getSharedHolidays(year, countryCode1, countryCode2);
    }
}