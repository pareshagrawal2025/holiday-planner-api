package com.example.holidayplanner.model;

import lombok.Getter;
import lombok.Setter;

// Model class used in Nager API response representing a country with its code and name
@Getter
@Setter
public class AvailableCountry {
    private String countryCode;
    private String name;
}