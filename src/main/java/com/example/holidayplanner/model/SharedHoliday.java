package com.example.holidayplanner.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Represents a holiday celebrated in two countries")
public class SharedHoliday {
    @Schema(description = "Date of the holiday in YYYY-MM-DD format", example = "2025-01-01")
    private String date;

    @Schema(description = "Local name of the holiday in the first country", example = "New Year's Day")
    private String localNameCountry1;

    @Schema(description = "Local name of the holiday in the second country", example = "New Year")
    private String localNameCountry2;

    public SharedHoliday(String date, String localNameCountry1, String localNameCountry2) {
        this.date = date;
        this.localNameCountry1 = localNameCountry1;
        this.localNameCountry2 = localNameCountry2;
    }

}