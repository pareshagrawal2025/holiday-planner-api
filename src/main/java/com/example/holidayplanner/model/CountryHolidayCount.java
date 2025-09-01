package com.example.holidayplanner.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

// Model class used by non weekend holiday counts endpoint
// representing the total count of public holidays for a country
@Setter
@Getter
@Schema(description = "Represents the count of public holidays for a country")
public class CountryHolidayCount {
    @Schema(description = "Country code (ISO 3166-1 alpha-2)", example = "NL")
    private String countryCode;

    @Schema(description = "Number of public holidays not on weekends", example = "10")
    private int holidayCount;

    public CountryHolidayCount(String countryCode, int holidayCount) {
        this.countryCode = countryCode;
        this.holidayCount = holidayCount;
    }

}