package com.example.holidayplanner.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

// Model class used by get last number of holidays endpoint for e.g. get last 3 holiday
// representing a public holiday with its date and local name
@Setter
@Getter
@Schema(description = "Represents a public holiday")
public class Holiday {
    @Schema(description = "Date of the holiday in YYYY-MM-DD format", example = "2025-01-01")
    private String date;

    @Schema(description = "Local name of the holiday", example = "Nieuwjaarsdag")
    private String localName;

    public Holiday(String date, String localName) {
        this.date = date;
        this.localName = localName;
    }

}